package li.vin.appcore.mortarflow.presenter;

import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import li.vin.appcore.screenview.DrawerScreenView;
import li.vin.appcore.screenview.ScreenViewPresenter;

/** Allows shared configuration of the Android Drawer. */
public class DrawerPresenter extends ScreenViewPresenter<DrawerScreenView> {

  @Module
  public static class DrawerPresenterModule {
    @Provides
    @Singleton
    DrawerPresenter provideDrawerPresenter() {
      return new DrawerPresenter();
    }
  }

  protected DrawerPresenter() {
  }

  @Override
  protected void onAttachedToWindow(@NonNull DrawerScreenView view) {
    super.onAttachedToWindow(view);
    DrawerLayout drawerLayout = findDrawerLayout(view);
    view.setDrawerLayout(drawerLayout);
    view.setDrawerView(drawerLayout.getChildAt(1));
  }

  @Override
  protected void onDetachedFromWindow(@NonNull DrawerScreenView view) {
    super.onDetachedFromWindow(view);
    view.setDrawerView(null);
    view.setDrawerLayout(null);
  }

  public void openDrawer() {
    DrawerScreenView view = getView();
    if (view == null) return;
    DrawerLayout drawerLayout = view.getDrawerLayout();
    android.view.View drawerView = view.getDrawerView();
    if (drawerLayout != null && drawerView != null) {
      drawerLayout.openDrawer(drawerView);
    }
  }

  public void closeDrawer() {
    DrawerScreenView view = getView();
    if (view == null) return;
    DrawerLayout drawerLayout = view.getDrawerLayout();
    android.view.View drawerView = view.getDrawerView();
    if (drawerLayout != null && drawerView != null) {
      drawerLayout.closeDrawer(drawerView);
    }
  }

  public boolean isDrawerOpen() {
    DrawerScreenView view = getView();
    if (view == null) return false;
    DrawerLayout drawerLayout = view.getDrawerLayout();
    android.view.View drawerView = view.getDrawerView();
    return drawerLayout != null && drawerView != null && drawerLayout.isDrawerOpen(drawerView);
  }

  public void setDrawerLockMode(int lockMode) {
    DrawerScreenView view = getView();
    if (view == null) return;
    DrawerLayout drawerLayout = view.getDrawerLayout();
    android.view.View drawerView = view.getDrawerView();
    if (drawerLayout != null && drawerView != null) {
      drawerLayout.setDrawerLockMode(lockMode, drawerView);
    }
  }

  public void onDrawerSlide(@NonNull DrawerScreenView view, View drawerView, float slideOffset) {

  }

  public void onDrawerOpened(@NonNull DrawerScreenView view, View drawerView) {

  }

  public void onDrawerClosed(@NonNull DrawerScreenView view, View drawerView) {

  }

  public void onDrawerStateChanged(@NonNull DrawerScreenView view, int newState) {

  }

  protected static DrawerLayout findDrawerLayout(DrawerScreenView view) {
    ViewGroup parent = (ViewGroup) view.getParent();
    while (parent != null && !(parent instanceof DrawerLayout)) {
      parent = (ViewGroup) parent.getParent();
    }
    return (DrawerLayout) parent;
  }
}
