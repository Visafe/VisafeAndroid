
package app.visafe.ui.settings;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import androidx.preference.DialogPreference;
import app.visafe.R;
import app.visafe.sys.PersistentState;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * A preference that opens a dialog, allowing the user to choose their preferred server from a list
 * or by entering a URL.
 */

public class ServerChooser extends DialogPreference {
  private String url;
  private String summaryTemplate;

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ServerChooser(Context context) {
    super(context);
    initialize(context);
  }

  public ServerChooser(Context context, AttributeSet attrs) {
    super(context, attrs);
    initialize(context);
  }

  public ServerChooser(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    initialize(context);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ServerChooser(Context context, AttributeSet attrs, int defStyle, int defStyleRes) {
    super(context, attrs, defStyle, defStyleRes);
    initialize(context);
  }

  public void initialize(Context context) {
    // Setting the key in code here, with the same value as in preferences.xml, allows this
    // preference to be initialized both declaratively (from XML) and imperatively (from Java).
    setKey(context.getResources().getString(R.string.server_choice_key));
    setPersistent(true);
    setDialogLayoutResource(R.layout.servers);
    summaryTemplate =
        context.getResources().getString(R.string.server_choice_summary);
    setPositiveButtonText(null);
    setNegativeButtonText(null);
  }

  @Override
  protected void onSetInitialValue(Object defaultValue) {
    String storedUrl = getPersistedString(url);
    setUrl(storedUrl != null ? storedUrl : (String) defaultValue);
  }

  @Override
  protected Object onGetDefaultValue(TypedArray a, int index) {
    return a.getString(index);
  }


  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
    persistString(url);
    updateSummary(url);
  }

  // Updates the "Currently <servername>" summary under the title.
  private void updateSummary(String url) {
    url = PersistentState.expandUrl(getContext(), url);
    String domain = null;
    try {
      URL parsed = new URL(url);
      domain = parsed.getHost();
    } catch (MalformedURLException e) {
      // Leave domain null.
    }
    if (domain != null) {
      setSummary(String.format(Locale.ROOT, summaryTemplate, domain));
    } else {
      setSummary(null);
    }
  }
}
