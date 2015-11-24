package li.vin.uiconcepts.presenter;

import android.support.annotation.NonNull;
import dagger.Module;
import dagger.Provides;
import flow.Flow;
import li.vin.appcore.mortarflow.popup.Confirmation;
import li.vin.appcore.mortarflow.presenter.ActionBarPresenter;
import li.vin.appcore.mortarflow.presenter.DrawerPresenter;
import li.vin.appcore.mortarflow.scope.PerSubScreen;
import li.vin.appcore.screenview.ScreenViewPresenter;
import li.vin.uiconcepts.R;
import li.vin.uiconcepts.screen.SubScreen;
import li.vin.uiconcepts.view.SubView;
import mortar.PopupPresenter;

public class SubPresenter extends ScreenViewPresenter<SubView> {

  @Module
  public static class SubPresenterModule {
    @Provides
    @PerSubScreen
    SubPresenter provideSubPresenter(String subScreenText, DrawerPresenter drawerPresenter,
        ActionBarPresenter actionBarPresenter) {
      return new SubPresenter(subScreenText, drawerPresenter, actionBarPresenter);
    }
  }

  private final String subScreenText;
  private final DrawerPresenter drawerPresenter;
  private final AppActionBarPresenter actionBarPresenter;
  private final PopupPresenter<Confirmation, Boolean> confirmer;

  private SubPresenter(String subScreenText, DrawerPresenter drawerPresenter,
      ActionBarPresenter actionBarPresenter) {
    this.subScreenText = subScreenText;
    this.drawerPresenter = drawerPresenter;
    this.actionBarPresenter = (AppActionBarPresenter) actionBarPresenter;
    this.confirmer = new PopupPresenter<Confirmation, Boolean>() {
      @Override
      protected void onPopupResult(Boolean confirmed) {
        if (confirmed && SubPresenter.this.hasView()) {
          toastShort(String.format("confirmed from %s!", SubPresenter.this.subScreenText));
        }
      }
    };
  }

  @Override
  protected void onFinishInflate(@NonNull SubView view) {
    if (subScreenText.equals("DEEPLY")) {
      view.hideNavvy();
    }

    view.setSubScreenText(subScreenText);
  }

  @Override
  protected void onAttachedToWindow(@NonNull SubView view) {
    if (subScreenText.equals("DEEPLY")) {
      actionBarPresenter.startConfiguration() //
          .up() //
          .custom(R.layout.ab_custom_view) //
          .commit();
    }

    confirmer.takeView(getView().getConfirmerPopup());
  }

  @Override
  protected void onDetachedFromWindow(@NonNull SubView view) {
    confirmer.dropView(view.getConfirmerPopup());
  }

  public void clickShowy() {
    drawerPresenter.openDrawer();
  }

  public void clickPoppy() {
    confirmer.show(new Confirmation("Title", "Body", "Confirm", "Cancel"));
  }

  public void clickNavvy() {
    Flow.get(getView()).set(new SubScreen("DEEPLY"));
  }
}
