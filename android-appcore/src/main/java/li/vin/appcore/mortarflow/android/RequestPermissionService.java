package li.vin.appcore.mortarflow.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import mortar.MortarScope;
import mortar.Scoped;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.app.ActivityCompat.requestPermissions;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static java.lang.Math.min;

public final class RequestPermissionService implements Scoped {
  public static final String SERVICE_NAME = RequestPermissionService.class.getName();

  public static RequestPermissionService getRequestPermissionService(Context context) {
    //noinspection ResourceType
    return (RequestPermissionService) context.getSystemService(SERVICE_NAME);
  }

  public static RequestPermissionService getRequestPermissionService(MortarScope scope) {
    return scope.getService(SERVICE_NAME);
  }

  public interface RequestPermissionCallback {
    void requestPermissionResult(@NonNull String[] permissions, @NonNull int[] grantResults);
  }

  public interface RequestIgnoreBatteryOptimizationsCallback {
    void requestPermissionResult(boolean result);
  }

  private static int reqCodeGen;

  private static int genReqCode() {
    return (reqCodeGen = (reqCodeGen + 1) % 50);
  }

  private Set<RequestIgnoreBatteryOptimizationsCallback> optCallbacks = new HashSet<>();
  private Map<ReqKey, ReqVal> callbacks = new HashMap<>();
  private WeakReference<MortarFlowAppCompatActivity> activityRef = new WeakReference<>(null);
  private boolean registered;

  RequestPermissionService(@NonNull MortarFlowAppCompatActivity activity) {
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

  @TargetApi(23)
  public void requestIgnoreBatteryOptimizations(
      @NonNull RequestIgnoreBatteryOptimizationsCallback cb) {
    assertUiThread();

    MortarFlowAppCompatActivity activity = activityRef.get();
    if (activity == null) throw new RuntimeException("no activity.");

    if (Build.VERSION.SDK_INT < 23) {
      cb.requestPermissionResult(true);
      return;
    }

    String packageName;
    try {
      packageName = activity.getPackageName();
      PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
      if (pm.isIgnoringBatteryOptimizations(packageName)) {
        cb.requestPermissionResult(true);
        return;
      }
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "requestIgnoreBatteryOptimizations failed", e);
      cb.requestPermissionResult(false);
      return;
    }

    if (optCallbacks.add(cb) && optCallbacks.size() == 1) {
      try {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        intent.setData(Uri.parse("package:" + packageName));
        activity.startActivityForResult(intent, 66);
      } catch (Exception e) {
        Log.e(SERVICE_NAME, "requestIgnoreBatteryOptimizations failed", e);
        cb.requestPermissionResult(false);
        optCallbacks.clear();
      }
    }
  }

  public void removeRequestBatteryOptimizationCallback(
      @NonNull RequestIgnoreBatteryOptimizationsCallback cb) {
    assertUiThread();

    optCallbacks.remove(cb);
  }

  @TargetApi(23)
  void notifyActivityResult(@NonNull MortarFlowAppCompatActivity activity, int requestCode) {
    assertUiThread();

    if (requestCode != 66 || Build.VERSION.SDK_INT < 23) return;

    boolean result;
    try {
      String packageName = activity.getPackageName();
      PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
      result = pm.isIgnoringBatteryOptimizations(packageName);
    } catch (Exception e) {
      Log.e(SERVICE_NAME, "notifyActivityResult failed", e);
      result = false;
    }
    for (RequestIgnoreBatteryOptimizationsCallback cb : new HashSet<>(optCallbacks)) {
      cb.requestPermissionResult(result);
    }
    optCallbacks.clear();
  }

  public boolean needsPermission(@NonNull String[] permissions) {
    assertUiThread();

    MortarFlowAppCompatActivity activity = activityRef.get();
    if (activity == null) throw new RuntimeException("no activity.");

    for (String p : permissions) {
      if (ContextCompat.checkSelfPermission(activity, p) != PackageManager.PERMISSION_GRANTED) {
        return true;
      }
    }
    return false;
  }

