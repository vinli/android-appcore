package li.vin.uiconcepts;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import li.vin.appcore.mortarflow.FramePathContainerView;
import li.vin.appcore.segue.Segues;
import li.vin.uiconcepts.view.MainView;
import li.vin.uiconcepts.view.SplashView;
import li.vin.uiconcepts.view.SubView;

import static li.vin.appcore.segue.SegueFactories.SCALE_DOWN_AND_FADE;
import static li.vin.appcore.segue.SegueFactories.SCALE_UP_AND_FADE;
import static li.vin.appcore.segue.SegueFactories.VERTICAL_UP_SLIDE;

/**
 * Created by christophercasey on 9/3/15.
 */
@Module
public class AppModule {

  @Provides
  @Singleton
  Segues provideSegues() {
    Segues segues = new Segues();
    segues.putFactory(FramePathContainerView.class, SplashView.class, MainView.class,
        VERTICAL_UP_SLIDE);
    segues.putFactory(FramePathContainerView.class, MainView.class, SubView.class,
        SCALE_DOWN_AND_FADE);
    segues.putFactory(FramePathContainerView.class, SubView.class, MainView.class, //
        SCALE_UP_AND_FADE);
    return segues;
  }
}
