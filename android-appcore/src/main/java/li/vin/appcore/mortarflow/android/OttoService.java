package li.vin.appcore.mortarflow.android;

import android.content.Context;
import android.support.annotation.NonNull;
import com.squareup.otto.Bus;
import mortar.MortarScope;
import mortar.Scoped;

public final class OttoService implements Scoped {
  public static final String SERVICE_NAME = OttoService.class.getName();

  public static OttoService getOttoService(Context context) {
    //noinspection ResourceType
    return (OttoService) context.getSystemService(SERVICE_NAME);
  }

  public static OttoService getOttoService(MortarScope scope) {
    return scope.getService(SERVICE_NAME);
  }

  private final Bus bus;
  private boolean registered;

  public OttoService(@NonNull Bus bus) {
    this.bus = bus;
  }

  @Override
  public void onEnterScope(MortarScope scope) {
    if (registered) throw new IllegalStateException("Cannot double register");
    registered = true;
  }

  @Override
  public void onExitScope() {
    // Nothing to do.
  }

  public Bus getBus() {
    return bus;
  }
}
