package li.vin.appcore.mortarflow;

import android.content.Context;
import android.content.ContextWrapper;
import android.view.LayoutInflater;
import dagger.Module;
import dagger.Provides;
import flow.path.Path;
import flow.path.PathContextFactory;
import javax.inject.Singleton;
import mortar.MortarScope;

public final class MortarContextFactory implements PathContextFactory {

  @Module
  public static class MortarContextFactoryModule {
    @Provides
    @Singleton
    MortarContextFactory provideMortarContextFactory() {
      return new MortarContextFactory();
    }
  }

  private final ScreenScoper screenScoper = new ScreenScoper();

  private MortarContextFactory() {
  }

  @Override
  public Context setUpContext(Path path, Context parentContext) {
    if (!(path instanceof Screen)) throw new RuntimeException("Path must be a Screen.");
    Screen screen = (Screen) path;
    MortarScope screenScope =
        screenScoper.getScreenScope(parentContext, screen.getScopeName(), screen);
    return new TearDownContext(parentContext, screenScope);
  }

  @Override
  public void tearDownContext(Context context) {
    TearDownContext.destroyScope(context);
  }

  static class TearDownContext extends ContextWrapper {
    private static final String SERVICE = "SNEAKY_MORTAR_PARENT_HOOK";
    private final MortarScope parentScope;
    private LayoutInflater inflater;

    static void destroyScope(Context context) {
      MortarScope.getScope(context).destroy();
    }

    public TearDownContext(Context context, MortarScope scope) {
      super(scope.createContext(context));
      this.parentScope = MortarScope.getScope(context);
    }

    @Override
    public Object getSystemService(String name) {
      if (LAYOUT_INFLATER_SERVICE.equals(name)) {
        if (inflater == null) {
          inflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
        }
        return inflater;
      }

      if (SERVICE.equals(name)) {
        return parentScope;
      }

      return super.getSystemService(name);
    }
  }
}
