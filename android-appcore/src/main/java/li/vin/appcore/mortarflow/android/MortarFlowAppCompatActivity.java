package li.vin.appcore.mortarflow.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import com.squareup.otto.Bus;
import flow.Flow;
import flow.FlowDelegate;
import flow.History;
import flow.StateParceler;
import flow.path.PathContainerView;
import flow.path.PathContextFactory;
import java.util.HashSet;
import java.util.Set;
import li.vin.appcore.R;
import li.vin.appcore.mortarflow.FramePathContainerView;
import li.vin.appcore.mortarflow.MortarInflater;
import li.vin.appcore.mortarflow.Screen;
import li.vin.appcore.mortarflow.presenter.ActionBarPresenter;
import li.vin.appcore.mortarflow.presenter.SplashPresenter;
import li.vin.appcore.screenview.DrawerScreenView;
import li.vin.appcore.screenview.SplashScreenView;
import li.vin.appcore.segue.Segues;
import mortar.MortarScope;
import mortar.MortarScopeDevHelper;
import mortar.bundler.BundleServiceRunner;

import static android.support.v4.widget.CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER;
import static li.vin.appcore.TypefaceUtil.spanWithTypeface;
import static li.vin.appcore.mortarflow.android.RequestPermissionService.getRequestPermissionService;

/**
 * Created by christophercasey on 9/7/15.
 */
