package li.vin.appcore.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import static li.vin.appcore.TypefaceUtil.injectTypeface;

public final class TabLayoutTF extends android.support.design.widget.TabLayout
    implements HasTypeface {

  private Typeface typeface;
  private final HasTypefaceInitImpl initializer = new HasTypefaceInitImpl();
  private boolean allowHierarchyListener;

  public TabLayoutTF(Context context) {
    super(context);
    initializer.init(this, context, null);
  }

  public TabLayoutTF(Context context, AttributeSet attrs) {
    super(context, attrs);
    initializer.init(this, context, attrs);
  }

  public TabLayoutTF(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initializer.init(this, context, attrs);
  }

  @Override
  public void setTypeface(Typeface tf) {
    typeface = tf;
    if (tf == null) return;
    injectTypeface(tf, this);
    allowHierarchyListener = true;
    HierarchyTreeChangeListener.assignRecursively(this,
        HierarchyTreeChangeListener.wrap(new TypefaceHierarchyListener(typeface)));
    allowHierarchyListener = false;
  }

  @Override
  public Typeface getTypeface() {
    return typeface;
  }

  @Override
  public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
    if (!allowHierarchyListener) throw new IllegalStateException("not allowed.");
    super.setOnHierarchyChangeListener(listener);
  }

  private static class TypefaceHierarchyListener implements OnHierarchyChangeListener {
    private final Typeface typeface;

    TypefaceHierarchyListener(Typeface typeface) {
      this.typeface = typeface;
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
      if (child instanceof TextView) {
        ((TextView) child).setTypeface(typeface);
      }
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
      // no-op
    }
  }
}
