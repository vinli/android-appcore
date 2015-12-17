package li.vin.appcore.screenview;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.BoolRes;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;
import mortar.ViewPresenter;

public class ScreenViewPresenter<V extends View & ScreenView> extends ViewPresenter<V> {
  private static final String TAG = ScreenViewPresenter.class.getSimpleName();

  private SparseArray<Parcelable> mainContainer;
  private Map<Integer, SparseArray<Parcelable>> dynamicStates;

  private final DynamicViewListener dynamicViewListener = new DynamicViewListener() {
    @Override
    public void onDynamicViewCreate(@NonNull View view) {
      if (view.getId() == View.NO_ID) throw new RuntimeException("cannot restore for NO_ID.");

      SparseArray<Parcelable> state = dynamicStates.get(view.getId());
      if (state != null) {
        view.restoreHierarchyState(state);
      } else {
        view.restoreHierarchyState(mainContainer);
      }

      Log.i(TAG,
          "finished restoring state for " + ScreenViewPresenter.this.getClass().getSimpleName() +
              " to view " + view.getClass().getSimpleName() + " (" + view.getResources()
              .getResourceName(view.getId()) + ")");
    }

    @Override
    public void onDynamicViewDestroy(@NonNull View view) {
      if (view.getId() == View.NO_ID) throw new RuntimeException("cannot restore for NO_ID.");

      SparseArray<Parcelable> state = new SparseArray<>();
      view.saveHierarchyState(state);
      dynamicStates.put(view.getId(), state);

      for (int i = 0; i < state.size(); i++) {
        mainContainer.put(state.keyAt(i), state.valueAt(i));
      }

      Log.i(TAG, "finished saving state for " + getClass().getSimpleName() +
          " to view " + view.getClass().getSimpleName() + " (" + view.getResources()
          .getResourceName(view.getId()) + ")");
    }
  };

  @CallSuper
  protected void onCreate(@NonNull V v) {
    DynamicViewManager dynamicViewManager = getDynamicViewManager();
    if (dynamicViewManager != null) {
      mainContainer = new SparseArray<>();
      dynamicStates = new HashMap<>();
    }
  }

  protected void onFinishInflate(@NonNull V v) {
  }

  @CallSuper
  @Nullable
  protected Bundle onProvideTransientParams(@NonNull V v, @NonNull android.view.View view,
      @Nullable Bundle params) {
    DynamicViewManager dynamicViewManager = getDynamicViewManager();
    if (dynamicViewManager != null) {
      for (int id : dynamicViewManager.getPersistentViewIds()) {
        View child = v.findViewById(id);
        if (child instanceof HandlesTransientParams) {
          Bundle b = ((HandlesTransientParams) child).onProvideTransientParams(view, params);
          if (b != null) params = b;
        }
      }
    }
    return params;
  }

  @CallSuper
  protected void onReceiveTransientParams(@NonNull V v, @Nullable Bundle params) {
    DynamicViewManager dynamicViewManager = getDynamicViewManager();
    if (dynamicViewManager instanceof ReceivesTransientParams) {
      ((ReceivesTransientParams) dynamicViewManager).onReceiveTransientParams(params);
    }
  }

  @CallSuper
  protected void onAttachedToWindow(@NonNull V v) {
    if (dynamicStates != null) {
      //noinspection ConstantConditions
      getDynamicViewManager().setDynamicViewListener(dynamicViewListener);
    }
  }

  @CallSuper
  protected void onDetachedFromWindow(@NonNull V v) {
    if (dynamicStates != null) {
      //noinspection ConstantConditions
      getDynamicViewManager().setDynamicViewListener(null);
    }
  }

  protected void onVisibilityChanged(@NonNull V v, @NonNull View changedView, int visibility) {
  }

  protected void onSizeChanged(@NonNull V v, int w, int h, int oldw, int oldh) {
  }

