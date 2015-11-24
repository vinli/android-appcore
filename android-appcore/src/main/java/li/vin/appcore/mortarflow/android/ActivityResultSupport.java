package li.vin.appcore.mortarflow.android;

import android.content.Intent;
import android.view.View;

/**
 * Support for {@link HandlesActivityResult}.
 */
public class ActivityResultSupport {

  public static boolean onActivityResult(View childView, int requestCode, int resultCode,
      Intent data) {
    if (childView instanceof HandlesActivityResult) {
      if (((HandlesActivityResult) childView).onActivityResult(requestCode, resultCode, data)) {
        return true;
      }
    }
    return false;
  }

  private ActivityResultSupport() {
  }
}
