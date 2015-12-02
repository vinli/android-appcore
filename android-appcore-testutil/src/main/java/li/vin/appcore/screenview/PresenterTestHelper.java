package li.vin.appcore.screenview;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.squareup.otto.Bus;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import li.vin.appcore.mortarflow.android.OttoService;
import li.vin.appcore.mortarflow.presenter.ActionBarPresenter;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;
import org.powermock.api.mockito.PowerMockito;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("unused")
public final class PresenterTestHelper {

  public static void setVar(@NonNull Object obj, @NonNull String name, @Nullable Object val)
      throws Exception {
    for (Class cls = obj.getClass(); //
        cls != null && cls != Object.class; //
        cls = cls.getSuperclass()) {
      for (Field f : cls.getDeclaredFields()) {
        if (f.getName().equals(name)) {
          f.setAccessible(true);
          f.set(obj, val);
          return;
        }
      }
    }
    throw new RuntimeException("no var matching " + name);
  }

  public static <T> T callMethod(@NonNull Object obj, @NonNull String name, Object... args)
      throws Exception {
    for (Class cls = obj.getClass(); //
        cls != null && cls != Object.class; //
        cls = cls.getSuperclass()) {
      for (Method m : cls.getDeclaredMethods()) {
        if (m.getName().equals(name)) {
          m.setAccessible(true);
          //noinspection unchecked
          return (T) m.invoke(obj, args);
        }
      }
    }
    throw new RuntimeException("no method matching " + name);
  }

  public static <T> T getVar(@NonNull Object obj, @NonNull String name) throws Exception {
    for (Class cls = obj.getClass(); //
        cls != null && cls != Object.class; //
        cls = cls.getSuperclass()) {
      for (Field f : cls.getDeclaredFields()) {
        if (f.getName().equals(name)) {
          f.setAccessible(true);
          //noinspection unchecked
          return (T) f.get(obj);
        }
      }
    }
    throw new RuntimeException("no var matching " + name);
  }

  public static void verifyCall(@NonNull Object obj, @NonNull VerificationMode mode,
      @NonNull String name, Object... args) throws Exception {
    callMethod(verify(obj, mode), name, args);
  }

  @NonNull
  public static ActionBarPresenter mockActionBarPresenter() throws Exception {
    final AtomicReference<ActionBarPresenter.ConfigBuilder> ref = new AtomicReference<>();
    ref.set(PowerMockito.mock(ActionBarPresenter.ConfigBuilder.class, new Answer() {
      @Override
      public Object answer(InvocationOnMock invocation) throws Throwable {
        return ref.get();
      }
    }));
    ActionBarPresenter abp = mock(ActionBarPresenter.class);
    when(abp.startConfiguration()).thenReturn(ref.get());
    return abp;
  }

  @NonNull
  public static <V extends View> V mockView(@NonNull Class<V> viewClass) throws Exception {
    V v = mock(viewClass);
    Resources res = mock(Resources.class);
    Context ctx = mock(Context.class);
    when(ctx.getResources()).thenReturn(res);
    when(v.getContext()).thenReturn(ctx);
    when(v.getResources()).thenReturn(res);
    return v;
  }

  @NonNull
  public static <V extends View> Bus mockEventBus(@NonNull V v) throws Exception {
    Bus bus = mock(Bus.class);
    //noinspection ResourceType
    when(v.getContext().getSystemService(OttoService.SERVICE_NAME)) //
        .thenReturn(new OttoService(bus));
    return bus;
  }

  public static <P extends ScreenViewPresenter> P testablePresenter(@NonNull Class factoryClass,
      Object... args) throws Exception {
    return PowerMockito.spy((PresenterTestHelper.<P>invokeFactory(factoryClass, args)));
  }

  @NonNull
  public static <P extends ScreenViewPresenter> P withView(@NonNull P pres, @NonNull View v)
      throws Exception {
    PowerMockito.doReturn(v).when(pres, "getView");
    PowerMockito.doReturn(true).when(pres, "hasView");
    return pres;
  }

  @NonNull
  public static <P extends ScreenViewPresenter> P withoutView(@NonNull P pres) throws Exception {
    PowerMockito.doReturn(null).when(pres, "getView");
    PowerMockito.doReturn(false).when(pres, "hasView");
    return pres;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public static <P extends ScreenViewPresenter> P onReceiveTransientParams(P pres, @NonNull View v,
      @Nullable Bundle b) throws Exception {
    withoutView(pres).onReceiveTransientParams(v, b);
    return pres;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public static <P extends ScreenViewPresenter> P onAttachedToWindow(P pres, @NonNull View v)
      throws Exception {
    withView(pres, v).onAttachedToWindow(v);
    return pres;
  }

  @SuppressWarnings("unchecked")
  @NonNull
  public static <P extends ScreenViewPresenter> P onDetachedFromWindow(P pres, @NonNull View v)
      throws Exception {
    onAttachedToWindow(pres, v);
    withoutView(pres).onDetachedFromWindow(v);
    return pres;
  }

  @NonNull
  private static <T> T invokeFactory(Class factoryClass, Object... args) throws Exception {
    Method factoryMethod = factoryClass.getDeclaredMethods()[0];
    factoryMethod.setAccessible(true);
    Object[] factoryMethodParams = buildParams(factoryMethod.getParameterTypes(), args);
    //noinspection unchecked
    T t = (T) factoryMethod.invoke(factoryClass.newInstance(), factoryMethodParams);
    if (t == null) throw new NullPointerException();
    return t;
  }

  @NonNull
  private static Object[] buildParams(Class[] paramTypes, Object... args) {
    List<Object> params = new ArrayList<>();
    for (Class paramType : paramTypes) {
      Object matchingArg = null;
      for (Object arg : args) {
        if (arg.getClass() == paramType) {
          matchingArg = arg;
          break;
        }
      }
      if (matchingArg == null) {
        for (Object arg : args) {
          //noinspection unchecked
          if (paramType.isAssignableFrom(arg.getClass())) {
            matchingArg = arg;
            break;
          }
        }
      }
      if (matchingArg == null) {
        matchingArg = mock(paramType);
      }
      params.add(matchingArg);
    }
    return params.toArray();
  }

  @NonNull
  public static ActionBarConfigVerifier verifyActionBarConfig(
      @NonNull ActionBarPresenter actionBarPresenter) {
    return new ActionBarConfigVerifier(actionBarPresenter);
  }

  public static final class ActionBarConfigVerifier {

    private final ActionBarPresenter actionBarPresenter;
    private boolean title;
    private boolean up;

    private ActionBarConfigVerifier(ActionBarPresenter actionBarPresenter) {
      this.actionBarPresenter = actionBarPresenter;
    }

    public ActionBarConfigVerifier hasTitle() {
      title = true;
      return this;
    }

    public ActionBarConfigVerifier hasUp() {
      up = true;
      return this;
    }

    public void commit() {
      if (title) {
        Mockito.verify(actionBarPresenter.startConfiguration(), times(1)).title(anyString());
      }
      if (up) {
        Mockito.verify(actionBarPresenter.startConfiguration(), times(1)).up();
      }
      Mockito.verify(actionBarPresenter.startConfiguration(), times(1)).commit();
      verifyNoMoreInteractions(actionBarPresenter.startConfiguration());
    }
  }

  private PresenterTestHelper() {
  }
}