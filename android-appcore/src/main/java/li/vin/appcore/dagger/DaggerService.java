package li.vin.appcore.dagger;

import android.content.Context;
import mortar.MortarScope;

/**
 * Created by christophercasey on 9/3/15.
 */
public class DaggerService {
  public static final String SERVICE_NAME = DaggerService.class.getName();

  /**
   * Caller is required to know the type of the component for this context.
   */
  @SuppressWarnings("unchecked") //
  public static <T> T getDaggerComponent(Context context) {
    //noinspection ResourceType
    return (T) context.getSystemService(SERVICE_NAME);
  }

  /**
   * Caller is required to know the type of the component for this context.
   */
  @SuppressWarnings("unchecked") //
  public static <T> T getDaggerComponent(MortarScope mortarScope) {
    //noinspection ResourceType
    return mortarScope.getService(SERVICE_NAME);
  }

  // Don't do this - it pushes errors that could be caught at compile time to runtime.

  ///**
  // * Magic method that creates a component with its dependencies set, by reflection. Relies on
  // * Dagger2 naming conventions.
  // */
  //public static <T> T createComponent(Class<T> componentClass, Object... dependencies) {
  //  String fqn = componentClass.getName();
  //
  //  String packageName = componentClass.getPackage().getName();
  //  // Accounts for inner classes, ie MyApplication$Component
  //  String simpleName = fqn.substring(packageName.length() + 1);
  //  String generatedName = (packageName + ".Dagger" + simpleName).replace('$', '_');
  //
  //  try {
  //    Class<?> generatedClass = Class.forName(generatedName);
  //    Object builder = generatedClass.getMethod("builder").invoke(null);
  //
  //    for (Method method : builder.getClass().getDeclaredMethods()) {
  //      Class<?>[] params = method.getParameterTypes();
  //      if (params.length == 1) {
  //        Class<?> dependencyClass = params[0];
  //        for (Object dependency : dependencies) {
  //          if (dependencyClass.isAssignableFrom(dependency.getClass())) {
  //            method.invoke(builder, dependency);
  //            break;
  //          }
  //        }
  //      }
  //    }
  //    //noinspection unchecked
  //    return (T) builder.getClass().getMethod("build").invoke(builder);
  //  } catch (Exception e) {
  //    throw new RuntimeException(e);
  //  }
  //}
}
