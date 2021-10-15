
package app.visafe.ui;

import android.content.res.AssetManager;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Map IP addresses to country codes, using a fixed-width sorted database.
 * Lookups are performed using a binary search.
 * The database requires about 5 MB of RAM, 2 MB compressed in the APK.  A carefully designed
 * tree-based representation could probably save a factor of 4.
 * Note that this class is not used by the service, so it should only contribute to RAM usage when
 * the UI is visible.
 */
class CountryMap {

  // Number of bytes used to store each country string.
  private static final int COUNTRY_SIZE = 2;

  // TODO: Reintroduce IPv6 support when visafe supports IPv6 again.
  private final byte[] db;

  CountryMap(AssetManager assetManager) throws IOException {
    db = read(assetManager.open("dbip.v4"));
  }

  private static byte[] read(InputStream input) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int n;
    byte[] temp = new byte[4096];
    while ((n = input.read(temp, 0, temp.length)) != -1) {
      buffer.write(temp, 0, n);
    }
    return buffer.toByteArray();
  }

  /**
   * Compares two arrays of equal length.  The first is an entry in a database, specified by the
   * index of its first byte.  The second is a standalone array.
   * @return The lexicographic comparison of the two arrays.
   */
  private static boolean lessEqual(byte[] db, int position, byte[] key) {
    for (int i = 0; i < key.length; ++i) {
      int ai = db[position + i] & 0xFF;
      int bi = key[i] & 0xFF;

      if (ai < bi) {
        return true;
      }
      if (ai > bi) {
        return false;
      }
    }
    return true;
  }

  String getCountryCode(InetAddress address) {
    byte[] key = address.getAddress();
    if (key.length != 4) {
      // Only IPv4 is supported
      return "ZZ";
    }
    int recordSize = key.length + COUNTRY_SIZE;
    int low = 0;
    int high = db.length / recordSize;
    while (high - low > 1) {
      int mid = (low + high) / 2;
      int position = mid * recordSize;
      if (lessEqual(db, position, key)) {
        low = mid;
      } else {
        high = mid;
      }
    }
    int position = low * recordSize + key.length;
    byte[] countryCode = Arrays.copyOfRange(db, position, position + COUNTRY_SIZE);
    return new String(countryCode, Charset.forName("UTF-8"));
  }
}
