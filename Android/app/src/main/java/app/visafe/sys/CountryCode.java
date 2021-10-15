
package app.visafe.sys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;
import androidx.annotation.NonNull;
import app.visafe.sys.firebase.LogWrapper;
import java.lang.reflect.Method;

/**
 * Static class for getting the system's country code.
 */
public class CountryCode {
  // The SIM or CDMA country.
  private final @NonNull String deviceCountry;

  // The country claimed by the attached cell network.
  private final @NonNull String networkCountry;

  public CountryCode(Context context) {
    TelephonyManager telephonyManager =
        (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);

    deviceCountry = countryFromSim(telephonyManager);
    networkCountry = telephonyManager.getNetworkCountryIso();
  }

  public @NonNull String getDeviceCountry() {
    return deviceCountry;
  }

  public @NonNull String getNetworkCountry() {
    return networkCountry;
  }

  private static @NonNull String countryFromSim(TelephonyManager telephonyManager) {
    String simCountry = telephonyManager.getSimCountryIso();
    if (!simCountry.isEmpty()) {
      return simCountry;
    }
    // User appears to be non-GSM.  Try CDMA.
    try {
      // Get the system properties by reflection.
      @SuppressLint("PrivateApi")
      Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
      Method get = systemPropertiesClass.getMethod("get", String.class);
      String cdmaOperator = (String)get.invoke(systemPropertiesClass,
          "ro.cdma.home.operator.numeric");
      if (cdmaOperator == null || cdmaOperator.isEmpty()) {
        // System is neither GSM nor CDMA.
        return "";
      }
      String mcc = cdmaOperator.substring(0, 3);
      @SuppressLint("PrivateApi")
      Class<?> mccTableClass = Class.forName("com.android.internal.telephony.MccTable");
      Method countryCodeForMcc = mccTableClass.getMethod("countryCodeForMcc", String.class);
      return (String)countryCodeForMcc.invoke(mccTableClass, mcc);
    } catch (Exception e) {
      LogWrapper.logException(e);
      return "";
    }
  }
}
