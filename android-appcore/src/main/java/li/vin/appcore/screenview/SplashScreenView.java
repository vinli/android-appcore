package li.vin.appcore.screenview;

import android.animation.Animator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import li.vin.appcore.mortarflow.presenter.SplashPresenter;

public abstract class SplashScreenView
    extends LinearLayoutScreenView<SplashScreenView, SplashPresenter> {

  public SplashScreenView(Context context) {
    super(context);
  }

  public SplashScreenView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SplashScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @SuppressWarnings("unused")
  public SplashScreenView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  public void dismiss() {
    SplashPresenter presenter = getPresenter();
    if (presenter != null) presenter.dismiss();
  }

  @NonNull
  public abstract Animator dismissAnimator();
}
