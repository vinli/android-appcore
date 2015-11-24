package li.vin.appcore.mortarflow;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import flow.path.PathContextFactory;
import java.util.HashMap;
import java.util.Map;

import static li.vin.appcore.mortarflow.Layouts.getLayout;

/**
 * Created by christophercasey on 9/8/15.
 */
public final class MortarInflater {

  private static final Map<PathContextFactory, MortarInflater> INFLATER_MAP = new HashMap<>();

  public static MortarInflater from(@NonNull PathContextFactory contextFactory) {
    MortarInflater inflater = INFLATER_MAP.get(contextFactory);
    if (inflater == null) {
      INFLATER_MAP.put(contextFactory, inflater = new MortarInflater(contextFactory));
    }
    return inflater;
  }

  // -----

  private final PathContextFactory contextFactory;

  private MortarInflater(@NonNull PathContextFactory contextFactory) {
    this.contextFactory = contextFactory;
  }

  public View inflate(@NonNull Screen screen, @NonNull ViewGroup container) {
    Context screenContext = contextFactory.setUpContext(screen, container.getContext());
    return LayoutInflater.from(screenContext).inflate(getLayout(screen), container, false);
  }

  public void destroyScope(View v) {
    contextFactory.tearDownContext(v.getContext());
  }
}
