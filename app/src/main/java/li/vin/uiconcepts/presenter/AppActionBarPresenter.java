package li.vin.uiconcepts.presenter;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import li.vin.appcore.mortarflow.presenter.ActionBarPresenter;

public class AppActionBarPresenter extends ActionBarPresenter {
  public static final String ACTION_BAR_COLOR = "ACTION_BAR_COLOR";

  @Module
  public static class AppActionBarPresenterModule {
    @Provides
    @Singleton
    ActionBarPresenter provideAppActionBarPresenter() {
      return new AppActionBarPresenter();
    }
  }

  public static class ConfigBuilder
      extends ActionBarPresenter.ConfigBuilder<AppActionBarPresenter.ConfigBuilder> {
    protected ConfigBuilder(ActionBarPresenter presenter) {
      super(presenter);
    }

    public final ConfigBuilder homeTitle() {
      return title("Home");
    }

    public final ConfigBuilder color(int color) {
      return integer(ACTION_BAR_COLOR, color);
    }
  }

  @NonNull
  @Override
  public ConfigBuilder startConfiguration() {
    return new ConfigBuilder(this);
  }
}
