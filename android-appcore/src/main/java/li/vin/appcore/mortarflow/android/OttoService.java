package li.vin.appcore.mortarflow.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import com.squareup.otto.Bus;
import java.util.Map;
import java.util.WeakHashMap;
import mortar.MortarScope;
import mortar.Scoped;

@SuppressWarnings("unused")
public final class OttoService implements Scoped {
  public static final String SERVICE_NAME = OttoService.class.getName();

  private static final class Holder {
    private static final Map<Object, OttoService> registrations = new WeakHashMap<>();
  }

  private static void addRegistration(@NonNull Object key, @NonNull OttoService ottoService) {
    Map<Object, OttoService> regs = Holder.registrations;
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (regs) {
      if (regs.containsKey(key)) throw new RuntimeException("already registered.");
      regs.put(key, ottoService);
    }
  }

  @NonNull
  private static OttoService getRegistration(@NonNull Object key, boolean remove) {
    Map<Object, OttoService> regs = Holder.registrations;
    OttoService ottoService;
    //noinspection SynchronizationOnLocalVariableOrMethodParameter
    synchronized (regs) {
      if (remove) {
        ottoService = regs.remove(key);
      } else {
        ottoService = regs.get(key);
      }
    }
    if (ottoService == null) throw new RuntimeException("not registered.");
    return ottoService;
  }

  private static void cleanupFailedReg(OttoService ottoService, Object receiver) {
    try {
      if (ottoService != null) ottoService.getBus().unregister(receiver);
    } catch (Exception ignored) {
    }
    try {
      getRegistration(receiver, true);
    } catch (Exception ignored) {
    }
  }

  public static OttoService getOttoService(Context context) {
    //noinspection ResourceType
    return (OttoService) context.getSystemService(SERVICE_NAME);
  }

  public static OttoService getOttoService(MortarScope scope) {
    return scope.getService(SERVICE_NAME);
  }

  /**
   * Permissively post an event - won't throw, but will log an error and return false if a valid
   * event bus is not registered to the given receiver, or if the event cannot be posted.
   */
  public static boolean postEvent(@NonNull Object receiver, @NonNull Object event) {
    try {
      getRegistration(receiver, false).getBus().post(event);
      return true;
    } catch (Exception ex) {
      Log.e(SERVICE_NAME, "postEvent failed for event " + //
          event.getClass().getSimpleName(), ex);
    }
    return false;
  }

  /**
   * Permissively post an event - won't throw, but will log an error and return false if a valid
   * event bus cannot be retrieved from the given Context, or if the event cannot be posted.
   */
  public static boolean postEvent(@NonNull Context serviceContext, @NonNull Object event) {
    try {
      getOttoService(serviceContext).getBus().post(event);
      return true;
    } catch (Exception ex) {
      Log.e(SERVICE_NAME, "postEvent failed for event " + //
          event.getClass().getSimpleName(), ex);
    }
    return false;
  }

  /** @see #postEvent(Context, Object) */
  public static boolean postEvent(@NonNull View view, @NonNull Object event) {
    return postEvent(view.getContext(), event);
  }

  /**
   * Permissively register for events - won't throw, but will log an error and return false if a
   * valid event bus cannot be retrieved from the given Context, or if the receiver cannot be
   * registered.
   */
  public static boolean register(@NonNull Context serviceContext, @NonNull Object receiver) {
    OttoService ottoService = null;
    try {
      ottoService = getOttoService(serviceContext);
      if (ottoService == null) throw new NullPointerException("null service lookup.");
      addRegistration(receiver, ottoService);
      ottoService.getBus().register(receiver);
      return true;
    } catch (Exception ex) {
      Log.e(SERVICE_NAME, "register failed for receiver " + //
          receiver.getClass().getSimpleName(), ex);
      cleanupFailedReg(ottoService, receiver);
    }
    return false;
  }

  /** @see #register(Context, Object) */
  public static boolean register(@NonNull View view, @NonNull Object receiver) {
    return register(view.getContext(), receiver);
  }

  /**
   * Permissively unregister for events - won't throw, but will log an error and return false if a
   * valid event bus cannot be retrieved from the given Context, or if the receiver cannot be
   * unregistered.
   */
  public static boolean unregister(@NonNull Object receiver) {
    OttoService ottoService = null;
    try {
      ottoService = getRegistration(receiver, true);
      ottoService.getBus().unregister(receiver);
      return true;
    } catch (Exception ex) {
      Log.e(SERVICE_NAME, "unregister failed for receiver " + //
          receiver.getClass().getSimpleName(), ex);
      cleanupFailedReg(ottoService, receiver);
    }
    return false;
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
