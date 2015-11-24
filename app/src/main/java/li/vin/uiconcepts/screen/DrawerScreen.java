package li.vin.uiconcepts.screen;

import android.content.res.Resources;
import android.support.annotation.Nullable;
import dagger.Subcomponent;
import javax.inject.Singleton;
import li.vin.uiconcepts.R;
import li.vin.uiconcepts.AppComponent;
import li.vin.appcore.dagger.DaggerService;
import li.vin.appcore.mortarflow.Layout;
import li.vin.appcore.mortarflow.Screen;
import li.vin.uiconcepts.view.DrawerView;
import mortar.MortarScope;

@Layout(R.layout.drawer_view)
public class DrawerScreen extends Screen {

  @Singleton
  @Subcomponent
  public interface DrawerScreenComponent {
    void inject(DrawerView DrawerView);
  }

  @Nullable
  @Override
  protected Object createDaggerComponent(Resources resources, MortarScope parentScope) {
    return DaggerService //
        .<AppComponent>getDaggerComponent(parentScope) //
        .drawerScreenComponent();
  }
}
