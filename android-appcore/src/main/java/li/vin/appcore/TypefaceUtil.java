package li.vin.appcore;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;
import android.view.View;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE;

@SuppressWarnings("unused")
public final class TypefaceUtil {

  /** Always cache created Typefaces to avoid mem leaks on older Android versions. */
  private static final class TypefaceCacheHolder {
    private static final Map<Object, Typeface> fontCache = new HashMap<>();
  }

  @NonNull
  public static Typeface getTypeface(@NonNull Context context, @NonNull String assetPath) {
    // Note - assumption here is that there won't be any need to grap typefaces off the ui thread,
    // since this is definitely view logic. if a legit use case comes up that requires us to lift
    // this restriction, can consider refactoring cache into a concurrent structure, accounting for
    // atomicity, etc - but keep this logic as simple as possible until that becomes a real issue.
    assertUiThread();
    Typeface tf = TypefaceCacheHolder.fontCache.get(assetPath);
    if (tf == null) {
      TypefaceCacheHolder.fontCache.put(assetPath,
          tf = Typeface.createFromAsset(context.getApplicationContext().getAssets(), assetPath));
    }
    if (tf == null) {
      throw new NullPointerException("null tf, should never happen.");
    }
    return tf;
  }

  /**
   * Get a Typeface that's already been loaded, or else throw unchecked exception. Use variation
   * that takes Context param if lazy loading Typeface is needed - this variant is only for when
   * caller is sure that the Typeface has already been loaded.
   *
   * @see #getTypeface(Context, String)
   */
  @NonNull
  public static Typeface getTypeface(@NonNull String assetPath) {
    assertUiThread();
    Typeface tf = TypefaceCacheHolder.fontCache.get(assetPath);
    if (tf == null) {
      throw new NullPointerException("null tf - use getTypeface with context param?");
    }
    return tf;
  }

  public static void setTypeface(@NonNull TextView textView, @NonNull String assetPath, int style) {
    textView.setTypeface(getTypeface(textView.getContext(), assetPath), style);
  }

  public static void setTypeface(@NonNull TextView textView, @NonNull String assetPath) {
    textView.setTypeface(getTypeface(textView.getContext(), assetPath));
  }

  public static CharSequence spanWithTypeface(@NonNull Typeface typeface,
      @NonNull CharSequence charSequence, int start, int end, int flags) {
    SpannableStringBuilder ss = new SpannableStringBuilder(charSequence);
    ss.setSpan(new CustomTypefaceSpan(typeface), start, end, flags);
    return ss;
  }

  public static CharSequence spanWithTypeface(@NonNull Typeface typeface,
      @NonNull CharSequence charSequence, int start, int end) {
    return spanWithTypeface(typeface, charSequence, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public static CharSequence spanWithTypeface(@NonNull Typeface typeface,
      @NonNull CharSequence charSequence, int start) {
    return spanWithTypeface(typeface, charSequence, start, charSequence.length(),
        SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public static CharSequence spanWithTypeface(@NonNull Typeface typeface,
      @NonNull CharSequence charSequence) {
    return spanWithTypeface(typeface, charSequence, 0, charSequence.length(),
        SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public static CharSequence spanWithTypeface(@NonNull Context context, @NonNull String assetPath,
      @NonNull CharSequence charSequence, int start, int end, int flags) {
    return spanWithTypeface(getTypeface(context, assetPath), charSequence, start, end, flags);
  }

  public static CharSequence spanWithTypeface(@NonNull Context context, @NonNull String assetPath,
      @NonNull CharSequence charSequence, int start, int end) {
    return spanWithTypeface(context, assetPath, charSequence, start, end, SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public static CharSequence spanWithTypeface(@NonNull Context context, @NonNull String assetPath,
      @NonNull CharSequence charSequence, int start) {
    return spanWithTypeface(context, assetPath, charSequence, start, charSequence.length(),
        SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public static CharSequence spanWithTypeface(@NonNull Context context, @NonNull String assetPath,
      @NonNull CharSequence charSequence) {
    return spanWithTypeface(context, assetPath, charSequence, 0, charSequence.length(),
        SPAN_EXCLUSIVE_EXCLUSIVE);
  }

  public static void injectTypeface(@NonNull Typeface typeface, @NonNull View hierarchy,
      @NonNull CharSequence... targetTexts) {
    ArrayList<View> outv = new ArrayList<>();
    if (targetTexts.length == 0) {
      outv.addAll(ViewUtil.findViewsOfType(hierarchy, TextView.class));
    } else {
      for (CharSequence targetText : targetTexts) {
        hierarchy.findViewsWithText(outv, targetText, View.FIND_VIEWS_WITH_TEXT);
      }
    }
    if (!outv.isEmpty()) {
      View v = outv.get(0);
      if (v instanceof TextView) {
        ((TextView) v).setTypeface(typeface);
      }
    }
  }

  public static void injectTypeface(@NonNull Context context, @NonNull String assetPath,
      @NonNull View hierarchy, @NonNull CharSequence... targetText) {
    injectTypeface(getTypeface(context, assetPath), hierarchy, targetText);
  }

  private static void assertUiThread() {
    if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
      throw new RuntimeException("requires ui thread");
    }
  }

  public static class CustomTypefaceSpan extends MetricAffectingSpan {
    private final Typeface typeface;

    public CustomTypefaceSpan(final Typeface typeface) {
      this.typeface = typeface;
    }

    @Override
    public void updateDrawState(final TextPaint drawState) {
      apply(drawState);
    }

    @Override
    public void updateMeasureState(final TextPaint paint) {
      apply(paint);
    }

    private void apply(final Paint paint) {
      final Typeface oldTypeface = paint.getTypeface();
      final int oldStyle = oldTypeface != null
          ? oldTypeface.getStyle()
          : 0;
      final int fakeStyle = oldStyle & ~typeface.getStyle();

      if ((fakeStyle & Typeface.BOLD) != 0) {
        paint.setFakeBoldText(true);
      }

      if ((fakeStyle & Typeface.ITALIC) != 0) {
        paint.setTextSkewX(-0.25f);
      }

      paint.setTypeface(typeface);
    }
  }

  private TypefaceUtil() {
  }
}
