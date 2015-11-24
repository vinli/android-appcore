package li.vin.uiconcepts;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import flow.History;
import flow.StateParceler;
import flow.path.PathContextFactory;
import javax.inject.Inject;
import li.vin.appcore.dagger.DaggerService;
import li.vin.appcore.mortarflow.MortarContextFactory;
import li.vin.appcore.mortarflow.Screen;
import li.vin.appcore.mortarflow.android.MortarFlowAppCompatActivity;
import li.vin.appcore.mortarflow.presenter.ActionBarPresenter;
import li.vin.appcore.parcel.GsonParceler;
import li.vin.appcore.segue.Segues;
import li.vin.uiconcepts.presenter.AppActionBarPresenter;
import li.vin.uiconcepts.screen.DrawerScreen;
import li.vin.uiconcepts.screen.SplashScreen;

/**
 * Created by christophercasey on 9/2/15.
 */
public class MainActivity extends MortarFlowAppCompatActivity {

  @Inject ActionBarPresenter actionBarPresenter;
  @Inject GsonParceler gsonParceler;
  @Inject Segues segues;
  @Inject MortarContextFactory contextFactory;

  @Nullable
  @Override
  protected ActionBarPresenter getActionBarPresenter() {
    return actionBarPresenter;
  }

  @Nullable
  @Override
  protected Screen getDrawerScreen() {
    return new DrawerScreen();
  }

  @NonNull
  @Override
  protected StateParceler getParceler() {
    return gsonParceler;
  }

  @NonNull
  @Override
  protected Segues getSegues() {
    return segues;
  }

  @NonNull
  @Override
  protected PathContextFactory getPathContextFactory() {
    return contextFactory;
  }

  @NonNull
  @Override
  protected History getDefaultHistory() {
    return History.single(new SplashScreen());
  }

  @LayoutRes
  @Override
  protected int getContentLayoutResId() {
    return R.layout.root_layout;
  }

  @IdRes
  @Override
  protected int getPathContainerViewResId() {
    return R.id.main_path;
  }

  @IdRes
  @Override
  protected int getDrawerLayoutViewResId() {
    return R.id.drawer;
  }

  @Override
  protected int getToolbarViewResId() {
    return R.id.toolbar;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    DaggerService //
        .<AppComponent>getDaggerComponent(getApplicationContext()) //
        .inject(this);
    super.onCreate(savedInstanceState);
  }

  @Override
  public void configureActionBar(Bundle config) {
    super.configureActionBar(config);
    int clr = config.getInt(AppActionBarPresenter.ACTION_BAR_COLOR, -1);
    if (clr != -1) {
      setAbColor(clr);
    }
  }

  private void setAbColor(int color) {
    actionBar().setBackgroundDrawable(new ColorDrawable(color));
  }
}
