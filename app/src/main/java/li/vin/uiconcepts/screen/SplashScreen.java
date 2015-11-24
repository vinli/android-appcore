package li.vin.uiconcepts.screen;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import dagger.Subcomponent;
import flow.NotPersistent;
import li.vin.uiconcepts.R;
import li.vin.uiconcepts.AppComponent;
import li.vin.uiconcepts.presenter.SplashPresenter.SplashPresenterModule;
import li.vin.appcore.dagger.DaggerService;
import li.vin.appcore.mortarflow.Layout;
import li.vin.appcore.mortarflow.scope.PerScreen;
import li.vin.appcore.mortarflow.Screen;
import li.vin.uiconcepts.view.SplashView;
import mortar.MortarScope;

@NotPersistent
@Layout(R.layout.splash_view)
public class SplashScreen extends Screen {

  @PerScreen
  @Subcomponent(modules = SplashPresenterModule.class)
  public interface SplashScreenComponent {
    void inject(SplashView SplashView);
  }

  @Nullable
  @Override
  protected Object createDaggerComponent(Resources resources, MortarScope parentScope) {
    return DaggerService //
        .<AppComponent>getDaggerComponent(parentScope) //
        .splashScreenComponent();
  }
}
