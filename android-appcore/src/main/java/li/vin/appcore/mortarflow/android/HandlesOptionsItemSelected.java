package li.vin.appcore.mortarflow.android;

import android.view.MenuItem;

public interface HandlesOptionsItemSelected {
  /** Return true if the event was handled, false to allow normal processing. */
  boolean onOptionsItemSelected(MenuItem item);
}
