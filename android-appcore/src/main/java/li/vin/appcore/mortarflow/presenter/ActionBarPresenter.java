package li.vin.appcore.mortarflow.presenter;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import li.vin.appcore.mortarflow.android.HasActivity;
import mortar.Presenter;
import mortar.bundler.BundleService;

import static mortar.bundler.BundleService.getBundleService;

/** Allows shared configuration of the Android ActionBar. */
public class ActionBarPresenter extends Presenter<ActionBarPresenter.Activity> {

  @Module
  public static class ActionBarPresenterModule {
    @Provides
    @Singleton
    ActionBarPresenter provideActionBarPresenter() {
      return new ActionBarPresenter();
    }
  }

  public interface Activity extends HasActivity {
    void configureActionBar(Bundle config);
  }

  public static final String ACTION_BAR_HIDDEN = "ACTION_BAR_HIDDEN";
  public static final String ACTION_BAR_INVIS = "ACTION_BAR_INVIS";
  public static final String ACTION_BAR_DISABLEHOME = "ACTION_BAR_DISABLEHOME";
  public static final String ACTION_BAR_UP = "ACTION_BAR_UP";
  public static final String ACTION_BAR_ICON = "ACTION_BAR_ICON";
  public static final String ACTION_BAR_LOGO = "ACTION_BAR_LOGO";
  public static final String ACTION_BAR_ELEVATION = "ACTION_BAR_ELEVATION";
  public static final String ACTION_BAR_BG = "ACTION_BAR_BG";
  public static final String ACTION_BAR_CUSTOM = "ACTION_BAR_CUSTOM";
  public static final String ACTION_BAR_TITLE = "ACTION_BAR_TITLE";
  public static final String ACTION_BAR_SUBTITLE = "ACTION_BAR_SUBTITLE";
  public static final String ACTION_BAR_TITLE_TYPEFACE = "ACTION_BAR_TITLE_TYPEFACE";
  public static final String ACTION_BAR_SUBTITLE_TYPEFACE = "ACTION_BAR_SUBTITLE_TYPEFACE";
  public static final String ACTION_BAR_MENUS = "ACTION_BAR_MENUS";

  @SuppressWarnings("unchecked")
  public static class ConfigBuilder<T extends ConfigBuilder> {
    private final Bundle bundle = new Bundle();
    private final ActionBarPresenter presenter;

    protected ConfigBuilder(ActionBarPresenter presenter) {
      this.presenter = presenter;
    }

    public final T hide() {
      return bool(ACTION_BAR_HIDDEN, true);
    }

    public final T invis(@ColorInt int invisClr) {
      return integer(ACTION_BAR_INVIS, invisClr);
    }

    public final T disableHome() {
      return bool(ACTION_BAR_DISABLEHOME, true);
    }

    public final T up() {
      return bool(ACTION_BAR_UP, true);
    }

    public final T icon(@DrawableRes int resId) {
      return integer(ACTION_BAR_ICON, resId);
    }

    public final T logo(@DrawableRes int resId) {
      return integer(ACTION_BAR_LOGO, resId);
    }

    public final T elevation(float elevation) {
      return floating(ACTION_BAR_ELEVATION, elevation);
    }

    public final T bg(@DrawableRes int resId) {
      return integer(ACTION_BAR_BG, resId);
    }

    public final T custom(@LayoutRes int resId) {
      return integer(ACTION_BAR_CUSTOM, resId);
    }

    public final T title(@NonNull CharSequence title) {
      return charSequence(ACTION_BAR_TITLE, title);
    }

    public final T subTitle(@NonNull CharSequence subTitle) {
      return charSequence(ACTION_BAR_SUBTITLE, subTitle);
    }

    public final T titleTypeface(@NonNull String typefaceAssetPath) {
      return charSequence(ACTION_BAR_TITLE_TYPEFACE, typefaceAssetPath);
    }

    public final T subTitleTypeface(@NonNull String typefaceAssetPath) {
      return charSequence(ACTION_BAR_SUBTITLE_TYPEFACE, typefaceAssetPath);
    }

    public final T menus(@NonNull int... menus) {
      return intArray(ACTION_BAR_MENUS, menus);
    }

    public final T bool(@NonNull String key, boolean b) {
      bundle.putBoolean(key, b);
      return (T) this;
    }

    public final T integer(@NonNull String key, int i) {
      bundle.putInt(key, i);
      return (T) this;
    }

    public final T floating(@NonNull String key, float f) {
      bundle.putFloat(key, f);
      return (T) this;
    }

    public final T charSequence(@NonNull String key, @NonNull CharSequence charSeq) {
      bundle.putCharSequence(key, charSeq);
      return (T) this;
    }

    public final T intArray(@NonNull String key, @NonNull int... ints) {
      bundle.putIntArray(key, ints);
      return (T) this;
    }

    public final void commit() {
      checkBuildConditions();
      presenter.config.clear();
      presenter.config.putAll(bundle);
      presenter.update();
    }

    @CallSuper
    protected void checkBuildConditions() {
      if (bundle.isEmpty()) {
        throw new RuntimeException("cannot commit empty config.");
      }
      if (bundle.containsKey(ACTION_BAR_INVIS) && bundle.size() > 1) {
        throw new RuntimeException("invis may not be provided alongside other options.");
      }
      int i = 0;
      if (bundle.containsKey(ACTION_BAR_UP)) i++;
      if (bundle.containsKey(ACTION_BAR_ICON)) i++;
      if (bundle.containsKey(ACTION_BAR_LOGO)) i++;
      if (i > 1) {
        throw new RuntimeException("invis, up, icon, and logo are mutually exclusive.");
      }
    }
  }

  private final Bundle config = new Bundle();

  protected ActionBarPresenter() {
    // hidden until proven otherwise.
    config.putBoolean(ACTION_BAR_HIDDEN, true);
  }

  @CallSuper
  @Override
  public void onLoad(Bundle savedInstanceState) {
    update();
  }

  @CallSuper
  @Override
  public void dropView(Activity view) {
    this.config.clear();
    super.dropView(view);
  }

  @NonNull
  public ConfigBuilder startConfiguration() {
    return new ConfigBuilder(this);
  }

  @Override
  protected final BundleService extractBundleService(Activity activity) {
    return getBundleService(activity.getActivity());
  }

  private void update() {
    if (config.isEmpty() || !hasView()) return;
    Activity activity = getView();
    activity.configureActionBar(new Bundle(config));
  }
}
