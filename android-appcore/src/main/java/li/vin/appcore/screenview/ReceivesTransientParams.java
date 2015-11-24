package li.vin.appcore.screenview;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by christophercasey on 10/27/15.
 */
public interface ReceivesTransientParams {
  void onReceiveTransientParams(@Nullable Bundle params);
}
