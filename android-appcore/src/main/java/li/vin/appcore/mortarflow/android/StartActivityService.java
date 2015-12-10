package li.vin.appcore.mortarflow.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import java.lang.ref.WeakReference;
import mortar.MortarScope;
import mortar.Scoped;

@SuppressWarnings("unused")
public final class StartActivityService implements Scoped {
  public static final String SERVICE_NAME = StartActivityService.class.getName();

  public static StartActivityService getStartActivityService(Context context) {
    //noinspection ResourceType
    return (StartActivityService) context.getSystemService(SERVICE_NAME);
  }

  public static StartActivityService getStartActivityService(MortarScope scope) {
    return scope.getService(SERVICE_NAME);
  }

  @Nullable
  public static MortarFlowAppCompatActivity getActivity(@NonNull Context context) {
    try {
      return getStartActivityService(context).getActivity();
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "failed getActivity", e);
    }
    return null;
  }

  @Nullable
  public static MortarFlowAppCompatActivity getActivity(@NonNull View view) {
    return getActivity(view.getContext());
  }

  public static boolean startActivityForResult(@NonNull Context context, //
      Intent intent, int requestCode, @Nullable Bundle options) {
    try {
      getStartActivityService(context).startActivityForResult(intent, requestCode, options);
      return true;
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "failed startActivityForResult " + //
          intent + " " + requestCode + " " + options, e);
    }
    return false;
  }

  public static boolean startActivityForResult(@NonNull View view, //
      Intent intent, int requestCode, @Nullable Bundle options) {
    return startActivityForResult(view.getContext(), intent, requestCode, options);
  }

  public static boolean startActivity(@NonNull Context context, //
      Intent intent, @Nullable Bundle options) {
    try {
      getStartActivityService(context).startActivity(intent, options);
      return true;
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "failed startActivity " + //
          intent + " " + options, e);
    }
    return false;
  }

  public static boolean startActivity(@NonNull View view, //
      Intent intent, @Nullable Bundle options) {
    return startActivity(view.getContext(), intent, options);
  }

  private WeakReference<MortarFlowAppCompatActivity> activityRef = new WeakReference<>(null);
  private boolean registered;

  StartActivityService(@NonNull MortarFlowAppCompatActivity activity) {
    update(activity);
  }

  void update(@NonNull MortarFlowAppCompatActivity activity) {
    activityRef = new WeakReference<>(activity);
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
  public MortarFlowAppCompatActivity getActivity() {
    MortarFlowAppCompatActivity activity = activityRef.get();
    if (activity == null) throw new RuntimeException("no activity.");
    return activity;
  }

  public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
    MortarFlowAppCompatActivity activity = activityRef.get();
    if (activity == null) throw new RuntimeException("no activity.");
    activity.startActivityForResult(intent, requestCode, options);
  }

  public void startActivity(Intent intent, @Nullable Bundle options) {
    MortarFlowAppCompatActivity activity = activityRef.get();
    if (activity == null) throw new RuntimeException("no activity.");
    activity.startActivity(intent, options);
  }
}