public abstract class MortarFlowAppCompatActivity extends AppCompatActivity
    implements ActionBarPresenter.Activity, Flow.Dispatcher {

  private MortarScope activityScope;
  private int[] actionBarMenuRes;
  private boolean disableHome;

  private FramePathContainerView container;
  private FlowDelegate flowDelegate;
  private DrawerScreenView drawerView;
  private PathContextFactory pathContextFactory;
  private ActionBarDrawerToggle drawerToggle;
  private CursorAdapter searchSuggestionsAdapter;

  private float defAbElevation;

  /**
   * Action Bar presenter - optional. Return null if this Activity requires no action bar
   * presentation handling
   */
  @Nullable
  protected ActionBarPresenter getActionBarPresenter() {
    return null;
  }

  @Nullable
  protected Screen getDrawerScreen() {
    return null;
  }

  @Nullable
  protected Screen getSplashScreen() {
    return null;
  }

  /** Provide a valid StateParceler instance for Flow state preservation. */
  @NonNull
  protected abstract StateParceler getParceler();

  /** Provide valid segues. */
  @NonNull
  protected abstract Segues getSegues();

  /** Provide valid PathContextFactory instance for global usage with Mortar & Flow. */
  @NonNull
  protected abstract PathContextFactory getPathContextFactory();

  /** Return default History for Flow. */
  @NonNull
  protected abstract History getDefaultHistory();

  /** Return valid layout resource ID for content view layout. */
  @LayoutRes
  protected abstract int getContentLayoutResId();

  /** Provide resource ID of main {@link PathContainerView} in the layout. This is required. */
  @IdRes
  protected abstract int getPathContainerViewResId();

  /**
   * Provide resource ID of the {@link DrawerLayout} within the main content view. This is
   * required if {@link #getDrawerScreen()} returns on non-null value; otherwise, it is ignored.
   */
  @IdRes
  protected int getDrawerLayoutViewResId() {
    return -1;
  }

  /**
   * Provide resource ID of a {@link Toolbar} within the main content view. This is required if
   * {@link #getActionBarPresenter()} returns a non-null value and the Activity's theme does not
   * provide
   * a built-in Action Bar.
   */
  @IdRes
  protected int getToolbarViewResId() {
    return -1;
  }

  /**
   * Provide resource ID of a ViewGroup capable of hosting a splash screen.
   */
  @IdRes
  protected int getSplashContainerViewResId() {
    return -1;
  }

  /**
   * Provide AARRGGBB hex color int for the background color of the status bar when a {@link
   * DrawerLayout} is used. Ignored if no drawer is provided; default implementation looks up
   * primary dark color from theme and uses that.
   */
  @ColorInt
  protected int getStatusBarBackgroundColor() {
    TypedArray a = obtainStyledAttributes(new int[] { R.attr.colorPrimaryDark });
    int clr = a.getColor(0, Color.BLACK);
    a.recycle();
    return clr;
  }

  @Nullable
  protected SearchSuggestionsLayout getSearchSuggestionsLayout() {
    return null;
  }

  @StringRes
  protected int getSearchQueryHint() {
    return -1;
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    RequestPermissionService svc = getRequestPermissionService(activityScope);
    if (svc == null) return;
    svc.notifyRequestPermissionsResult(requestCode, permissions, grantResults);
  }

  @CallSuper
  @Override
  public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {
    container.dispatch(traversal, callback);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    @SuppressWarnings("deprecation") Object lastNonConfig = getLastNonConfigurationInstance();
    FlowDelegate.NonConfigurationInstance nonConfig =
        lastNonConfig instanceof FlowDelegate.NonConfigurationInstance
            ? (FlowDelegate.NonConfigurationInstance) lastNonConfig
            : null;

    String scopeName = getScopeName();
    activityScope = MortarScope.findChild(getApplicationContext(), scopeName);
    if (activityScope == null) {
      activityScope = MortarScope.buildChild(getApplicationContext()) //
          .withService(BundleServiceRunner.SERVICE_NAME, new BundleServiceRunner())
          .withService(WindowService.SERVICE_NAME, new WindowService(this))
          .withService(FragmentService.SERVICE_NAME, new FragmentService(this))
          .withService(StartActivityService.SERVICE_NAME, new StartActivityService(this))
          .withService(RequestPermissionService.SERVICE_NAME, new RequestPermissionService(this))
          .withService(OttoService.SERVICE_NAME, new OttoService(new Bus()))
          .build(scopeName);
    } else {
      WindowService.getWindowService(activityScope).update(this);
      FragmentService.getFragmentService(activityScope).update(this);
      StartActivityService.getStartActivityService(activityScope).update(this);
      RequestPermissionService.getRequestPermissionService(activityScope).update(this);
    }

    BundleServiceRunner //
        .getBundleServiceRunner(activityScope) //
        .onCreate(savedInstanceState);

    setContentView(getContentLayoutResId());

    ActionBarPresenter actionBarPresenter = getActionBarPresenter();
    if (actionBarPresenter != null) {
      Toolbar toolbar = (Toolbar) findViewById(getToolbarViewResId());
      if (toolbar != null) setSupportActionBar(toolbar);
      defAbElevation = actionBar().getElevation();
      // action bar hidden when first added to layout to avoid artifacting if initial screen
      // requires no action bar. It looks more natural to show an action bar where none existed
      // than it does to immediately hide one that was shown for a brief instant.
      hideAb();
      actionBarPresenter.takeView(this);
    }

    Screen drawerScreen = getDrawerScreen();
    if (drawerScreen != null) {
      DrawerLayout drawerLayout = (DrawerLayout) findViewById(getDrawerLayoutViewResId());
      if (drawerLayout == null) {
        throw new RuntimeException("Drawer screen found with no drawer layout.");
      }
      ViewGroup drawerParentView = (ViewGroup) drawerLayout.getChildAt(1);
      setProperMaterialDrawerWidth(drawerParentView);
      drawerParentView.addView(drawerView =
          (DrawerScreenView) MortarInflater.from(pathContextFactory = getPathContextFactory())
              .inflate(drawerScreen, drawerParentView));
      drawerLayout.setStatusBarBackgroundColor(getStatusBarBackgroundColor());
      drawerLayout.setDrawerListener(drawerToggle =
          new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open,
              R.string.drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
              super.onDrawerSlide(drawerView, slideOffset);
              MortarFlowAppCompatActivity.this.drawerView.onDrawerSlide(drawerView, slideOffset);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
              super.onDrawerOpened(drawerView);
              MortarFlowAppCompatActivity.this.drawerView.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
              super.onDrawerClosed(drawerView);
              MortarFlowAppCompatActivity.this.drawerView.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
              super.onDrawerStateChanged(newState);
              MortarFlowAppCompatActivity.this.drawerView.onDrawerStateChanged(newState);
            }
          });
    }

    SplashPresenter splashPresenter = savedInstanceState == null
        ? presentSplash(false)
        : null;

    container = (FramePathContainerView) findViewById(getPathContainerViewResId());
    container.init(getPathContextFactory(), getSegues());
    flowDelegate = FlowDelegate.onCreate(nonConfig, getIntent(), savedInstanceState, getParceler(),
        getDefaultHistory(), this);

    if (splashPresenter != null) {
      splashPresenter.setMainFlow(Flow.get(container));
    }
  }

  protected final SplashPresenter presentSplash() {
    return presentSplash(true);
  }

  protected final boolean splashIsShowing() {
    if (isFinishing()) return false;
    Screen splashScreen = getSplashScreen();
    if (splashScreen == null) return false;
    ViewGroup splashHost = (ViewGroup) findViewById(getSplashContainerViewResId());
    if (splashHost == null) {
      throw new RuntimeException("Splash screen found with no container layout.");
    }
    return (splashHost.getChildCount() != 0);
  }

  private SplashPresenter presentSplash(boolean immediatelySetFlow) {
    if (isFinishing()) return null;
    Screen splashScreen = getSplashScreen();
    if (splashScreen == null) return null;
    ViewGroup splashHost = (ViewGroup) findViewById(getSplashContainerViewResId());
    if (splashHost == null) {
      throw new RuntimeException("Splash screen found with no container layout.");
    }
    if (splashHost.getChildCount() != 0) return null;

    if (pathContextFactory == null) pathContextFactory = getPathContextFactory();
    SplashScreenView splashView = (SplashScreenView) MortarInflater.from(pathContextFactory)
        .inflate(splashScreen, splashHost);
    SplashPresenter splashPresenter = splashView.getPresenter();
    if (splashPresenter != null && drawerView != null) {
      splashPresenter.setDrawerPresenter(drawerView.getPresenter());
    }
    if (Build.VERSION.SDK_INT >= 21) {
      splashHost.setElevation(999f);
    }
    splashView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
      @Override
      public void onViewAttachedToWindow(View v) {
      }

      @Override
      public void onViewDetachedFromWindow(View v) {
        MortarInflater.from(pathContextFactory).destroyScope(v);
        v.removeOnAttachStateChangeListener(this);
      }
    });
    splashHost.addView(splashView);
    if (immediatelySetFlow && splashPresenter != null) {
      splashPresenter.setMainFlow(Flow.get(container));
    }
    return splashPresenter;
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    if (drawerToggle != null) drawerToggle.syncState();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    if (drawerToggle != null) drawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    flowDelegate.onNewIntent(intent);
  }

  @Override
  protected void onResume() {
    super.onResume();
    flowDelegate.onResume();
    if (drawerView != null) drawerView.onResume();
    container.onResume();
  }

  @Override
  protected void onPause() {
    container.onPause();
    if (drawerView != null) drawerView.onPause();
    flowDelegate.onPause();
    super.onPause();
  }

  @Override
  public Object onRetainCustomNonConfigurationInstance() {
    return flowDelegate.onRetainNonConfigurationInstance();
  }

  @Override
  public Object getSystemService(@NonNull String name) {

    if (flowDelegate != null) {
      Object flowService = flowDelegate.getSystemService(name);
      if (flowService != null) return flowService;
    }

    return activityScope != null && activityScope.hasService(name)
        ? activityScope.getService(name)
        : super.getSystemService(name);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    flowDelegate.onSaveInstanceState(outState);

    BundleServiceRunner //
        .getBundleServiceRunner(this) //
        .onSaveInstanceState(outState);
  }

  /** Inform the view about back events. */
  @Override
  public void onBackPressed() {
    if (drawerView != null && drawerView.isDrawerOpen()) {
      drawerView.closeDrawer();
      return;
    }
    if (drawerView != null && drawerView.onBackPressed()) return;
    if (!container.onBackPressed()) super.onBackPressed();
  }

  /** Inform then view about activity results. */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    try {
      RequestPermissionService.getRequestPermissionService(activityScope)
          .notifyActivityResult(this, requestCode);
    } catch (Exception ignored) {
    }
    if (drawerView != null && drawerView.onActivityResult(requestCode, resultCode, data)) return;
    if (!container.onActivityResult(requestCode, resultCode, data)) {
      super.onActivityResult(requestCode, resultCode, data);
    }
  }

  /** Inform the view about up events and dispatch options item selected events. */
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (splashIsShowing()) return true; // dispatch no menu events when splash is visible.
    if (!disableHome) {
      if (drawerToggle != null && drawerToggle.onOptionsItemSelected(item)) return true;
      if (item.getItemId() == android.R.id.home) return container.onBackPressed();
    }
    if (buildConfigDebug() && item.getItemId() == R.id.action_log_scope_hierarchy) {
      Log.d("MORTAR_SCOPE", MortarScopeDevHelper.scopeHierarchyToString(activityScope));
      return true;
    }
    return (drawerView != null && drawerView.onOptionsItemSelected(item))
        || container.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
  }

  protected void onQueryTextSubmit(@NonNull String query) {
    container.onSearchQuerySubmit(query);
  }

  protected void onQueryTextChange(@NonNull String query, @NonNull Set<String> outSuggestions) {
    container.onSearchQueryChange(query, outSuggestions);
  }

  /** Configure the action bar menu as required by {@link ActionBarPresenter.Activity}. */
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    if (actionBarMenuRes != null) {
      for (int menuRes : actionBarMenuRes) {
        getMenuInflater().inflate(menuRes, menu);
      }
    }

    if (actionBarMenuRes == null || actionBarMenuRes.length > 0) {
      if (buildConfigDebug()) {
        getMenuInflater().inflate(R.menu.log_scope_hierarchy_menu, menu);
      }
    }

    final MenuItem searchItem = menu.findItem(R.id.action_search);
    if (searchItem != null) {
      final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
      if (searchView != null) {

        int hint = getSearchQueryHint();
        if (hint != -1) {
          searchView.setQueryHint(getString(hint));
        }

        SearchSuggestionsLayout suggestionsLayout;
        if (searchSuggestionsAdapter == null
            && (suggestionsLayout = getSearchSuggestionsLayout()) != null) {
          final String[] from = new String[] { "THE_COL" };
          final int[] to = new int[] { suggestionsLayout.textViewResId };
          searchSuggestionsAdapter =
              new SimpleCursorAdapter(this, suggestionsLayout.layoutResId, null, from, to,
                  FLAG_REGISTER_CONTENT_OBSERVER);
        }
        if (searchSuggestionsAdapter != null) {
          searchView.setSuggestionsAdapter(searchSuggestionsAdapter);
        }

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
          @Override
          public boolean onSuggestionSelect(int position) {
            return false;
          }

          @Override
          public boolean onSuggestionClick(int position) {
            MatrixCursor c = (MatrixCursor) searchView.getSuggestionsAdapter().getItem(position);
            String suggestion = c.getString(1);
            if (suggestion == null) suggestion = "";
            searchItem.collapseActionView();
            MortarFlowAppCompatActivity.this.onQueryTextSubmit(suggestion);
            return false;
          }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            if (query == null) query = "";
            searchItem.collapseActionView();
            MortarFlowAppCompatActivity.this.onQueryTextSubmit(query);
            return false;
          }

          @Override
          public boolean onQueryTextChange(String query) {
            if (query == null) query = "";
            Set<String> suggestions = new HashSet<>();
            MortarFlowAppCompatActivity.this.onQueryTextChange(query, suggestions);
            MatrixCursor c = new MatrixCursor(new String[] { BaseColumns._ID, "THE_COL" });
            int i = 0;
            for (String suggestion : suggestions) {
              c.addRow(new Object[] { i++, suggestion });
            }
            searchSuggestionsAdapter.changeCursor(c);
            return false;
          }
        });
      }
    }

    return true;
  }

  protected static SearchSuggestionsLayout newSearchSuggestionsLayout(@LayoutRes int layoutResId,
      @IdRes int textViewResId) {
    return new SearchSuggestionsLayout(layoutResId, textViewResId);
  }

  protected static final class SearchSuggestionsLayout {
    @LayoutRes public final int layoutResId;
    @IdRes public final int textViewResId;

    private SearchSuggestionsLayout(@LayoutRes int layoutResId, @IdRes int textViewResId) {
      this.layoutResId = layoutResId;
      this.textViewResId = textViewResId;
    }
  }

  @Override
  protected void onDestroy() {
    ActionBarPresenter actionBarPresenter = getActionBarPresenter();
    if (actionBarPresenter != null) {
      actionBarPresenter.dropView(this);
    }

    if (drawerView != null) {
      ((ViewGroup) drawerView.getParent()).removeView(drawerView);
      MortarInflater.from(pathContextFactory).destroyScope(drawerView);
    }

    if (isFinishing() && activityScope != null) {
      activityScope.destroy();
      activityScope = null;
    }

    super.onDestroy();
  }

  protected final ActionBar actionBar() {
    ActionBar actionBar = getSupportActionBar();
    if (actionBar == null) throw new NullPointerException("no action bar.");
    return actionBar;
  }

  @NonNull
  @Override
  public final Activity getActivity() {
    return this;
  }

  @Override
  public void configureActionBar(Bundle config) {
    boolean hide = config.getBoolean(ActionBarPresenter.ACTION_BAR_HIDDEN);
    int invis = config.getInt(ActionBarPresenter.ACTION_BAR_INVIS, -1);
    disableHome = config.getBoolean(ActionBarPresenter.ACTION_BAR_DISABLEHOME);
    boolean up = config.getBoolean(ActionBarPresenter.ACTION_BAR_UP);
    int icon = config.getInt(ActionBarPresenter.ACTION_BAR_ICON, -1);
    int logo = config.getInt(ActionBarPresenter.ACTION_BAR_LOGO, -1);
    float elevation = config.getFloat(ActionBarPresenter.ACTION_BAR_ELEVATION, -1);
    int bg = config.getInt(ActionBarPresenter.ACTION_BAR_BG, -1);
    int custom = config.getInt(ActionBarPresenter.ACTION_BAR_CUSTOM, -1);
    CharSequence title = config.getCharSequence(ActionBarPresenter.ACTION_BAR_TITLE, null);
    CharSequence subTitle = config.getCharSequence(ActionBarPresenter.ACTION_BAR_SUBTITLE, null);
    CharSequence titleTypefaceAssetPath =
        config.getCharSequence(ActionBarPresenter.ACTION_BAR_TITLE_TYPEFACE, null);
    CharSequence subTitleTypefaceAssetPath =
        config.getCharSequence(ActionBarPresenter.ACTION_BAR_SUBTITLE_TYPEFACE, null);
    int[] menus = config.getIntArray(ActionBarPresenter.ACTION_BAR_MENUS);

    if (invis != -1) {
      setAbInvis();
      setAbTitles(null, null, null, null);
      actionBar().setElevation(0);
      actionBar().setBackgroundDrawable(new ColorDrawable(invis));
      setAbCustom(-1);
      setAbMenu(new int[] {});
      showAb();
      return;
    }

    actionBar().setElevation(elevation == -1
        ? defAbElevation
        : elevation);

    if (hide) {
      hideAb();
    } else {
      showAb();
    }

    if (disableHome) {
      setAbUp(false);
    } else if (up) {
      setAbUp(true);
    } else if (logo != -1) {
      setAbLogo(logo);
    } else if (icon != -1) {
      setAbIcon(icon);
    } else {
      setAbUp(false);
    }

    setAbTitles(title, subTitle, titleTypefaceAssetPath, subTitleTypefaceAssetPath);
    setAbBg(bg);
    setAbCustom(custom);
    setAbMenu(menus);
  }

  private void hideAb() {
    actionBar().hide();
  }

  private void showAb() {
    actionBar().show();
  }

  private void setAbInvis() {
    ActionBar actionBar = actionBar();
    if (drawerToggle != null) {
      drawerToggle.setDrawerIndicatorEnabled(false);
    }
    actionBar.setDisplayHomeAsUpEnabled(false);
    actionBar.setHomeButtonEnabled(false);
  }

  private void setAbNoHome() {

  }

  private void setAbUp(boolean up) {
    ActionBar actionBar = actionBar();
    if (drawerToggle != null) {
      drawerToggle.setDrawerIndicatorEnabled(!up);
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    } else {
      actionBar.setHomeButtonEnabled(up);
      actionBar.setDisplayHomeAsUpEnabled(up);
    }
  }

  private void setAbIcon(@DrawableRes int resId) {
    if (drawerToggle != null) {
      drawerToggle.setDrawerIndicatorEnabled(false);
    }
    actionBar().setHomeButtonEnabled(true);
    actionBar().setIcon(resId);
  }

  private void setAbLogo(@DrawableRes int resId) {
    if (drawerToggle != null) {
      drawerToggle.setDrawerIndicatorEnabled(false);
    }
    actionBar().setHomeButtonEnabled(true);
    actionBar().setLogo(resId);
  }

  private void setAbBg(int bg) {
    if (bg == -1) {
      actionBar().setBackgroundDrawable(defAbBg());
    } else {
      //noinspection deprecation
      actionBar().setBackgroundDrawable(getResources().getDrawable(bg));
    }
  }

  private void setAbCustom(int custom) {
    ActionBar actionBar = actionBar();
    boolean hasCustom = custom != -1;
    actionBar.setDisplayShowCustomEnabled(hasCustom);
    if (hasCustom) {
      actionBar.setCustomView(custom);
    } else {
      actionBar.setCustomView(null);
    }
  }

  private void setAbTitles(CharSequence title, CharSequence subTitle,
      CharSequence titleTypefaceAssetPath, CharSequence subTitleTypefaceAssetPath) {
    ActionBar actionBar = actionBar();
    boolean hasTitle = !(title == null || TextUtils.getTrimmedLength(title) == 0);
    boolean hasSub = !(subTitle == null || TextUtils.getTrimmedLength(subTitle) == 0);
    boolean hasTitleTf = !(titleTypefaceAssetPath == null || //
        TextUtils.getTrimmedLength(titleTypefaceAssetPath) == 0);
    boolean hasSubTitleTf = !(subTitleTypefaceAssetPath == null || //
        TextUtils.getTrimmedLength(subTitleTypefaceAssetPath) == 0);
    if (hasTitle && hasTitleTf) {
      title = spanWithTypeface(this, titleTypefaceAssetPath.toString(), title);
    }
    if (hasSub && hasSubTitleTf) {
      subTitle = spanWithTypeface(this, subTitleTypefaceAssetPath.toString(), subTitle);
    }
    actionBar.setDisplayShowTitleEnabled(hasTitle);
    actionBar.setTitle(hasTitle
        ? title
        : "");
    actionBar.setSubtitle(hasSub
        ? subTitle
        : "");
  }

  private void setAbMenu(int[] menuRes) {
    actionBarMenuRes = menuRes;
    invalidateOptionsMenu();
  }

  private String getScopeName() {
    return getLocalClassName() + "-task-" + getTaskId();
  }

  private Integer _abHeight;

  private int abHeight() {
    if (_abHeight != null) return _abHeight;
    TypedArray a = obtainStyledAttributes(new int[] { R.attr.actionBarSize });
    int sz = a.getDimensionPixelSize(0, 0);
    a.recycle();
    if (sz == 0) sz = getResources().getDimensionPixelSize(R.dimen.default_actionbarheight);
    return _abHeight = sz;
  }

  private Drawable.ConstantState _defAbBg;

  private Drawable defAbBg() {
    if (_defAbBg != null) {
      return _defAbBg.newDrawable(getResources());
    }
    TypedArray a = obtainStyledAttributes(new int[] { R.attr.actionBarStyle });
    int style = a.getResourceId(0, 0);
    a.recycle();
    // ---
    a = obtainStyledAttributes(style, new int[] { R.attr.background });
    Drawable db = a.getDrawable(0);
    a.recycle();
    // ---
    if (db == null) throw new RuntimeException("need default action bar background accessible.");
    _defAbBg = db.getConstantState();
    return db;
  }

  private Boolean _buildConfigDebug = null;

  protected boolean buildConfigDebug() {
    if (_buildConfigDebug == null) {
      try {
        _buildConfigDebug = (boolean) Class.forName(getPackageName() + ".BuildConfig")
            .getDeclaredField("DEBUG")
            .get(null);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return _buildConfigDebug;
  }

  private int displayWidth() {
    Display display = getWindowManager().getDefaultDisplay();
    Point size = new Point();
    display.getSize(size);
    return size.x;
  }

  private void setProperMaterialDrawerWidth(View drawerView) {
    int actionBarSize = abHeight();
    drawerView.getLayoutParams().width =
        Math.min(displayWidth() - actionBarSize, 6 * actionBarSize);
  }
}
