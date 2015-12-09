package li.vin.appcore.mortarflow.presenter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import dagger.Module;
import dagger.Provides;
import flow.Flow;
import javax.inject.Singleton;
import li.vin.appcore.screenview.ScreenViewPresenter;
import li.vin.appcore.screenview.SplashScreenView;

/** Customizable splash screen presenter. */
public class SplashPresenter extends ScreenViewPresenter<SplashScreenView> {

  @Module
  public static class SplashPresenterModule {
    @Provides
    @Singleton
    SplashPresenter provideSplashPresenter() {
      return new SplashPresenter();
    }
  }

  private Flow mainFlow;
  private DrawerPresenter drawerPresenter;

  protected SplashPresenter() {
  }

  public void dismiss() {
    SplashScreenView view = getView();
    if (view == null) return;
    Animator dismissAnimator = view.dismissAnimator();
    dismissAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        SplashScreenView view = getView();
        if (view == null) return;
        if (view.getParent() instanceof ViewGroup) {
          ((ViewGroup) view.getParent()).removeView(view);
        }
        dropView(view);
      }
    });
    dismissAnimator.start();
  }

  @Override
  public void dropView(SplashScreenView view) {
    super.dropView(view);
  }

  @NonNull
  public Flow mainFlow() {
    if (mainFlow == null) throw new IllegalStateException("No Flow set.");
    return mainFlow;
  }

  public final void setMainFlow(Flow flow) {
    if (flow == null) throw new NullPointerException();
    mainFlow = flow;
  }

  @NonNull
  public DrawerPresenter drawerPresenter() {
    if (drawerPresenter == null) throw new IllegalStateException("No DrawerPresenter set.");
    return drawerPresenter;
  }

  public final void setDrawerPresenter(DrawerPresenter drawer) {
    if (drawer == null) throw new NullPointerException();
    drawerPresenter = drawer;
  }
}
