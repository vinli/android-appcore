package li.vin.uiconcepts;

import dagger.Component;
import javax.inject.Singleton;
import li.vin.appcore.mortarflow.MortarContextFactory.MortarContextFactoryModule;
import li.vin.appcore.mortarflow.presenter.DrawerPresenter.DrawerPresenterModule;
import li.vin.appcore.parcel.GsonParceler.GsonModule;
import li.vin.appcore.parcel.GsonParceler.GsonParcelerModule;
import li.vin.uiconcepts.presenter.AppActionBarPresenter.AppActionBarPresenterModule;
import li.vin.uiconcepts.screen.DrawerScreen;
import li.vin.uiconcepts.screen.MainScreen;
import li.vin.uiconcepts.screen.SplashScreen;
import li.vin.uiconcepts.screen.SubScreen;

@Singleton
@Component(modules = {
    AppModule.class, //
    MortarContextFactoryModule.class, //
    DrawerPresenterModule.class, //
    //ActionBarPresenterModule.class, //
    AppActionBarPresenterModule.class, //
    GsonModule.class,
    GsonParcelerModule.class,
})
public interface AppComponent {
  void inject(MainActivity mainActivity);

  SplashScreen.SplashScreenComponent splashScreenComponent();

  DrawerScreen.DrawerScreenComponent drawerScreenComponent();

  MainScreen.MainScreenComponent mainScreenComponent();

  SubScreen.SubScreenComponent subScreenComponent(SubScreen.SubScreenModule mod);
}
