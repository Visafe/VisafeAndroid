
package app.visafe.sys.firebase;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Locale;

/**
 * Utility class for initializing Firebase Remote Config.  Remote Configuration allows us to conduct
 * A/B tests of experimental functionality, and to enable or disable features without having to
 * release a new version of the app.
 */
public class RemoteConfig {
  public static Task<Boolean> update() {
    try {
      FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
      return config.fetchAndActivate();
    } catch (IllegalStateException e) {
      LogWrapper.logException(e);
      return Tasks.forResult(false);
    }
  }

  static String getExtraIPKey(String domain) {
    // Convert everything to lowercase for consistency.
    // The only allowed characters in a remote config key are A-Z, a-z, 0-9, and _.
    return "extra_ips_" + domain.toLowerCase(Locale.ROOT).replaceAll("\\W", "_");
  }

  // Returns any additional IPs known for this domain, as a string containing a comma-separated
  // list, or the empty string if none are known.
  public static String getExtraIPs(String domain) {
    try {
      return FirebaseRemoteConfig.getInstance().getString(getExtraIPKey(domain));
    } catch (IllegalStateException e) {
      LogWrapper.logException(e);
      return "";
    }
  }

  public static boolean getChoirEnabled() {
    try {
      return FirebaseRemoteConfig.getInstance().getBoolean("choir");
    } catch (IllegalStateException e) {
      LogWrapper.logException(e);
      return false;
    }
  }
}
