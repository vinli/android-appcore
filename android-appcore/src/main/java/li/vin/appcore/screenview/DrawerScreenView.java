package li.vin.appcore.screenview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.View;
import li.vin.appcore.mortarflow.presenter.DrawerPresenter;

/**
 * Created by christophercasey on 9/10/15.
 */
public abstract class DrawerScreenView
    extends LinearLayoutScreenView<DrawerScreenView, DrawerPresenter>
    implements DrawerLayout.DrawerListener {

  private DrawerLayout drawerLayout;
  private View drawerView;

  public DrawerScreenView(Context context) {
    super(context);
  }

  public DrawerScreenView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public DrawerScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @SuppressWarnings("unused")
  public DrawerScreenView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
  }

  @Nullable
  public final DrawerLayout getDrawerLayout() {
    return drawerLayout;
  }

  @Nullable
  public final View getDrawerView() {
    return drawerView;
  }

  public final void setDrawerLayout(DrawerLayout drawerLayout) {
    this.drawerLayout = drawerLayout;
  }

  public final void setDrawerView(View drawerView) {
    this.drawerView = drawerView;
  }

  @Override
  public final void onDrawerSlide(View drawerView, float slideOffset) {
    DrawerPresenter presenter = getPresenter();
    if (presenter != null) presenter.onDrawerSlide(this, drawerView, slideOffset);
  }

  @Override
  public final void onDrawerOpened(View drawerView) {
    DrawerPresenter presenter = getPresenter();
    if (presenter != null) presenter.onDrawerOpened(this, drawerView);
  }

  @Override
  public final void onDrawerClosed(View drawerView) {
    DrawerPresenter presenter = getPresenter();
    if (presenter != null) presenter.onDrawerClosed(this, drawerView);
  }

  @Override
  public final void onDrawerStateChanged(int newState) {
    DrawerPresenter presenter = getPresenter();
    if (presenter != null) presenter.onDrawerStateChanged(this, newState);
  }

  public boolean isDrawerOpen() {
    DrawerPresenter presenter = getPresenter();
    return presenter != null && presenter.isDrawerOpen();
  }

  public void closeDrawer() {
    DrawerPresenter presenter = getPresenter();
    if (presenter != null) {
      presenter.closeDrawer();
    }
  }

  public void openDrawer() {
    DrawerPresenter presenter = getPresenter();
    if (presenter != null) {
      presenter.openDrawer();
    }
  }

  public void setDrawerLockMode(int lockMode) {
    DrawerPresenter presenter = getPresenter();
    if (presenter != null) {
      presenter.setDrawerLockMode(lockMode);
    }
  }
}
