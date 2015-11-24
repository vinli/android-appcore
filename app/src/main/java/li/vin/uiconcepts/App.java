package li.vin.uiconcepts;

import android.support.annotation.NonNull;
import li.vin.appcore.mortarflow.android.MortarFlowApp;

/**
 * Created by christophercasey on 9/2/15.
 */
public class App extends MortarFlowApp {

  @NonNull
  @Override
  protected Object createAppComponent() {
    return DaggerAppComponent.builder() //
        .appModule(new AppModule()) //
        .build();
  }
}
