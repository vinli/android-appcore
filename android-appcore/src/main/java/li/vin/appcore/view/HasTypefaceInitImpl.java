package li.vin.appcore.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import li.vin.appcore.R;

import static android.text.TextUtils.getTrimmedLength;
import static li.vin.appcore.TypefaceUtil.getTypeface;

class HasTypefaceInitImpl {
  void init(HasTypeface hasTypeface, Context context, AttributeSet attrs) {
    Typeface tf = null;
    if (attrs != null) {
      TypedArray a = null;
      String tfAssetPath = null;
      try {
        a = context.obtainStyledAttributes(attrs, R.styleable.TextViewTF);
        tfAssetPath = a.getString(R.styleable.TextViewTF_typefaceAsset);
      } finally {
        if (a != null) a.recycle();
      }
      if (tfAssetPath != null && getTrimmedLength(tfAssetPath) != 0) {
        tf = getTypeface(context, tfAssetPath);
      }
    }
    if (tf == null) tf = TextViewTF.getDefaultTypeface();
    if (tf != null) hasTypeface.setTypeface(tf);
  }
}
