package li.vin.appcore.mortarflow;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import flow.path.Path;
import mortar.MortarScope;

/**
 * Created by christophercasey on 9/6/15.
 */
public abstract class Screen extends Path {
  @Nullable
  protected Object createDaggerComponent(Resources resources, MortarScope parentScope) {
    return null;
  }

  @NonNull
  protected String getScopeName() {
    return getClass().getName();
  }
}
