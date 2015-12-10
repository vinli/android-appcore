package li.vin.appcore.mortarflow.android;

import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import java.lang.ref.WeakReference;
import mortar.MortarScope;
import mortar.Scoped;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

@SuppressWarnings("unused")
public final class WindowService implements Scoped {
  public static final String SERVICE_NAME = WindowService.class.getName();

  public static WindowService getWindowService(Context context) {
    //noinspection ResourceType
    return (WindowService) context.getSystemService(SERVICE_NAME);
  }

  public static WindowService getWindowService(MortarScope scope) {
    return scope.getService(SERVICE_NAME);
  }

  public static boolean setSoftInputMode(@NonNull Context context, int softInputMode) {
    try {
      getWindowService(context).setSoftInputMode(softInputMode);
      return true;
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "failed setSoftInputMode " + softInputMode, e);
    }
    return false;
  }

  public static boolean setSoftInputMode(@NonNull View view, int softInputMode) {
    return setSoftInputMode(view.getContext(), softInputMode);
  }

  public static boolean setKeepScreenOn(@NonNull Context context, boolean keepScreenOn) {
    try {
      getWindowService(context).setKeepScreenOn(keepScreenOn);
      return true;
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "failed setKeepScreenOn " + keepScreenOn, e);
    }
    return false;
  }

  public static boolean setKeepScreenOn(@NonNull View view, boolean keepScreenOn) {
    return setKeepScreenOn(view.getContext(), keepScreenOn);
  }

  public static boolean hideSoftKeyboard(@NonNull Context context) {
    try {
      getWindowService(context).hideSoftKeyboard();
      return true;
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "failed hideSoftKeyboard", e);
    }
    return false;
  }

  public static boolean hideSoftKeyboard(@NonNull View view) {
    return hideSoftKeyboard(view.getContext());
  }

  private WeakReference<Window> windowRef = new WeakReference<>(null);
  private boolean registered;

  WindowService(@NonNull MortarFlowAppCompatActivity activity) {
    update(activity);
  }

  void update(@NonNull MortarFlowAppCompatActivity activity) {
    windowRef = new WeakReference<>(activity.getWindow());
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

  /** @see Window#setSoftInputMode(int) */
  public void setSoftInputMode(int softInputMode) {
    Window window = windowRef.get();
    if (window != null) {
      window.setSoftInputMode(softInputMode);
    }
  }

  public void setKeepScreenOn(boolean keepScreenOn) {
    Window window = windowRef.get();
    if (window != null) {
      if (keepScreenOn) {
        window.addFlags(FLAG_KEEP_SCREEN_ON);
      } else {
        window.clearFlags(FLAG_KEEP_SCREEN_ON);
      }
    }
  }

  public void hideSoftKeyboard() {
    Window window = windowRef.get();
    if (window != null) {
      IBinder winToken;
      try {
        winToken = window.getDecorView().findViewById(android.R.id.content).getWindowToken();
        InputMethodManager imm = ((InputMethodManager) window.getContext()
            .getApplicationContext()
            .getSystemService(Context.INPUT_METHOD_SERVICE));
        imm.hideSoftInputFromWindow(winToken, 0);
      } catch (Exception ignored) {
      }
    }
  }
}
