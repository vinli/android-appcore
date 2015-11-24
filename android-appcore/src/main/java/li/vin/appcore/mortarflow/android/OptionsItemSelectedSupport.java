package li.vin.appcore.mortarflow.android;

import android.view.MenuItem;
import android.view.View;

/**
 * Support for {@link HandlesOptionsItemSelected}.
 */
public class OptionsItemSelectedSupport {

  public static boolean onOptionsItemSelected(View childView, MenuItem item) {
    if (childView instanceof HandlesOptionsItemSelected) {
      if (((HandlesOptionsItemSelected) childView).onOptionsItemSelected(item)) {
        return true;
      }
    }
    return false;
  }

  private OptionsItemSelectedSupport() {
  }
}
