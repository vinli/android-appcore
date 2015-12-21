package li.vin.appcore.screenview;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;

/**
 * Lifecycle-safe proxy on top of Views that are being managed by a {@link ScreenViewPresenter}
 * and are part of the Mortar & Flow fragment replacement ecosystem.
 */
interface ScreenView<V extends View & ScreenView, VP extends ScreenViewPresenter<V>> {

  /**
   * Return true to automatically inject this view with Butter Knife before {@link #onInflate()} is
   * called.
   */
  boolean isButtery();

  /**
   * Return a presenter if this view needs one. Return null if none is needed, but should return a
   * consistent value for the lifetime of the view - i.e., the results are undefined if this method
   * returns a non-null value and then later a null one, or vice versa.
   */
  @Nullable
  VP getPresenter();

  /**
   * Called synchronously from the constructor of the view. This is the only callback guaranteed
   * to take place before the first call to {@link #getPresenter()}, so it should be used as an
   * opportunity to initialize the presenter.
   */
  void onCreate(@NonNull Context context);

  /** Lifecycle-safe proxy for {@link View#onFinishInflate()}. */
  void onInflate();

  /** Provide optional params when transitioning from this to the given view. */
  @Nullable
  Bundle onProvideParams(@NonNull android.view.View view, @Nullable Bundle params);

  /**
   * Called after inflate, but before attach to receive optional transient params from the source
   * of this View's creation. These parameters are transient; they will not be preserved across
   * restorations of state.
   */
  void onReceiveParams(@Nullable Bundle params);

  /** Lifecycle-safe proxy for {@link View#onAttachedToWindow()}. */
  void onAttach();

  /** Lifecycle-safe proxy for {@link View#onDetachedFromWindow()}. */
  void onDetach();

  /** Lifecycle-safe proxy for {@link View#onVisibilityChanged(View, int)}. */
  void onVisChange(@NonNull View changedView, int visibility);

  /** Lifecycle-safe proxy for {@link View#onSizeChanged(int, int, int, int)}. */
  void onSizeChange(int w, int h, int oldw, int oldh);

  /** Lifecycle-safe proxy for {@link View#dispatchRestoreInstanceState(SparseArray)}. */
  void dispatchRestoreState(@NonNull SparseArray<Parcelable> container);

  /** Lifecycle-safe proxy for {@link View#dispatchSaveInstanceState(SparseArray)}. */
  void dispatchSaveState(@NonNull SparseArray<Parcelable> container);

  /** Lifecycle-safe proxy for {@link View#onRestoreInstanceState(Parcelable)}. */
  void restoreState(Parcelable state);

  /** Lifecycle-safe proxy for {@link View#onSaveInstanceState()}. */
  Parcelable saveState();
}
