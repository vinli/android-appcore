package li.vin.appcore;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by christophercasey on 9/3/15.
 */
public final class ViewUtil {

  public interface OnMeasuredCallback {
    void onMeasured(View view, int width, int height);
  }

  public static void waitForMeasure(final View view, final OnMeasuredCallback callback) {
    int width = view.getWidth();
    int height = view.getHeight();

    if (width > 0 && height > 0) {
      callback.onMeasured(view, width, height);
      return;
    }

    view.getViewTreeObserver().addOnPreDrawListener(new OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        if (observer.isAlive()) observer.removeOnPreDrawListener(this);
        callback.onMeasured(view, view.getWidth(), view.getHeight());
        return true;
      }
    });
  }

  public static <T> Set<T> findViewsOfType(View view, Class<T> type) {
    return findViewsOfType(view, type, null);
  }

  public static <T> Set<T> findViewsOfType(View view, Class<T> type, Set<T> outSet) {
    if (outSet == null) outSet = new HashSet<>();
    if (view == null) return outSet;
    if (type.isInstance(view)) {
      //noinspection unchecked
      outSet.add((T) view);
    }
    if (view instanceof ViewGroup) {
      ViewGroup vg = (ViewGroup) view;
      for (int i = 0; i < vg.getChildCount(); i++) {
        findViewsOfType(vg.getChildAt(i), type, outSet);
      }
    }
    return outSet;
  }

  private ViewUtil() {
  }
}
