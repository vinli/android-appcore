package li.vin.appcore.mortarflow.android;

import android.content.Context;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import java.lang.ref.WeakReference;
import mortar.MortarScope;
import mortar.Scoped;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

public final class WindowService implements Scoped {
  public static final String SERVICE_NAME = WindowService.class.getName();

  public static WindowService getWindowService(Context context) {
    //noinspection ResourceType
    return (WindowService) context.getSystemService(SERVICE_NAME);
  }

  public static WindowService getWindowService(MortarScope scope) {
    return scope.getService(SERVICE_NAME);
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
