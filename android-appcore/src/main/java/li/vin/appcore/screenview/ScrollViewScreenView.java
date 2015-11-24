package li.vin.appcore.screenview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import li.vin.appcore.mortarflow.android.HandlesActivityResult;
import li.vin.appcore.mortarflow.android.HandlesBack;
import li.vin.appcore.mortarflow.android.HandlesOptionsItemSelected;
import li.vin.appcore.mortarflow.android.HandlesPauseResume;

/**
 * Created by christophercasey on 9/9/15.
 */
public abstract class ScrollViewScreenView< //
    V extends ScrollViewScreenView, VP extends ScreenViewPresenter<V>> //
    extends ScrollView //
    implements ScreenView<V, VP>, HandlesBack, HandlesActivityResult, HandlesOptionsItemSelected,
    HandlesPauseResume, HandlesTransientParams {

  @NonNull private final V _this;
  @NonNull private final ScreenViewImpl<V, VP> impl = new ScreenViewImpl<>();

  public ScrollViewScreenView(Context context) {
    super(context);
    //noinspection unchecked
    _this = (V) this;
    impl.onCreateImpl(_this, context);
  }

  public ScrollViewScreenView(Context context, AttributeSet attrs) {
    super(context, attrs);
    //noinspection unchecked
    _this = (V) this;
    impl.onCreateImpl(_this, context);
  }

  public ScrollViewScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    //noinspection unchecked
    _this = (V) this;
    impl.onCreateImpl(_this, context);
  }

  @SuppressWarnings("unused")
  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public ScrollViewScreenView(Context context, AttributeSet attrs, int defStyleAttr,
      int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    //noinspection unchecked
    _this = (V) this;
    impl.onCreateImpl(_this, context);
  }

  @Override
  protected final void onFinishInflate() {
    super.onFinishInflate();
    impl.onFinishInflateImpl(_this);
  }

  @Nullable
  @Override
  public Bundle onProvideTransientParams(@NonNull View view, @Nullable Bundle params) {
    return impl.onProvideTransientParamsImpl(_this, view, params);
  }

  @Override
  public final void onReceiveTransientParams(@Nullable Bundle params) {
    impl.onReceiveTransientParamsImpl(_this, params);
  }

  @Override
  protected final void onAttachedToWindow() {
    super.onAttachedToWindow();
    impl.onAttachedToWindowImpl(_this);
  }

  @Override
  protected final void onDetachedFromWindow() {
    impl.onDetachedFromWindowImpl(_this);
    super.onDetachedFromWindow();
  }

  @Override
  protected final void onVisibilityChanged(@NonNull View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);
    impl.onVisibilityChangedImpl(_this, changedView, visibility);
  }

  @Override
  protected final void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    impl.onSizeChangedImpl(_this, w, h, oldw, oldh);
  }

  @Override
  public final void dispatchRestoreInstanceState(@NonNull SparseArray<Parcelable> container) {
    super.dispatchRestoreInstanceState(container);
    impl.dispatchRestoreInstanceStateImpl(_this, container);
  }

  @Override
  public final void dispatchSaveInstanceState(@NonNull SparseArray<Parcelable> container) {
    super.dispatchSaveInstanceState(container);
    impl.dispatchSaveInstanceStateImpl(_this, container);
  }

  @Override
  protected final void onRestoreInstanceState(Parcelable state) {
    super.onRestoreInstanceState(impl.onRestoreInstanceStateImpl(_this, state));
  }

  @SuppressLint("MissingSuperCall")
  @Override
  protected final Parcelable onSaveInstanceState() {
    return impl.onSaveInstanceStateImpl(_this);
  }

  @Override
  public final boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    return impl.onActivityResultImpl(_this, requestCode, resultCode, data);
  }

  @Override
  public final boolean onBackPressed() {
    return impl.onBackPressedImpl(_this);
  }

  @Override
  public final boolean onOptionsItemSelected(MenuItem item) {
    return impl.onOptionsItemSelectedImpl(_this, item);
  }

  @Override
  public final void onPause() {
    impl.onPause(_this);
  }

  @Override
  public final void onResume() {
    impl.onResume(_this);
  }

  @CallSuper
  @Override
  public void onCreate(@NonNull Context context) {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public void onInflate() {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Nullable
  @Override
  public Bundle onProvideParams(@NonNull View view, @Nullable Bundle params) {
    impl.assertAllowScreenViewCalls();
    return params;
  }

  @CallSuper
  @Override
  public void onReceiveParams(@Nullable Bundle params) {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public void onAttach() {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public void onDetach() {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public void onVisChange(@NonNull View changedView, int visibility) {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public void onSizeChange(int w, int h, int oldw, int oldh) {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public void dispatchRestoreState(@NonNull SparseArray<Parcelable> container) {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public void dispatchSaveState(@NonNull SparseArray<Parcelable> container) {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public void restoreState(Parcelable state) {
    impl.assertAllowScreenViewCalls();
  }

  @CallSuper
  @Override
  public Parcelable saveState() {
    impl.assertAllowScreenViewCalls();
    return super.onSaveInstanceState();
  }

  @Override
  public boolean isButtery() {
    return false;
  }
}
