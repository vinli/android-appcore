package li.vin.uiconcepts.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import dagger.Module;
import dagger.Provides;
import li.vin.appcore.mortarflow.MortarContextFactory;
import li.vin.appcore.mortarflow.MortarInflater;
import li.vin.appcore.mortarflow.scope.PerScreen;
import li.vin.uiconcepts.screen.SubScreen;

public class MainPagerAdapter extends PagerAdapter {

  @Module
  public static class MainPagerAdapterModule {
    @Provides
    @PerScreen
    MainPagerAdapter provideMainPagerAdapter(MortarContextFactory contextFactory) {
      return new MainPagerAdapter(contextFactory);
    }
  }

  private final MortarContextFactory contextFactory;

  private MainPagerAdapter(MortarContextFactory contextFactory) {
    this.contextFactory = contextFactory;
  }

  @Override
  public CharSequence getPageTitle(int position) {
    return "Page " + (position + 1);
  }

  @Override
  public int getCount() {
    return 3;
  }

  @Override
  public Object instantiateItem(ViewGroup container, int position) {
    SubScreen subScreen = new SubScreen(getPageTitle(position).toString());
    View v = MortarInflater.from(contextFactory).inflate(subScreen, container);
    container.addView(v);
    return v;
  }

  @Override
  public void destroyItem(ViewGroup container, int position, Object object) {
    View v = (View) object;
    container.removeView(v);
    MortarInflater.from(contextFactory).destroyScope(v);
  }

  @Override
  public boolean isViewFromObject(View view, Object object) {
    return object == view;
  }
}
