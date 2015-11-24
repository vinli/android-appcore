package li.vin.uiconcepts.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.AttributeSet;
import butterknife.Bind;
import javax.inject.Inject;
import li.vin.appcore.dagger.DaggerService;
import li.vin.appcore.screenview.LinearLayoutScreenView;
import li.vin.uiconcepts.R;
import li.vin.uiconcepts.adapter.MainPagerAdapter;
import li.vin.uiconcepts.presenter.MainPresenter;
import li.vin.uiconcepts.screen.MainScreen.MainScreenComponent;

/**
 * Created by christophercasey on 9/3/15.
 */
public class MainView extends LinearLayoutScreenView<MainView, MainPresenter> {

  @Inject MainPresenter presenter;

  @Bind(R.id.main_viewpager) ViewPager viewPager;
  @Bind(R.id.sliding_tabs) TabLayout slidingTabs;

  public MainView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean isButtery() {
    return true;
  }

  @Nullable
  @Override
  public MainPresenter getPresenter() {
    return presenter;
  }

  @Override
  public void onCreate(@NonNull Context context) {
    super.onCreate(context);
    DaggerService //
        .<MainScreenComponent>getDaggerComponent(context) //
        .inject(this);
  }

  public void setPagerAdapter(MainPagerAdapter pagerAdapter) {
    if (viewPager.getAdapter() != null) throw new IllegalStateException("adapter already set.");
    viewPager.setAdapter(pagerAdapter);
    slidingTabs.setTabsFromPagerAdapter(pagerAdapter);
    viewPager.addOnPageChangeListener(pageChangeListener);
    slidingTabs.setOnTabSelectedListener(tabSelectedListener);
    pageChangeListener.onPageSelected(viewPager.getCurrentItem());
  }

  public void clearPagerAdapter() {
    viewPager.setAdapter(null);
    viewPager.removeOnPageChangeListener(pageChangeListener);
    slidingTabs.setOnTabSelectedListener(null);
  }

  private final SimpleOnPageChangeListener pageChangeListener =
      new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
          //noinspection ConstantConditions
          slidingTabs.getTabAt(position).select();
        }
      };

  private final OnTabSelectedListener tabSelectedListener = new OnTabSelectedListener() {
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
      viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
    }
  };
}
