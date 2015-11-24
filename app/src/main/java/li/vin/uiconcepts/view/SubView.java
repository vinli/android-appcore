package li.vin.uiconcepts.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import javax.inject.Inject;
import li.vin.appcore.dagger.DaggerService;
import li.vin.appcore.mortarflow.popup.ConfirmerPopup;
import li.vin.appcore.screenview.LinearLayoutScreenView;
import li.vin.uiconcepts.R;
import li.vin.uiconcepts.presenter.SubPresenter;
import li.vin.uiconcepts.screen.SubScreen.SubScreenComponent;

/**
 * Created by christophercasey on 9/3/15.
 */
public class SubView extends LinearLayoutScreenView<SubView, SubPresenter> {

  @Inject SubPresenter presenter;

  @Bind(R.id.sub_showy) TextView showy;
  @Bind(R.id.sub_navvy) TextView navvy;

  private final ConfirmerPopup confirmerPopup;

  public SubView(Context context, AttributeSet attrs) {
    super(context, attrs);
    confirmerPopup = new ConfirmerPopup(context);
  }

  @Override
  public boolean isButtery() {
    return true;
  }

  @Nullable
  @Override
  public SubPresenter getPresenter() {
    return presenter;
  }

  @Override
  public void onCreate(@NonNull Context context) {
    super.onCreate(context);
    DaggerService //
        .<SubScreenComponent>getDaggerComponent(context) //
        .inject(this);
  }

  @OnClick(R.id.sub_showy)
  void showy() {
    presenter.clickShowy();
  }

  @OnClick(R.id.sub_poppy)
  void poppy() {
    presenter.clickPoppy();
  }

  @OnClick(R.id.sub_navvy)
  void navvy() {
    presenter.clickNavvy();
  }

  public void setSubScreenText(String subScreenText) {
    showy.setText(subScreenText);
  }

  public ConfirmerPopup getConfirmerPopup() {
    return confirmerPopup;
  }

  public void hideNavvy() {
    navvy.setVisibility(GONE);
  }
}
