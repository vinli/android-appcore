package li.vin.appcore.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("unused")
public class TextViewTF extends TextView implements HasTypeface {

  private static final AtomicReference<Typeface> defaultTypeface = new AtomicReference<>();

  private final HasTypefaceInitImpl initializer = new HasTypefaceInitImpl();

  public static void setDefaultTypeface(@NonNull Typeface typeface) {
    defaultTypeface.set(typeface);
  }

  @Nullable
  public static Typeface getDefaultTypeface() {
    return defaultTypeface.get();
  }

  public TextViewTF(Context context) {
    super(context);
    initializer.init(this, context, null);
  }

  public TextViewTF(Context context, AttributeSet attrs) {
    super(context, attrs);
    initializer.init(this, context, attrs);
  }

  public TextViewTF(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initializer.init(this, context, attrs);
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  public TextViewTF(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    super(context, attrs, defStyleAttr, defStyleRes);
    initializer.init(this, context, attrs);
  }
}
