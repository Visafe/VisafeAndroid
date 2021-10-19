
package app.visafe.sys;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import app.visafe.domain.DomainVisafe;
import app.visafe.R;
import app.visafe.sys.firebase.LogWrapper;
import app.visafe.ui.settings.Untemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Static class representing on-disk storage of mutable state.  Collecting this all in one class
 * helps to reduce duplication of code using SharedPreferences, allows settings to be read and
 * written by separate components, and also helps to improve preference naming consistency.
 */
public class PersistentState {
  private static final String LOG_TAG = "PersistentState";

  public static final String APPS_KEY = "pref_apps";
  public static final String URL_KEY = "pref_server_url";

  private static final String APPROVED_KEY = "approved";
  private static final String ENABLED_KEY = "enabled";
  private static final String SERVER_KEY = "server";

  private static final String INTERNAL_STATE_NAME = "MainActivity";

  // The approval state is currently stored in a separate preferences file.
  // TODO: Unify preferences into a single file.
  private static final String APPROVAL_PREFS_NAME = "IntroState";

  private static SharedPreferences getInternalState(Context context) {
    return context.getSharedPreferences(INTERNAL_STATE_NAME, Context.MODE_PRIVATE);
  }

  private static SharedPreferences getUserPreferences(Context context) {
    return PreferenceManager.getDefaultSharedPreferences(context);
  }

  static boolean getVpnEnabled(Context context) {
    return getInternalState(context).getBoolean(ENABLED_KEY, false);
  }

  static void setVpnEnabled(Context context, boolean enabled) {
    SharedPreferences.Editor editor = getInternalState(context).edit();
    editor.putBoolean(ENABLED_KEY, enabled);
    editor.apply();
  }

  public static void syncLegacyState(Context context) {
    // Copy the domain choice into the new URL setting, if necessary.
    if (getServerUrl(context) != null) {
      // New server URL is already populated
      return;
    }

    // There is no URL setting, so read the legacy server name.
    SharedPreferences settings = getInternalState(context);
    String domain = settings.getString(SERVER_KEY, null);
    if (domain == null) {
      // Legacy setting is in the default state, so we can leave the new URL setting in the default
      // state as well.
      return;
    }

    String[] urls = context.getResources().getStringArray(R.array.urls);
    String defaultDomain = context.getResources().getString(R.string.legacy_domain0);
    String url = null;
    if (domain.equals(defaultDomain)) {
      // Common case: the domain is dns.google.com, which now corresponds to dns.google (url 0).
      url = urls[0];
    } else {
      // In practice, we expect that domain will always be cloudflare-dns.com at this point, because
      // that was the only other option in the relevant legacy versions of visafe.
      // Look for the corresponding URL among the builtin servers.
      for (String u : urls) {
        Uri parsed = Uri.parse(u);
        if (domain.equals(parsed.getHost())) {
          url = u;
          break;
        }
      }
    }

    if (url == null) {
      LogWrapper.log(Log.WARN, LOG_TAG, "Legacy domain is unrecognized");
      return;
    }
    setServerUrl(context, url);
  }

  public static void setServerUrl(Context context, String url) {
    SharedPreferences.Editor editor = getUserPreferences(context).edit();
    editor.putString(URL_KEY, url);
    editor.apply();
  }

  public static String getServerUrl(Context context) {
    String urlTemplate = getUserPreferences(context).getString(URL_KEY, null);
    if (urlTemplate == null) {
      return null;
    }
    return Untemplate.strip(urlTemplate);
  }

  private static String extractHost(String url) {
    try {
      URL parsed = new URL(url);
      return parsed.getHost();
    } catch (MalformedURLException e) {
      LogWrapper.log(Log.WARN, LOG_TAG, "URL is corrupted");
      return null;
    }
  }

