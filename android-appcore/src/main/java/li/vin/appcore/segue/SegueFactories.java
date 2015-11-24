package li.vin.appcore.segue;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import flow.Flow;

/**
 * Created by christophercasey on 9/4/15.
 */
public final class SegueFactories {

  public static final SegueFactory<View, View> HORIZONTAL_LEFT_SLIDE =
      new SegueFactory<View, View>() {
        @Override
        public Animator createSegue(View from, View to, Flow.Direction direction) {
          boolean backward = direction == Flow.Direction.BACKWARD;
          int fromTranslation = backward
              ? from.getWidth()
              : -from.getWidth();
          int toTranslation = backward
              ? -to.getWidth()
              : to.getWidth();

          AnimatorSet set = new AnimatorSet();

          set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, fromTranslation));
          set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, toTranslation, 0));

          return set;
        }
      };

  public static final SegueFactory<View, View> HORIZONTAL_RIGHT_SLIDE =
      new SegueFactory<View, View>() {
        @Override
        public Animator createSegue(View from, View to, Flow.Direction direction) {
          boolean backward = direction == Flow.Direction.BACKWARD;
          int fromTranslation = backward
              ? -from.getWidth()
              : from.getWidth();
          int toTranslation = backward
              ? to.getWidth()
              : -to.getWidth();

          AnimatorSet set = new AnimatorSet();

          set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_X, fromTranslation));
          set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_X, toTranslation, 0));

          return set;
        }
      };

  public static final SegueFactory<View, View> VERTICAL_UP_SLIDE = //
      new SegueFactory<View, View>() {
        @Override
        public Animator createSegue(View from, View to, Flow.Direction direction) {
          boolean backward = direction == Flow.Direction.BACKWARD;
          int fromTranslation = backward
              ? from.getHeight()
              : -from.getHeight();
          int toTranslation = backward
              ? -to.getHeight()
              : to.getHeight();

          AnimatorSet set = new AnimatorSet();

          set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_Y, fromTranslation));
          set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_Y, toTranslation, 0));

          return set;
        }
      };

  public static final SegueFactory<View, View> VERTICAL_DOWN_SLIDE =
      new SegueFactory<View, View>() {
        @Override
        public Animator createSegue(View from, View to, Flow.Direction direction) {
          boolean backward = direction == Flow.Direction.BACKWARD;
          int fromTranslation = backward
              ? -from.getHeight()
              : from.getHeight();
          int toTranslation = backward
              ? to.getHeight()
              : -to.getHeight();

          AnimatorSet set = new AnimatorSet();

          set.play(ObjectAnimator.ofFloat(from, View.TRANSLATION_Y, fromTranslation));
          set.play(ObjectAnimator.ofFloat(to, View.TRANSLATION_Y, toTranslation, 0));

          return set;
        }
      };

  public static final SegueFactory<View, View> FADE = //
      new SegueFactory<View, View>() {
        @Override
        public Animator createSegue(View from, View to, Flow.Direction direction) {
          AnimatorSet set = new AnimatorSet();
          set.play(ObjectAnimator.ofFloat(to, View.ALPHA, 0f, 1f));
          return set;
        }
      };

  public static final SegueFactory<View, View> SCALE_UP_AND_FADE = //
      new SegueFactory<View, View>() {
        @Override
        public Animator createSegue(View from, View to, Flow.Direction direction) {
          AnimatorSet set = new AnimatorSet();
          set.play(ObjectAnimator.ofFloat(to, View.ALPHA, 0f, 1f));
          set.play(ObjectAnimator.ofFloat(to, View.SCALE_X, 0.5f, 1f));
          set.play(ObjectAnimator.ofFloat(to, View.SCALE_Y, 0.5f, 1f));
          return set;
        }
      };

  public static final SegueFactory<View, View> SCALE_DOWN_AND_FADE = //
      new SegueFactory<View, View>() {
        @Override
        public Animator createSegue(View from, View to, Flow.Direction direction) {
          AnimatorSet set = new AnimatorSet();
          set.play(ObjectAnimator.ofFloat(to, View.ALPHA, 0f, 1f));
          set.play(ObjectAnimator.ofFloat(to, View.SCALE_X, 1.5f, 1f));
          set.play(ObjectAnimator.ofFloat(to, View.SCALE_Y, 1.5f, 1f));
          return set;
        }
      };

  private SegueFactories() {
  }
}
