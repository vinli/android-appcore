package li.vin.uiconcepts.presenter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import dagger.Module;
import dagger.Provides;
import li.vin.appcore.mortarflow.presenter.ActionBarPresenter;
import li.vin.appcore.mortarflow.scope.PerScreen;
import li.vin.appcore.screenview.ScreenViewPresenter;
import li.vin.uiconcepts.R;
import li.vin.uiconcepts.adapter.MainPagerAdapter;
import li.vin.uiconcepts.view.MainView;

public class MainPresenter extends ScreenViewPresenter<MainView> {

  @Module
  public static class MainPresenterModule {
    @Provides
    @PerScreen
    MainPresenter provideMainPresenter(ActionBarPresenter actionBarPresenter,
        MainPagerAdapter pagerAdapter) {
      return new MainPresenter(actionBarPresenter, pagerAdapter);
    }
  }

  private final AppActionBarPresenter actionBarPresenter;
  private final MainPagerAdapter pagerAdapter;

  private MainPresenter(ActionBarPresenter actionBarPresenter, MainPagerAdapter pagerAdapter) {
    this.actionBarPresenter = (AppActionBarPresenter) actionBarPresenter;
    this.pagerAdapter = pagerAdapter;
  }

  @Override
  protected void onAttachedToWindow(@NonNull MainView view) {
    actionBarPresenter.startConfiguration() //
        .homeTitle() //
        .color(Color.BLUE) //
        .menus(R.menu.test_menu) //
        .commit();
    view.setPagerAdapter(pagerAdapter);
  }

  @Override
  protected void onDetachedFromWindow(@NonNull MainView view) {
    view.clearPagerAdapter();
  }

  @Override
  public boolean onOptionsItemSelected(@NonNull MainView view, MenuItem item) {
    if (item.getItemId() == R.id.action_test) {
      toastShort("got test action!");
      return true;
    }
    return false;
  }
}
