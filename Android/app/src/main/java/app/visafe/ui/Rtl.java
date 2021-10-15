
package app.visafe.ui;

import android.os.Build;
import android.view.View;
import androidx.fragment.app.Fragment;

/**
 * Utility class for dealing with Right-to-Left locales.
 */
class Rtl {
  /**
   * @param fragment The UI element to analyze
   * @return True if the UI is configured to use a Right-to-Left locale.
   */
  static boolean isRtl(Fragment fragment) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
      return false;
    }
    int layoutDirection = fragment.getResources().getConfiguration().getLayoutDirection();
    return layoutDirection == View.LAYOUT_DIRECTION_RTL;
  }
}
