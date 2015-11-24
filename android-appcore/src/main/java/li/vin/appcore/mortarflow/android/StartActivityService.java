package li.vin.appcore.mortarflow.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.lang.ref.WeakReference;
import mortar.MortarScope;
import mortar.Scoped;

public final class StartActivityService implements Scoped {
  public static final String SERVICE_NAME = StartActivityService.class.getName();

  public static StartActivityService getStartActivityService(Context context) {
    //noinspection ResourceType
    return (StartActivityService) context.getSystemService(SERVICE_NAME);
  }

  public static StartActivityService getStartActivityService(MortarScope scope) {
    return scope.getService(SERVICE_NAME);
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
