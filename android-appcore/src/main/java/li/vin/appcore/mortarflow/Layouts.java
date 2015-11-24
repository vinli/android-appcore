package li.vin.appcore.mortarflow;

import android.support.annotation.NonNull;
import flow.path.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class Layouts {

  private static final Map<Class, Integer> PATH_LAYOUT_CACHE = new LinkedHashMap<>();

  public static int getLayout(@NonNull Path path) {
    Class pathType = path.getClass();
    Integer layoutResId = PATH_LAYOUT_CACHE.get(pathType);
    if (layoutResId == null) {
      Layout layout = (Layout) pathType.getAnnotation(Layout.class);
      if (layout == null) {
        throw new IllegalArgumentException(
            String.format("@%s annotation not found on class %s", Layout.class.getSimpleName(),
                pathType.getName()));
      }
      layoutResId = layout.value();
      PATH_LAYOUT_CACHE.put(pathType, layoutResId);
    }
    return layoutResId;
  }

  private Layouts() {
  }
}