  @CallSuper
  protected void restoreHierarchyState(@NonNull V v, @NonNull SparseArray<Parcelable> container) {
    if (dynamicStates == null) return;
    Log.i(TAG, "begin restoreHierarchyState for " + getClass().getSimpleName());

    mainContainer = container;

    Bundle b = (Bundle) container.get(v.getId() * -1);
    if (b != null) {
      //noinspection ConstantConditions
      for (int id : getDynamicViewManager().getPersistentViewIds()) {
        SparseArray<Parcelable> ctr = b.getSparseParcelableArray(String.valueOf(id));
        if (ctr == null) continue;
        dynamicStates.put(id, ctr);
      }
    }
  }

  @CallSuper
  protected void saveHierarchyState(@NonNull V v, @NonNull SparseArray<Parcelable> container) {
    if (dynamicStates == null) return;
    Log.i(TAG, "begin saveHierarchyState for " + getClass().getSimpleName());

    Bundle b = (Bundle) container.get(v.getId() * -1);
    if (b == null) {
      container.put(v.getId() * -1, b = new Bundle());
    }

    //noinspection ConstantConditions
    for (int id : getDynamicViewManager().getPersistentViewIds()) {
      View vv = v.findViewById(id);

      if (vv == null) {
        SparseArray<Parcelable> state = dynamicStates.get(id);
        if (state != null) {
          b.putSparseParcelableArray(String.valueOf(id), state);
          for (int i = 0; i < state.size(); i++) {
            container.put(state.keyAt(i), state.valueAt(i));
          }
        }
        continue;
      }

      SparseArray<Parcelable> ctr = new SparseArray<>();
      vv.saveHierarchyState(ctr);
      b.putSparseParcelableArray(String.valueOf(id), ctr);
      for (int i = 0; i < ctr.size(); i++) {
        container.put(ctr.keyAt(i), ctr.valueAt(i));
      }
    }
  }

  protected void onSaveInstanceState(@NonNull V v, @NonNull Bundle outState) {
  }

  protected void onRestoreInstanceState(@NonNull V v, @NonNull Bundle inState) {
  }

  protected boolean onActivityResult(@NonNull V v, int requestCode, int resultCode, Intent data) {
    return false;
  }

  protected boolean onBackPressed(@NonNull V v) {
    return false;
  }

  protected boolean onOptionsItemSelected(@NonNull V v, MenuItem item) {
    return false;
  }

  protected void onPause(@NonNull V v) {
  }

  protected void onResume(@NonNull V v) {
  }

  protected void toastShort(@StringRes int messageResId) {
    toast(messageResId, Toast.LENGTH_LONG);
  }

  protected void toastShort(String message) {
    toast(message, Toast.LENGTH_LONG);
  }

  protected void toastLong(@StringRes int messageResId) {
    toast(messageResId, Toast.LENGTH_LONG);
  }

  protected void toastLong(String message) {
    toast(message, Toast.LENGTH_LONG);
  }

  private void toast(@StringRes int messageResId, int length) {
    if (!hasView()) return;
    Toast.makeText(getView().getContext(), messageResId, length).show();
  }

  private void toast(String message, int length) {
    if (!hasView()) return;
    Toast.makeText(getView().getContext(), message, length).show();
  }

  @NonNull
  protected String getString(@StringRes int resId) {
    if (!hasView()) throw new RuntimeException("has no view.");
    return getView().getResources().getString(resId);
  }

  protected boolean getBool(@BoolRes int resId) {
    if (!hasView()) throw new RuntimeException("has no view.");
    return getView().getResources().getBoolean(resId);
  }

  protected int dpToPx(int dp) {
    if (!hasView()) throw new RuntimeException("has no view.");
    return (int) (dp * (getView().getResources().getDisplayMetrics().densityDpi / 160f));
  }

  protected int pxToDp(int px) {
    if (!hasView()) throw new RuntimeException("has no view.");
    return (int) (px / (getView().getResources().getDisplayMetrics().densityDpi / 160f));
  }

  /**
   * Return a non-null {@link DynamicViewManager} instance if you would like to automatically
   * save and restore state to dynamically created views in this hierarchy, such as those managed
   * by an adapter.
   */
  @Nullable
  protected DynamicViewManager getDynamicViewManager() {
    return null;
  }
}
