
package app.visafe.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.EditTextPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import app.visafe.R;
import app.visafe.sys.firebase.LogWrapper;
import app.visafe.sys.PersistentState;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

/**
 * UI Fragment for displaying user preferences.
 * See https://developer.android.com/guide/topics/ui/settings#Fragment
 *
 * This class contains the special logic for the app exclusion setting, which does not require a
 * custom UI.  The server selection preference requires a custom UI, so it has its own class.
 */

public class SettingsFragment extends PreferenceFragmentCompat {
  private static final String APPS_KEY = "apps";
  private String pass,newpass;
  // Acquiring the list of apps happens asynchronously.  To get the list of apps, use the
  // getAppList() function, which will wait for the enumeration to complete if necessary.
  private AppEnumerationTask enumerator = null;
  private ArrayList<AppInfo> appList = null;

  private MultiSelectListPreference appPref = null;

  /**
   * Task representing enumeration of the installed apps.  This task populates |labels| and
   * |packageNames| before completing.  Enumeration typically takes several seconds, so this task
   * ensures that enumeration starts when the user opens Settings, but only blocks the UI when the
   * user opens the Excluded Apps dialog.
   */

  private static class AppEnumerationTask extends AsyncTask<PackageManager, Void,
      ArrayList<AppInfo>> {
    @Override
    protected ArrayList<AppInfo> doInBackground(PackageManager... pms) {
      PackageManager pm = pms[0];
      List<ApplicationInfo> infos = pm.getInstalledApplications(0);
      ArrayList<AppInfo> entries = new ArrayList<>(infos.size());
      for (ApplicationInfo info : infos) {
        if ("app.visafe".equals(info.packageName)) {
          // Showing ourselves in the list of apps would be confusing and useless.
          continue;
        }
        if (pm.getLaunchIntentForPackage(info.packageName) == null) {
          // This is not an app that can actually be launched, so it's confusing to show it.
          continue;
        }
        // Calling loadLabel() can be very slow, because it needs to read into the APK.
        entries.add(new AppInfo(info.loadLabel(pm).toString(), info.packageName));
      }
      Collections.sort(entries);
      return entries;
    }
  }


  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    ArrayList<AppInfo> appList = getAppList();
    if (appList != null) {
      savedInstanceState.putParcelableArrayList(APPS_KEY, appList);
    }
  }

  public static String deviceName="";
  public NewSettingFragment newsettingFragment;
  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    Window window = getActivity().getWindow();
    window.setStatusBarColor(ContextCompat.getColor(getContext(),R.color.white));
    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    if (savedInstanceState != null) {
      // Restore the app list state.
      appList = savedInstanceState.getParcelableArrayList(APPS_KEY);
    }
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.preferences);
    appPref = (MultiSelectListPreference)findPreference(PersistentState.APPS_KEY);
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      // App exclusion relies on VpnService.Builder.addDisallowedApplication, which was added in L.
      appPref.setEnabled(false);
      appPref.setSummary(R.string.old_android);
    }

    }
  @Override
  public void onDisplayPreferenceDialog(Preference preference) {
    if (preference instanceof ServerChooser) {
      DialogFragment dialogFragment = ServerChooserFragment.newInstance(preference.getKey());
      dialogFragment.setTargetFragment(this, 0);
      dialogFragment.show(getFragmentManager(), null);
    } else {
      // This is the app exclusion dialog.
      final ArrayList<AppInfo> appList = getAppList();
      if (appList != null) {
        String[] labels = new String[appList.size()];
        String[] packageNames = new String[appList.size()];
        for (int i = 0; i < appList.size(); ++i) {
          AppInfo entry = appList.get(i);
          labels[i] = entry.label;
          packageNames[i] = entry.packageName;
        }
        appPref.setEntries(labels);
        appPref.setEntryValues(packageNames);
      }
      super.onDisplayPreferenceDialog(preference);
    }
  }

  /**
   * Update this SettingsFragment's list of installed apps for the "Excluded apps" setting.  This
   * method must be called before the fragment is displayed.
   * @param pm The global PackageManager.
   */
  @MainThread
  public void collectInstalledApps(PackageManager pm) {
    enumerator = new AppEnumerationTask();
    enumerator.execute(pm);
  }

  /**
   * Blocks until the app list is ready.
   * @return The app list, or null if it could not be generated for any reason.
   */
  @MainThread
  private ArrayList<AppInfo> getAppList() {
    if (appList != null) {
      return appList;
    }
    if (enumerator != null) {
      try {
        appList = enumerator.get();
      } catch (InterruptedException  e) {
      } catch (ExecutionException e) {
        LogWrapper.logException(e);
      }
    }
    return appList;
  }
}
