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
  private boolean attachedToWindow;

  void onCreateImpl(@NonNull V v, @NonNull Context context) {
    if (!v.isInEditMode()) {
      allowScreenViewCalls = true;
      v.onCreate(context);
      allowScreenViewCalls = false;
      VP presenter = getPresenter(v);
      if (presenter != null) presenter.onCreate(v);
    }
  }

  public void onFinishInflateImpl(@NonNull V v) {
    // SUPER
    if (v.isButtery()) ButterKnife.bind(v);
    allowScreenViewCalls = true;
    v.onInflate();
    allowScreenViewCalls = false;
    VP presenter = getPresenter(v);
    if (presenter != null) presenter.onFinishInflate(v);
  }

  @Nullable
  public Bundle onProvideTransientParamsImpl(@NonNull V v, @NonNull android.view.View view,
      @Nullable Bundle params) {
    // SUPER
    allowScreenViewCalls = true;
    Bundle b = v.onProvideParams(view, params);
    if (b != null) params = b;
    allowScreenViewCalls = false;

    VP presenter = getPresenter(v);
    if (presenter != null) {
      b = presenter.onProvideTransientParams(v, view, params);
      if (b != null) params = b;
    }
    return params;
  }

  public void onReceiveTransientParamsImpl(@NonNull V v, @Nullable Bundle params) {
    // SUPER
    allowScreenViewCalls = true;
    v.onReceiveParams(params);
    allowScreenViewCalls = false;

    VP presenter = getPresenter(v);
    if (presenter != null) {
      presenter.onReceiveTransientParams(v, params);
    }
  }

  public void onAttachedToWindowImpl(@NonNull V v) {

    if (attachedToWindow) return;
    attachedToWindow = true;

    // SUPER
    allowScreenViewCalls = true;
    v.onAttach();
    allowScreenViewCalls = false;
    VP presenter = getPresenter(v);
    if (presenter != null) {
      presenter.takeView(v);
      presenter.onAttachedToWindow(v);
    }
  }

  public void onDetachedFromWindowImpl(@NonNull V v) {
    VP presenter = getPresenter(v);
    if (presenter != null) {
      presenter.dropView(v);
      presenter.onDetachedFromWindow(v);
    }
    allowScreenViewCalls = true;
    v.onDetach();
    allowScreenViewCalls = false;
    // SUPER

    attachedToWindow = false;
  }

  protected void onVisibilityChangedImpl(@NonNull V v, @NonNull View changedView, int visibility) {
    // SUPER
    allowScreenViewCalls = true;
    v.onVisChange(changedView, visibility);
    allowScreenViewCalls = false;
    VP presenter = getPresenter(v);
    if (presenter != null) presenter.onVisibilityChanged(v, changedView, visibility);
  }

  public void onSizeChangedImpl(@NonNull V v, int w, int h, int oldw, int oldh) {
    // SUPER
    allowScreenViewCalls = true;
    v.onSizeChange(w, h, oldw, oldh);
    allowScreenViewCalls = false;
    VP presenter = getPresenter(v);
    if (presenter != null) presenter.onSizeChanged(v, w, h, oldw, oldh);
  }

  public void dispatchRestoreInstanceStateImpl(@NonNull V v,
      @NonNull SparseArray<Parcelable> container) {
    // SUPER
    allowScreenViewCalls = true;
    v.dispatchRestoreState(container);
    allowScreenViewCalls = false;
    VP presenter = getPresenter(v);
    if (presenter != null) presenter.restoreHierarchyState(v, container);
  }

  public void dispatchSaveInstanceStateImpl(@NonNull V v,
      @NonNull SparseArray<Parcelable> container) {
    // SUPER
    allowScreenViewCalls = true;
    v.dispatchSaveState(container);
    allowScreenViewCalls = false;
    VP presenter = getPresenter(v);
    if (presenter != null) presenter.saveHierarchyState(v, container);
  }

  public Parcelable onRestoreInstanceStateImpl(@NonNull V v, Parcelable state) {
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
    VP presenter = getPresenter(v);
    if (presenter != null && b != null) {
      presenter.onRestoreInstanceState(v, b);
    }
    return superState;
  }

  public Parcelable onSaveInstanceStateImpl(@NonNull V v) {
    // SUPER
    allowScreenViewCalls = true;
    Parcelable superState = v.saveState();
    allowScreenViewCalls = false;
    VP presenter = getPresenter(v);
    if (presenter != null) {
      Bundle b = new Bundle();
      b.putParcelable("___SUPER___", superState);
      presenter.onSaveInstanceState(v, b);
      return b;
    }
    return superState;
  }

  public boolean onActivityResultImpl(@NonNull V v, int requestCode, int resultCode, Intent data) {
    VP presenter = getPresenter(v);
    return presenter != null && presenter.onActivityResult(v, requestCode, resultCode, data);
  }

  public boolean onBackPressedImpl(@NonNull V v) {
    VP presenter = getPresenter(v);
    return presenter != null && presenter.onBackPressed(v);
  }

  public boolean onOptionsItemSelectedImpl(@NonNull V v, MenuItem item) {
    VP presenter = getPresenter(v);
    return presenter != null && presenter.onOptionsItemSelected(v, item);
  }

  public void onPause(@NonNull V v) {
    VP presenter = getPresenter(v);
    if (presenter != null) presenter.onPause(v);
  }

  public void onResume(@NonNull V v) {
    VP presenter = getPresenter(v);
    if (presenter != null) presenter.onResume(v);
  }

  public void assertAllowScreenViewCalls() {
    if (!allowScreenViewCalls) throw new RuntimeException("may not call method externally.");
  }

  private VP getPresenter(@NonNull V v) {
    //noinspection unchecked
    return (VP) v.getPresenter();
  }
}