  public boolean needsRationale(@NonNull String[] permissions) {
    assertUiThread();

    MortarFlowAppCompatActivity activity = activityRef.get();
    if (activity == null) throw new RuntimeException("no activity.");

    for (String p : permissions) {
      if (ActivityCompat.shouldShowRequestPermissionRationale(activity, p)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Request the given permissions. Caller must prepare for callback to trigger synchronously in
   * case permission is already granted. Must be called on the UI thread.
   */
  public void requestPermission(@NonNull String[] permissions,
      @NonNull RequestPermissionCallback callback) {
    assertUiThread();

    MortarFlowAppCompatActivity activity = activityRef.get();
    if (activity == null) throw new RuntimeException("no activity.");

    int[] results = new int[permissions.length];
    ArrayList<String> newPerms = new ArrayList<>();
    for (int i = 0; i < permissions.length; i++) {
      String p = permissions[i];
      // if already granted (or nonsense), just mark as granted
      if (p == null || TextUtils.getTrimmedLength(p) == 0 || //
          checkSelfPermission(activity, p) == PERMISSION_GRANTED) {
        results[i] = PERMISSION_GRANTED;
      } else {
        newPerms.add(p);
        // assume denied until proven granted
        results[i] = PERMISSION_DENIED;
      }
    }

    // if nothing needs to be requested, deliver result and return
    if (newPerms.isEmpty()) {
      callback.requestPermissionResult(permissions, results);
      return;
    }

    int reqCode = genReqCode();
    if (addCb(permissions, callback, reqCode, results)) {
      requestPermissions(activity, newPerms.toArray(new String[newPerms.size()]), reqCode);
    }
  }

  /**
   * Remove the given callback permissively - no errors if it isn't registered. Must be called on
   * the UI thread.
   */
  public void removeCallback(@NonNull RequestPermissionCallback callback) {
    assertUiThread();

    remCb(callback);
  }

  void notifyRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    assertUiThread();

    Set<Map.Entry<ReqKey, ReqVal>> entries = callbacks.entrySet();
    for (Iterator<Map.Entry<ReqKey, ReqVal>> it = entries.iterator(); it.hasNext(); ) {

      Map.Entry<ReqKey, ReqVal> entry = it.next();
      ReqKey key = entry.getKey();
      ReqVal val = entry.getValue();

      if (key == null || val == null || val.reqCode != requestCode) continue;

      it.remove();

      for (int i = 0; i < min(permissions.length, grantResults.length); i++) {
        int found = findPerm(key.permissions, permissions[i]);
        if (found != -1) val.results[found] = grantResults[i];
      }

      for (RequestPermissionCallback cb : val.cbs) {
        cb.requestPermissionResult(key.permissions, val.results);
      }
    }
  }

  private static int findPerm(String[] perms, String perm) {
    for (int i = 0; i < perms.length; i++) {
      if (perms[i].equals(perm)) return i;
    }
    return -1;
  }

  private boolean addCb(@NonNull String[] permissions, @NonNull RequestPermissionCallback cb,
      int reqCode, int[] results) {
    ReqKey k = new ReqKey(permissions);
    ReqVal v = callbacks.get(k);
    if (v == null) {
      callbacks.put(k, v = new ReqVal(reqCode, results));
    }
    boolean wasEmpty = v.cbs.isEmpty();
    v.cbs.add(cb);
    return wasEmpty && !v.cbs.isEmpty();
  }

  private void remCb(@NonNull RequestPermissionCallback cb) {
    for (ReqVal v : callbacks.values()) {
      v.cbs.remove(cb);
    }
  }

  private void assertUiThread() {
    if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
      throw new RuntimeException("not on UI thread.");
    }
  }

  private static class ReqKey {
    final String[] permissions;

    public ReqKey(String[] permissions) {
      this.permissions = new String[permissions.length];
      System.arraycopy(permissions, 0, this.permissions, 0, permissions.length);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      ReqKey that = (ReqKey) o;
      return Arrays.equals(permissions, that.permissions);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(permissions);
    }
  }

  private static class ReqVal {
    final Set<RequestPermissionCallback> cbs;
    final int reqCode;
    final int[] results;

    public ReqVal(int reqCode, int[] results) {
      this.cbs = new HashSet<>();
      this.reqCode = reqCode;
      this.results = new int[results.length];
      System.arraycopy(results, 0, this.results, 0, results.length);
    }
  }
}
