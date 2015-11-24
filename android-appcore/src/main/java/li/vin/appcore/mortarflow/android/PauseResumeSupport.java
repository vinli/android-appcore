package li.vin.appcore.mortarflow.android;

import android.view.View;

public class PauseResumeSupport {

  public static void onPause(View childView) {
    if (childView instanceof HandlesPauseResume) {
      ((HandlesPauseResume) childView).onPause();
    }
  }

  public static void onResume(View childView) {
    if (childView instanceof HandlesPauseResume) {
      ((HandlesPauseResume) childView).onResume();
    }
  }

  private PauseResumeSupport() {
  }
}
