package li.vin.appcore.screenview;

import android.support.annotation.NonNull;

public interface DynamicViewListener {
  void onDynamicViewCreate(@NonNull android.view.View view);

  void onDynamicViewDestroy(@NonNull android.view.View view);
}
