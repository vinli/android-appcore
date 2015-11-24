package li.vin.appcore.mortarflow.android;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import li.vin.appcore.dagger.DaggerService;
import mortar.MortarScope;

public abstract class MortarFlowApp extends Application {

  private MortarScope rootScope;
  private boolean buildingScope;

  @Override
  public final Object getSystemService(String name) {
    if (rootScope == null && !buildingScope) {
      buildingScope = true;
      MortarScope.Builder builder = MortarScope.buildRootScope()
          .withService(DaggerService.SERVICE_NAME, createAppComponent());
      for (MortarService service : calcServices()) {
        builder = builder.withService(service.serviceName, service.service);
      }
      rootScope = builder.build("Root");
      buildingScope = false;
    }
    return (rootScope != null && rootScope.hasService(name))
        ? rootScope.getService(name)
        : super.getSystemService(name);
  }

  @NonNull
  protected abstract Object createAppComponent();

  @Nullable
  protected Collection<MortarService> createMortarServices() {
    return null;
  }

  @NonNull
  private Set<MortarService> calcServices() {
    Collection<MortarService> services = createMortarServices();
    return services != null
        ? new HashSet<>(services)
        : new HashSet<MortarService>();
  }

  protected static final class MortarService {
    @NonNull public final String serviceName;
    @NonNull public final Object service;

    public MortarService(@NonNull String serviceName, @NonNull Object service) {
      this.serviceName = serviceName;
      this.service = service;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      MortarService service = (MortarService) o;
      return serviceName.equals(service.serviceName);
    }

    @Override
    public int hashCode() {
      return serviceName.hashCode();
    }
  }
}
