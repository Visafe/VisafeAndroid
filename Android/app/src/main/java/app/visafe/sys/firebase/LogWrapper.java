
package app.visafe.sys.firebase;

import android.util.Log;
import app.visafe.BuildConfig;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

/**
 * Wrapper for Crashlytics.  This allows unit testing of classes that contain logging and allows
 * us to cleanly disable Crashlytics in debug builds.
 * See https://stackoverflow.com/questions/16986753/how-to-disable-crashlytics-during-development
 */
public class LogWrapper {
  public static void logException(Throwable t) {
    Log.e("LogWrapper", "Error", t);
    if (BuildConfig.DEBUG) {
      return;
    }
    try {
      FirebaseCrashlytics.getInstance().recordException(t);
    } catch (IllegalStateException e) {
      // This only occurs during unit tests.
    }
  }

  public static void log(int severity, String tag, String message) {
    Log.println(severity, tag, message);
    if (BuildConfig.DEBUG) {
      return;
    }
    try {
      FirebaseCrashlytics.getInstance().log(tag + ": " + message);
    } catch (IllegalStateException e) {
      // This only occurs during unit tests.
    }
  }
}
