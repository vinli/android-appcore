package li.vin.appcore.mortarflow;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import flow.Flow;
import flow.path.PathContainerView;
import flow.path.PathContextFactory;
import java.util.Set;
import li.vin.appcore.R;
import li.vin.appcore.mortarflow.android.ActivityResultSupport;
import li.vin.appcore.mortarflow.android.BackSupport;
import li.vin.appcore.mortarflow.android.HandlesActivityResult;
import li.vin.appcore.mortarflow.android.HandlesBack;
import li.vin.appcore.mortarflow.android.HandlesOptionsItemSelected;
import li.vin.appcore.mortarflow.android.HandlesPauseResume;
import li.vin.appcore.mortarflow.android.HandlesSearchSuggestions;
import li.vin.appcore.mortarflow.android.OptionsItemSelectedSupport;
import li.vin.appcore.mortarflow.android.PauseResumeSupport;
import li.vin.appcore.mortarflow.android.SearchSuggestionsSupport;
import li.vin.appcore.segue.Segues;

/** A FrameLayout that can show screens for a {@link Flow}. */
public class FramePathContainerView extends FrameLayout
    implements PathContainerView, HandlesBack, HandlesActivityResult, HandlesOptionsItemSelected,
    HandlesSearchSuggestions, HandlesPauseResume {
  private SimplePathContainer container;
  private boolean disabled;

  public FramePathContainerView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public void init(PathContextFactory contextFactory, Segues segues) {
    if (this.container != null) throw new IllegalStateException("already initialized.");
    this.container = new SimplePathContainer(R.id.screen_switcher_tag,
        flow.path.Path.contextFactory(contextFactory), segues);
  }

  @Override
  public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
    checkInit();
    return !disabled && super.dispatchTouchEvent(ev);
  }

  @Override
  public ViewGroup getContainerView() {
    checkInit();
    return this;
  }

  @Override
  public void dispatch(Flow.Traversal traversal, final Flow.TraversalCallback callback) {
    checkInit();
    disabled = true;
    container.executeTraversal(this, traversal, new Flow.TraversalCallback() {
      @Override
      public void onTraversalCompleted() {
        callback.onTraversalCompleted();
        disabled = false;
      }
    });
  }

  @Override
  public boolean onBackPressed() {
    checkInit();
    return BackSupport.onBackPressed(getCurrentChild());
  }

  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    checkInit();
    return ActivityResultSupport.onActivityResult(getCurrentChild(), requestCode, resultCode, data);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    checkInit();
    return OptionsItemSelectedSupport.onOptionsItemSelected(getCurrentChild(), item);
  }

  @Override
  public boolean onSearchQuerySubmit(@NonNull String query) {
    checkInit();
    return SearchSuggestionsSupport.onSearchQuerySubmit(getCurrentChild(), query);
  }

  @Override
  public boolean onSearchQueryChange(@NonNull String query, @NonNull Set<String> outSet) {
    checkInit();
    return SearchSuggestionsSupport.onSearchQueryChange(getCurrentChild(), query, outSet);
  }

  @Override
  public void onPause() {
    checkInit();
    PauseResumeSupport.onPause(getCurrentChild());
  }

  @Override
  public void onResume() {
    checkInit();
    PauseResumeSupport.onResume(getCurrentChild());
  }

  @Override
  public ViewGroup getCurrentChild() {
    checkInit();
    return (ViewGroup) getContainerView().getChildAt(0);
  }

  private void checkInit() {
    if (container == null) throw new IllegalStateException("needs init.");
  }
}
