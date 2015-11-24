package li.vin.appcore.mortarflow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import flow.Flow;
import flow.path.Path;
import flow.path.PathContainer;
import flow.path.PathContext;
import flow.path.PathContextFactory;
import li.vin.appcore.ViewUtil.OnMeasuredCallback;
import li.vin.appcore.screenview.HandlesTransientParams;
import li.vin.appcore.segue.Segues;

import static flow.Flow.Direction.REPLACE;
import static li.vin.appcore.ViewUtil.waitForMeasure;
import static li.vin.appcore.mortarflow.Layouts.getLayout;

/**
 * Provides provides segue transitions. Saves and restores view state.
 * Uses {@link PathContext} to allow customized sub-containers.
 */
public class SimplePathContainer extends PathContainer {
  private final PathContextFactory contextFactory;
  private final Segues segues;

  public SimplePathContainer(int tagKey, PathContextFactory contextFactory, Segues segues) {
    super(tagKey);
    this.contextFactory = contextFactory;
    this.segues = segues;
  }

  @Override
  protected void performTraversal(final ViewGroup containerView,
      final TraversalState traversalState, final Flow.Direction direction,
      final Flow.TraversalCallback callback) {

    final PathContext context;
    final PathContext oldPath;
    if (containerView.getChildCount() > 0) {
      oldPath = PathContext.get(containerView.getChildAt(0).getContext());
    } else {
      oldPath = PathContext.root(containerView.getContext());
    }

    Path to = traversalState.toPath();

    View newView;
    context = PathContext.create(oldPath, to, contextFactory);
    int layout = getLayout(to);
    newView = LayoutInflater.from(context) //
        .cloneInContext(context) //
        .inflate(layout, containerView, false);

    View fromView = null;
    if (traversalState.fromPath() != null) {
      fromView = containerView.getChildAt(0);
      traversalState.saveViewState(fromView);
    }
    traversalState.restoreViewState(newView);

    Bundle optParams = null;
    if (fromView instanceof HandlesTransientParams) {
      optParams = ((HandlesTransientParams) fromView).onProvideTransientParams(newView, null);
    }
    if (newView instanceof HandlesTransientParams) {
      ((HandlesTransientParams) newView).onReceiveTransientParams(optParams);
    }

    if (fromView == null || direction == REPLACE) {
      containerView.removeAllViews();
      containerView.addView(newView);
      oldPath.destroyNotIn(context, contextFactory);
      callback.onTraversalCompleted();
    } else {
      containerView.addView(newView);
      final View finalFromView = fromView;
      waitForMeasure(newView, new OnMeasuredCallback() {
        @Override
        public void onMeasured(View view, int width, int height) {
          runAnimation(containerView, finalFromView, view, direction, new Flow.TraversalCallback() {
            @Override
            public void onTraversalCompleted() {
              containerView.removeView(finalFromView);
              oldPath.destroyNotIn(context, contextFactory);
              callback.onTraversalCompleted();
            }
          });
        }
      });
    }
  }

  private void runAnimation(final ViewGroup container, final View from, final View to,
      Flow.Direction direction, final Flow.TraversalCallback callback) {
    Animator animator = segues.getSegue(container, from, to, direction);
    animator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        container.removeView(from);
        callback.onTraversalCompleted();
      }
    });
    animator.start();
  }
}
