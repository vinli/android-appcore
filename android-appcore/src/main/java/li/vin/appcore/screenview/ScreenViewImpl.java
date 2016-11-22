package li.vin.appcore.screenview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import butterknife.ButterKnife;

class ScreenViewImpl<V extends View & ScreenView, VP extends ScreenViewPresenter<V>> {

  private boolean allowScreenViewCalls;

  void onCreateImpl(@NonNull V v, @NonNull Context context) {
    if (!v.isInEditMode()) {
      allowScreenViewCalls = true;
      v.onCreate(context);
      allowScreenViewCalls = false;
      VP presenter = getPresenter(v, true);
      if (presenter != null) presenter.onCreate(v);
    }
  }

  public void onFinishInflateImpl(@NonNull V v) {
    VP presenter = getPresenter(v, true);
    // SUPER
    if (v.isButtery()) ButterKnife.bind(v);
    allowScreenViewCalls = true;
    v.onInflate();
    allowScreenViewCalls = false;
    if (presenter != null) presenter.onFinishInflate(v);
  }

  @Nullable
  public Bundle onProvideTransientParamsImpl(@NonNull V v, @NonNull android.view.View view,
      @Nullable Bundle params) {
    VP presenter = getPresenter(v, false);
    // SUPER
    allowScreenViewCalls = true;
    Bundle b = v.onProvideParams(view, params);
    if (b != null) params = b;
    allowScreenViewCalls = false;

    if (presenter != null) {
      b = presenter.onProvideTransientParams(v, view, params);
      if (b != null) params = b;
    }
    return params;
  }

  public void onReceiveTransientParamsImpl(@NonNull V v, @Nullable Bundle params) {
    VP presenter = getPresenter(v, false);
    // SUPER
    allowScreenViewCalls = true;
    v.onReceiveParams(params);
    allowScreenViewCalls = false;

    if (presenter != null) {
      presenter.onReceiveTransientParams(v, params);
    }
  }

  public void onAttachedToWindowImpl(@NonNull V v) {
    VP presenter = getPresenter(v, true);
    // SUPER
    allowScreenViewCalls = true;
    v.onAttach();
    allowScreenViewCalls = false;
    if (presenter != null) {
      presenter.takeView(v);
      presenter.onAttachedToWindow(v);
    }
  }

  public void onDetachedFromWindowImpl(@NonNull V v) {
    VP presenter = getPresenter(v, false);
    if (presenter != null) {
      presenter.dropView(v);
      presenter.onDetachedFromWindow(v);
    }
    allowScreenViewCalls = true;
    v.onDetach();
    allowScreenViewCalls = false;
    // SUPER
  }

  protected void onVisibilityChangedImpl(@NonNull V v, @NonNull View changedView, int visibility) {
    VP presenter = getPresenter(v, false);
    // SUPER
    allowScreenViewCalls = true;
    v.onVisChange(changedView, visibility);
    allowScreenViewCalls = false;
    if (presenter != null) presenter.onVisibilityChanged(v, changedView, visibility);
  }

  public void onSizeChangedImpl(@NonNull V v, int w, int h, int oldw, int oldh) {
    VP presenter = getPresenter(v, false);
    // SUPER
    allowScreenViewCalls = true;
    v.onSizeChange(w, h, oldw, oldh);
    allowScreenViewCalls = false;
    if (presenter != null) presenter.onSizeChanged(v, w, h, oldw, oldh);
  }

  public void dispatchRestoreInstanceStateImpl(@NonNull V v,
      @NonNull SparseArray<Parcelable> container) {
    VP presenter = getPresenter(v, false);
    // SUPER
    allowScreenViewCalls = true;
    v.dispatchRestoreState(container);
    allowScreenViewCalls = false;
    if (presenter != null) presenter.restoreHierarchyState(v, container);
  }

  public void dispatchSaveInstanceStateImpl(@NonNull V v,
      @NonNull SparseArray<Parcelable> container) {
    VP presenter = getPresenter(v, false);
    // SUPER
    allowScreenViewCalls = true;
    v.dispatchSaveState(container);
    allowScreenViewCalls = false;
    if (presenter != null) presenter.saveHierarchyState(v, container);
  }

  public Parcelable onRestoreInstanceStateImpl(@NonNull V v, Parcelable state) {
    VP presenter = getPresenter(v, false);
    // SUPER
    Bundle b = (state instanceof Bundle)
        ? (Bundle) state
        : null;
    Parcelable superState = b != null
        ? b.getParcelable("___SUPER___")
        : null;
    allowScreenViewCalls = true;
    v.restoreState(superState);
    allowScreenViewCalls = false;
    if (presenter != null && b != null) {
      presenter.onRestoreInstanceState(v, b);
    }
    return superState;
  }

  public Parcelable onSaveInstanceStateImpl(@NonNull V v) {
    VP presenter = getPresenter(v, false);
    // SUPER
    allowScreenViewCalls = true;
    Parcelable superState = v.saveState();
    allowScreenViewCalls = false;
    if (presenter != null) {
      Bundle b = new Bundle();
      b.putParcelable("___SUPER___", superState);
      presenter.onSaveInstanceState(v, b);
      return b;
    }
    return superState;
  }

  public boolean onActivityResultImpl(@NonNull V v, int requestCode, int resultCode, Intent data) {
    VP presenter = getPresenter(v, false);
    return presenter != null && presenter.onActivityResult(v, requestCode, resultCode, data);
  }

  public boolean onBackPressedImpl(@NonNull V v) {
    VP presenter = getPresenter(v, false);
    return presenter != null && presenter.onBackPressed(v);
  }

  public boolean onOptionsItemSelectedImpl(@NonNull V v, MenuItem item) {
    VP presenter = getPresenter(v, false);
    return presenter != null && presenter.onOptionsItemSelected(v, item);
  }

  public void onPause(@NonNull V v) {
    VP presenter = getPresenter(v, false);
    if (presenter != null) presenter.onPause(v);
  }

  public void onResume(@NonNull V v) {
    VP presenter = getPresenter(v, false);
    if (presenter != null) presenter.onResume(v);
  }

  public void assertAllowScreenViewCalls() {
    if (!allowScreenViewCalls) throw new RuntimeException("may not call method externally.");
  }

  private VP getPresenter(@NonNull V v, boolean dropViewIfDiffers) {
    //noinspection unchecked
    VP p = (VP) v.getPresenter();
    V viewToDrop;
    if (p != null && (viewToDrop = p._v()) != null && viewToDrop != v) {
      if (dropViewIfDiffers) {
        p.dropView(viewToDrop);
        p.onDetachedFromWindow(viewToDrop);
      } else {
        return null;
      }
    }
    return p;
  }
}
