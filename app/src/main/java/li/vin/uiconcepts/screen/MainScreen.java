package li.vin.uiconcepts.screen;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import dagger.Subcomponent;
import li.vin.uiconcepts.R;
import li.vin.uiconcepts.adapter.MainPagerAdapter.MainPagerAdapterModule;
import li.vin.uiconcepts.AppComponent;
import li.vin.uiconcepts.presenter.MainPresenter.MainPresenterModule;
import li.vin.appcore.dagger.DaggerService;
import li.vin.appcore.mortarflow.Layout;
import li.vin.appcore.mortarflow.scope.PerScreen;
import li.vin.appcore.mortarflow.Screen;
import li.vin.uiconcepts.view.MainView;
import mortar.MortarScope;

@Layout(R.layout.main_view)
public class MainScreen extends Screen {

  @Override
  public boolean equals(Object o) {
    return this == o || !(o == null || getClass() != o.getClass());
  }

  @Override
  public int hashCode() {
    return 0;
  }

  @PerScreen
  @Subcomponent(modules = { MainPresenterModule.class, MainPagerAdapterModule.class })
  public interface MainScreenComponent {
    void inject(MainView mainView);

    SubScreen.SubScreenComponent plus(SubScreen.SubScreenModule module);
  }

  @Nullable
  @Override
  protected Object createDaggerComponent(Resources resources, MortarScope parentScope) {
    return DaggerService //
        .<AppComponent>getDaggerComponent(parentScope) //
        .mainScreenComponent();
  }
}
