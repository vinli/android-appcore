package li.vin.appcore.mortarflow.android;

import android.content.Intent;

public interface HandlesActivityResult {
  /** Return true if the event was handled, false to allow normal processing. */
  boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
