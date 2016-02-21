package li.vin.appcore.view;

import android.content.Context;
import android.util.AttributeSet;

public class AppCompatEditTextTF extends android.support.v7.widget.AppCompatEditText
    implements HasTypeface {

  private final HasTypefaceInitImpl initializer = new HasTypefaceInitImpl();

  public AppCompatEditTextTF(Context context) {
    super(context);
    initializer.init(this, context, null);
  }

  public AppCompatEditTextTF(Context context, AttributeSet attrs) {
    super(context, attrs);
    initializer.init(this, context, attrs);
  }

  public AppCompatEditTextTF(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initializer.init(this, context, attrs);
  }

  @Override
  public void setSelection(int index) {
    try {
      super.setSelection(index);
    } catch (Exception ignored) {
      // stop older android versions from crashing on certain accessibility actions
    }
  }

  @Override
  public void setSelection(int start, int stop) {
    try {
      super.setSelection(start, stop);
    } catch (Exception ignored) {
      // stop older android versions from crashing on certain accessibility actions
    }
  }
}
