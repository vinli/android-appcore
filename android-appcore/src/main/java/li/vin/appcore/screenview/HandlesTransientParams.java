package li.vin.appcore.screenview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by christophercasey on 10/27/15.
 */
public interface HandlesTransientParams extends ReceivesTransientParams {
  @Nullable
  Bundle onProvideTransientParams(@NonNull android.view.View view, @Nullable Bundle params);
}
