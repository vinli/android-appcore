package li.vin.appcore.mortarflow.popup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import mortar.Popup;
import mortar.PopupPresenter;

public class ConfirmerPopup implements Popup<Confirmation, Boolean> {
  private final Context context;

  private AlertDialog dialog;

  public ConfirmerPopup(Context context) {
    this.context = context;
  }

  @Override
  public Context getContext() {
    return context;
  }

  @Override
  public void show(Confirmation info, boolean withFlourish,
      final PopupPresenter<Confirmation, Boolean> presenter) {
    if (dialog != null) throw new IllegalStateException("Already showing, can't show " + info);

    dialog = new AlertDialog.Builder(context).setTitle(info.title)
        .setMessage(info.body)
        .setPositiveButton(info.confirm, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface d, int which) {
            dialog = null;
            presenter.onDismissed(true);
          }
        })
        .setNegativeButton(info.cancel, new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface d, int which) {
            dialog = null;
            presenter.onDismissed(false);
          }
        })
        .setCancelable(true)
        .setOnCancelListener(new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface d) {
            dialog = null;
            presenter.onDismissed(false);
          }
        })
        .show();
  }

  @Override
  public boolean isShowing() {
    return dialog != null;
  }

  @Override
  public void dismiss(boolean withFlourish) {
    dialog.dismiss();
    dialog = null;
  }
}