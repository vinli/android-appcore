package li.vin.appcore.segue;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import dagger.Module;
import dagger.Provides;
import flow.Flow;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;

import static li.vin.appcore.segue.SegueFactories.FADE;

/**
 * Created by christophercasey on 9/4/15.
 */
public final class Segues {

  @Module
  public static class SeguesModule {
    @Provides
    @Singleton
    Segues provideSegues() {
      return new Segues();
    }
  }

  private final Map<TraversalInfo, SegueFactory> segueFactoryMap = new HashMap<>();
  private final SegueFactory defaultFactory;

  public Segues(@NonNull SegueFactory<View, View> defaultFactory) {
    this.defaultFactory = defaultFactory;
  }

  public Segues() {
    this(FADE);
  }

  public <F extends View, T extends View> void putFactory(Class<? extends ViewGroup> containerCls,
      Class<? extends F> fromPathCls, Class<? extends T> toPathCls,
      SegueFactory<F, T> segueFactory) {
    segueFactoryMap.put(new TraversalInfo(containerCls, fromPathCls, toPathCls), segueFactory);
  }

  public <V extends View> void putBiDirectionalFactory(Class<? extends ViewGroup> containerCls,
      Class<? extends V> fromPathCls, Class<? extends V> toPathCls,
      SegueFactory<V, V> segueFactory) {
    segueFactoryMap.put(new TraversalInfo(containerCls, fromPathCls, toPathCls), segueFactory);
    segueFactoryMap.put(new TraversalInfo(containerCls, toPathCls, fromPathCls), segueFactory);
  }

  @SuppressWarnings("unchecked")
  public
  @NonNull
  Animator getSegue(ViewGroup container, View from, View to, Flow.Direction direction) {
    SegueFactory factory = segueFactoryMap.get(
        new TraversalInfo(container.getClass(), from.getClass(), to.getClass()));
    if (factory == null) factory = defaultFactory;

    Animator segue = factory.createSegue(from, to, direction);
    if (segue == null) segue = defaultFactory.createSegue(from, to, direction);
    if (segue == null) {
      throw new NullPointerException(
          "null segue produced by factory " + factory.getClass().getName());
    }

    segue.addListener(new RestoreViewPropsAdapter(from, to));
    return segue;
  }

  private static final class RestoreViewPropsAdapter extends AnimatorListenerAdapter {
    final WeakReference<View> v1WeakReference;
    final WeakReference<View> v2WeakReference;

    RestoreViewPropsAdapter(View v1, View v2) {
      v1WeakReference = new WeakReference<>(v1);
      v2WeakReference = new WeakReference<>(v2);
    }

    @Override
    public void onAnimationEnd(Animator animation) {
      restoreViewProps(v1WeakReference.get());
      restoreViewProps(v2WeakReference.get());
    }

    private static void restoreViewProps(View v) {
      if (v == null) return;
      v.setAlpha(1f);
      v.setTranslationX(0f);
      v.setTranslationY(0f);
      v.setScaleX(1f);
      v.setScaleY(1f);
    }
  }

  private static final class TraversalInfo {
    final Class<? extends ViewGroup> containerClass;
    final Class<? extends View> fromPathClass;
    final Class<? extends View> toClassPath;

    TraversalInfo(Class<? extends ViewGroup> containerClass, Class<? extends View> fromPathClass,
        Class<? extends View> toClassPath) {
      this.containerClass = containerClass;
      this.fromPathClass = fromPathClass;
      this.toClassPath = toClassPath;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      TraversalInfo that = (TraversalInfo) o;

      return !(containerClass != null
          ? !containerClass.equals(that.containerClass)
          : that.containerClass != null) && !(fromPathClass != null
          ? !fromPathClass.equals(that.fromPathClass)
          : that.fromPathClass != null) && !(toClassPath != null
          ? !toClassPath.equals(that.toClassPath)
          : that.toClassPath != null);
    }

    @Override
    public int hashCode() {
      int result = containerClass != null
          ? containerClass.hashCode()
          : 0;
      result = 31 * result + (fromPathClass != null
          ? fromPathClass.hashCode()
          : 0);
      result = 31 * result + (toClassPath != null
          ? toClassPath.hashCode()
          : 0);
      return result;
    }
  }
}
