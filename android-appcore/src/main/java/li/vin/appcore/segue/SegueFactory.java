package li.vin.appcore.segue;

import android.animation.Animator;
import android.view.View;
import flow.Flow;

/**
 * Created by christophercasey on 9/4/15.
 */
public interface SegueFactory<F extends View, T extends View> {
  Animator createSegue(F from, T to, Flow.Direction direction);
}
