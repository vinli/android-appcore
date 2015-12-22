package li.vin.appcore.view;

import android.content.Context;
import android.util.AttributeSet;

public class SwitchCompatTF extends android.support.v7.widget.SwitchCompat implements HasTypeface {

  private final HasTypefaceInitImpl initializer = new HasTypefaceInitImpl();

  public SwitchCompatTF(Context context) {
    super(context);
    initializer.init(this, context, null);
  }

  public SwitchCompatTF(Context context, AttributeSet attrs) {
    super(context, attrs);
    initializer.init(this, context, attrs);
  }

  public SwitchCompatTF(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initializer.init(this, context, attrs);
  }
}
