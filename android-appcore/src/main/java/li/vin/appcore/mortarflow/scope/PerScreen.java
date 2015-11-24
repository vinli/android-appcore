package li.vin.appcore.mortarflow.scope;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.inject.Scope;
import li.vin.appcore.mortarflow.Screen;

/** Default scope for {@link Screen} - should be used for any non-nested screen. */
@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerScreen {
}
