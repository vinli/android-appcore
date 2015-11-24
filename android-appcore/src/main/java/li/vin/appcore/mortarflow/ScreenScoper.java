package li.vin.appcore.mortarflow;

import android.content.Context;
import android.content.res.Resources;
import li.vin.appcore.dagger.DaggerService;
import mortar.MortarScope;

/**
 * Creates {@link MortarScope}s for screens.
 */
public class ScreenScoper {

  public MortarScope getScreenScope(Context context, String name, Screen screen) {
    MortarScope parentScope = MortarScope.getScope(context);
    return getScreenScope(context.getResources(), parentScope, name, screen);
  }

  /**
   * Finds or creates the scope for the given screen. Note that scopes are also created for
   * unannotated screens.
   */
  public MortarScope getScreenScope(Resources resources, MortarScope parentScope, final String name,
      final Screen screen) {
    MortarScope childScope = parentScope.findChild(name);
    if (childScope == null) {
      Object childComponent = screen.createDaggerComponent(resources, parentScope);
      if (childComponent == null) {
        // We need every screen to have a scope, so that anything it injects is scoped.  We need
        // this even if the screen doesn't declare a Component, because Dagger allows injection of
        // objects that are annotated even if they don't appear in a Component.
        childComponent = new Object();
      }
      childScope = parentScope.buildChild()
          .withService(DaggerService.SERVICE_NAME, childComponent)
          .build(name);
    }
    return childScope;
  }
}
