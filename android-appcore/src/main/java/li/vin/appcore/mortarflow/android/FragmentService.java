package li.vin.appcore.mortarflow.android;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import java.lang.ref.WeakReference;
import mortar.MortarScope;
import mortar.Scoped;

@SuppressWarnings("unused")
public final class FragmentService implements Scoped {
  public static final String SERVICE_NAME = FragmentService.class.getName();

  public static FragmentService getFragmentService(Context context) {
    //noinspection ResourceType
    return (FragmentService) context.getSystemService(SERVICE_NAME);
  }

  public static FragmentService getFragmentService(MortarScope scope) {
    return scope.getService(SERVICE_NAME);
  }

  @Nullable
  public static FragmentManager getFragmentManager(@NonNull Context context) {
    try {
      return getFragmentService(context).getFragmentManager();
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "failed getFragmentManager", e);
    }
    return null;
  }

  @Nullable
  public static FragmentManager getFragmentManager(@NonNull View view) {
    return getFragmentManager(view.getContext());
  }

  private WeakReference<FragmentManager> fragmentManagerRef = new WeakReference<>(null);
  private boolean registered;

  FragmentService(@NonNull MortarFlowAppCompatActivity activity) {
    update(activity);
  }

  void update(@NonNull MortarFlowAppCompatActivity activity) {
    fragmentManagerRef = new WeakReference<>(activity.getSupportFragmentManager());
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

  @NonNull
  public FragmentManager getFragmentManager() {
    FragmentManager fragmentManager = fragmentManagerRef.get();
    if (fragmentManager == null) throw new IllegalStateException("no fragment manager.");
    return fragmentManager;
  }
}
