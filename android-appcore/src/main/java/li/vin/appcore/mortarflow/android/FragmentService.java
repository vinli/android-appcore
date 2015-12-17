package li.vin.appcore.mortarflow.android;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import li.vin.appcore.R;
import mortar.MortarScope;
import mortar.Scoped;

@SuppressWarnings("unused")
public final class FragmentService implements Scoped {
  public static final String SERVICE_NAME = FragmentService.class.getName();

  private static int reuseFragmentIndex;

  private static final List<Integer> reusableIds = Collections.unmodifiableList(Arrays.asList( //
      R.id.reusable_fragment_container_0, //
      R.id.reusable_fragment_container_1, //
      R.id.reusable_fragment_container_2, //
      R.id.reusable_fragment_container_3, //
      R.id.reusable_fragment_container_4, //
      R.id.reusable_fragment_container_5, //
      R.id.reusable_fragment_container_6, //
      R.id.reusable_fragment_container_7, //
      R.id.reusable_fragment_container_8, //
      R.id.reusable_fragment_container_9, //
      R.id.reusable_fragment_container_10, //
      R.id.reusable_fragment_container_11, //
      R.id.reusable_fragment_container_12, //
      R.id.reusable_fragment_container_13, //
      R.id.reusable_fragment_container_14, //
      R.id.reusable_fragment_container_15 //
  ));

  public static int getFragmentContainerReusableId() {
    if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
      throw new RuntimeException("must be on ui thread.");
    }
    return reusableIds.get(reuseFragmentIndex = ((reuseFragmentIndex + 1) % reusableIds.size()));
  }

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
