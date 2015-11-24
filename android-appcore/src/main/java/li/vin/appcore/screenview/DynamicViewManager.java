package li.vin.appcore.screenview;

import android.support.annotation.NonNull;

public interface DynamicViewManager {
  void setDynamicViewListener(DynamicViewListener listener);

  @NonNull
  int[] getPersistentViewIds();
}
