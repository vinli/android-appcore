package li.vin.appcore;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import rx.Observable;
import rx.functions.Action0;

public final class ViewGroupLoader implements Action0 {

  public static <T> Observable<T> wrapAndGo(@NonNull Observable<T> wrapped, @NonNull ViewGroup root,
      @LayoutRes int progressLayout) {
    ViewGroupLoader vgl = new ViewGroupLoader(root, progressLayout, 0, 0);
    return wrapped.doOnTerminate(vgl).doOnUnsubscribe(vgl);
  }

  public static <T> Observable<T> wrapAndGo(@NonNull Observable<T> wrapped, @NonNull ViewGroup root,
      @LayoutRes int progressLayout, long delay, long min) {
    ViewGroupLoader vgl = new ViewGroupLoader(root, progressLayout, delay, min);
    return wrapped.doOnTerminate(vgl).doOnUnsubscribe(vgl);
  }

  public static <T> Observable<T> wrapAndGo(@NonNull Observable<T> wrapped, @NonNull ViewGroup root,
      @LayoutRes int progressLayout, long delay, long min, long delaySub) {
    ViewGroupLoader vgl = new ViewGroupLoader(root, progressLayout, delay, min);
    return wrapped.doOnTerminate(vgl)
        .doOnUnsubscribe(vgl)
        .delaySubscription(delaySub, TimeUnit.MILLISECONDS);
  }

  private ViewGroup root;
  private View progressIndicator;
  private long delay;
  private long min;
  private final List<ViewVis> loadingViews = new ArrayList<>();

  long startTime;

  private ViewGroupLoader(@NonNull ViewGroup root, @LayoutRes int progressLayout, long delay,
      long min) {
    if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
      throw new RuntimeException("not on UI thread.");
    }

    this.root = root;
    this.progressIndicator =
        LayoutInflater.from(root.getContext()).inflate(progressLayout, root, false);
    this.delay = delay;
    this.min = min;

    startLoading();
  }

  private void startLoading() {
    if (root == null || progressIndicator == null || progressIndicator.getParent() != null) return;

    int rootW = root.getWidth();
    int rootH = root.getHeight();
    if (rootW == 0 || rootH == 0 || delay > 0) {
      long delay = this.delay;
      this.delay = 0;
      new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
        @Override
        public void run() {
          startLoading();
        }
      }, delay);
      return;
    }

    int targetW = rootW - (root.getPaddingLeft() + root.getPaddingRight());
    int targetH = rootH - (root.getPaddingTop() + root.getPaddingBottom());

    for (int i = 0; i < root.getChildCount(); i++) {
      View child = root.getChildAt(i);
      loadingViews.add(new ViewVis(child, child.getVisibility()));
      child.setVisibility(View.GONE);
    }

    root.addView(progressIndicator);
    ViewGroup.LayoutParams lp = progressIndicator.getLayoutParams();
    lp.width = targetW;
    lp.height = targetH;

    startTime = System.currentTimeMillis();
  }

  private void stopLoading() {
    if (root == null || progressIndicator == null) return;

    root.removeView(progressIndicator);
    root = null;
    progressIndicator = null;

    for (ViewVis v : loadingViews) {
      v.view.setVisibility(v.vis);
    }
    loadingViews.clear();
  }

  @Override
  public void call() {
    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
      @Override
      public void run() {
        stopLoading();
      }
    }, Math.max(0, min - (System.currentTimeMillis() - startTime)));
  }

  private static final class ViewVis {
    final View view;
    final int vis;

    public ViewVis(View view, int vis) {
      this.view = view;
      this.vis = vis;
    }
  }
}
