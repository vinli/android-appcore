package li.vin.appcore.screenview;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import li.vin.appcore.screenview.RecyclerViewScreenView.ChildRemovalNotifyAdapter;

public class RecyclerViewDelayedRestoreState extends RecyclerView {

  private Parcelable delayedState;
  private boolean stateRestored;

  public RecyclerViewDelayedRestoreState(Context context) {
    super(context);
  }

  public RecyclerViewDelayedRestoreState(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public RecyclerViewDelayedRestoreState(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @CallSuper
  @Override
  protected void onRestoreInstanceState(Parcelable state) {
    delayedState = state;
    super.onRestoreInstanceState(state);
  }

  @CallSuper
  @Override
  public void setAdapter(Adapter adapter) {
    if (getAdapter() != null) getAdapter().unregisterAdapterDataObserver(dataObserver);
    if (adapter != null) adapter.registerAdapterDataObserver(dataObserver);
    doRestoreState(adapter);
    super.setAdapter(adapter);
  }

  private void doRestoreState(Adapter adapter) {
    if (stateRestored || adapter == null || adapter.getItemCount() == 0) return;
    stateRestored = true;
    if (delayedState != null) {
      super.onRestoreInstanceState(delayedState);
      delayedState = null;
    }
  }

  private final AdapterDataObserver dataObserver = new AdapterDataObserver() {
    @Override
    public void onChanged() {
      doRestoreState(getAdapter());
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount) {
      doRestoreState(getAdapter());
    }

    @Override
    public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
      doRestoreState(getAdapter());
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
      doRestoreState(getAdapter());
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
      doRestoreState(getAdapter());
    }

    @Override
    public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
      doRestoreState(getAdapter());
    }
  };

  // --------------------------------------------------------- //
  // --------------------------------------------------------- //

  protected void onPreChildRemoval(@NonNull View child) {
    if (getAdapter() instanceof ChildRemovalNotifyAdapter) {
      ((ChildRemovalNotifyAdapter) getAdapter()).onPreChildRemoval(child);
    }
  }

  private void dispatchPreChildRemoval(View child) {
    if (child != null && child.getParent() != null) {
      onPreChildRemoval(child);
    }
  }

  @Override
  public void removeViewAt(int index) {
    dispatchPreChildRemoval(getChildAt(index));
    super.removeViewAt(index);
  }

  @Override
  public void removeView(@NonNull View view) {
    dispatchPreChildRemoval(view);
    super.removeView(view);
  }

  @Override
  public void removeViews(int start, int count) {
    for (int i = start; i < start + count; i++) {
      dispatchPreChildRemoval(getChildAt(i));
    }
    super.removeViews(start, count);
  }

  @Override
  public void removeViewInLayout(@NonNull View view) {
    dispatchPreChildRemoval(view);
    super.removeViewInLayout(view);
  }

  @Override
  public void removeAllViewsInLayout() {
    for (int i = 0; i < getChildCount(); i++) {
      dispatchPreChildRemoval(getChildAt(i));
    }
    super.removeAllViewsInLayout();
  }

  @Override
  public void removeViewsInLayout(int start, int count) {
    for (int i = start; i < start + count; i++) {
      dispatchPreChildRemoval(getChildAt(i));
    }
    super.removeViewsInLayout(start, count);
  }

  @Override
  protected void removeDetachedView(View child, boolean animate) {
    dispatchPreChildRemoval(child);
    super.removeDetachedView(child, animate);
  }
}