  public static String url_default,id_default;
  public static @NonNull String expandUrl(Context context, @Nullable String url) {
    String userid ="";
    SharedPreferences share = context.getApplicationContext().getSharedPreferences("userID", 0);
    userid = share.getString("userID","");
    String reqString = Build.MANUFACTURER
            + " " + Build.MODEL + " " + Build.VERSION.RELEASE
            + " " + Build.VERSION_CODES.class.getFields()[android.os.Build.VERSION.SDK_INT].getName();
    if (userid == "")
    {
      if (url == null || url.isEmpty()) {
        String random_id = getDeviceId();
        id_default = random_id.toLowerCase();
        url_default = getDOH() +random_id.toLowerCase();
        SharedPreferences.Editor editor = share.edit();
        editor.putString("userID",random_id.toLowerCase());
        editor.putString("deviceName",reqString.replaceAll("\\s+","").replaceAll("[\\+\\.\\^\\-:,%&@*$;!#~=_<>?()]","").toLowerCase());
        editor.apply();
        System.out.println(url_default);
        return url_default;
      }
    }
    else {
      url_default = getDOH() + userid;
    }
    System.out.println(url_default);
    return url_default;
  }

  public static String url_expandUrl;

  // Returns a domain representing the current configured server URL, for use as a display name.
  public static String getServerName(Context context) {
    return extractHost(expandUrl(context, getServerUrl(context)));
  }

  public static String extractHostForAnalytics(Context context, String url) {
    String expanded = expandUrl(context, url);
    url_expandUrl = expanded;

    String[] urls = context.getResources().getStringArray(R.array.urls);
    if (Arrays.asList(urls).contains(expanded)) {
      return extractHost(expanded);
    }
    return InternalNames.CUSTOM_SERVER.name();
  }

  private static SharedPreferences getApprovalSettings(Context context) {
    return context.getSharedPreferences(APPROVAL_PREFS_NAME, Context.MODE_PRIVATE);
  }

  public static boolean getWelcomeApproved(Context context) {
    return getApprovalSettings(context).getBoolean(APPROVED_KEY, false);
  }

  public static void setWelcomeApproved(Context context, boolean approved) {
    SharedPreferences.Editor editor = getApprovalSettings(context).edit();
    editor.putBoolean(APPROVED_KEY, approved);
    editor.apply();
  }

  static Set<String> getExcludedPackages(Context context) {
    return getUserPreferences(context).getStringSet(APPS_KEY, new HashSet<String>());
  }

  static String error = ""; // string field
  public static String getDeviceId() {
    String result = null;
    int resCode;
    InputStream in;
    try {
      URL url = new URL(DomainVisafe.DOMAIN_GENERATE_ID);
      URLConnection urlConn = url.openConnection();

      HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
      httpsConn.setAllowUserInteraction(false);
      httpsConn.setInstanceFollowRedirects(true);
      httpsConn.setRequestMethod("GET");
      httpsConn.connect();
      resCode = httpsConn.getResponseCode();

      if (resCode == HttpURLConnection.HTTP_OK) {
        in = httpsConn.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);

        String strCurrentLine;
        try {
          strCurrentLine = br.readLine();
        } catch (IOException e) {
          strCurrentLine = "0";
        }
        System.out.println(strCurrentLine);
        JSONObject reader = null;
        String deviceId = "";
        try {
          reader = new JSONObject(strCurrentLine);
          deviceId = reader.getString("deviceId");
        } catch (JSONException e) {
          e.printStackTrace();
        }

        in.close();
        result = deviceId;
      } else {
        error += resCode;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result.toLowerCase();
  }
  public static String getDOH() {

    String result = null;
    result = DomainVisafe.DEFAULT_DOMAIN_DOH;
    int resCode;
    InputStream in;
    try {
      URL url = new URL(DomainVisafe.DOMAIN_GET_DOH);
      URLConnection urlConn = url.openConnection();

      HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
      httpsConn.setAllowUserInteraction(false);
      httpsConn.setInstanceFollowRedirects(true);
      httpsConn.setRequestMethod("GET");
      httpsConn.setConnectTimeout(3000);
      httpsConn.connect();
      resCode = httpsConn.getResponseCode();

      if (resCode == HttpURLConnection.HTTP_OK) {
        in = httpsConn.getInputStream();

        BufferedReader br = new BufferedReader(new InputStreamReader(in, "iso-8859-1"), 8);

        String strCurrentLine;
        try {
          strCurrentLine = br.readLine();
        } catch (IOException e) {
          strCurrentLine = "0";
        }
        JSONObject reader = null;
        try {
          reader = new JSONObject(strCurrentLine);
          result = reader.getString("hostname");
        } catch (JSONException e) {
          e.printStackTrace();
        }
        in.close();
      } else {
        result = DomainVisafe.DEFAULT_DOMAIN_DOH;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return "https://" + result.toLowerCase() + "/dns-query/";
  }
}
