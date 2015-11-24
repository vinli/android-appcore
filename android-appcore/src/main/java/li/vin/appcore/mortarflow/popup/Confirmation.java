package li.vin.appcore.mortarflow.popup;

import android.os.Parcel;
import android.os.Parcelable;

/** Messages displayed by a {@link ConfirmerPopup}. */
public class Confirmation implements Parcelable {
  public final String title;
  public final String body;
  public final String confirm;
  public final String cancel;

  public Confirmation(String title, String body, String confirm, String cancel) {
    this.title = title;
    this.body = body;
    this.confirm = confirm;
    this.cancel = cancel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Confirmation that = (Confirmation) o;

    return body.equals(that.body) //
        && cancel.equals(that.cancel) //
        && confirm.equals(that.confirm) //
        && title.equals(that.title);
  }

  private static final int HASH_PRIME = 31;

  @Override
  public int hashCode() {
    int result = title.hashCode();
    result = HASH_PRIME * result + body.hashCode();
    result = HASH_PRIME * result + confirm.hashCode();
    result = HASH_PRIME * result + cancel.hashCode();
    return result;
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel parcel, int i) {
    parcel.writeString(title);
    parcel.writeString(body);
    parcel.writeString(confirm);
    parcel.writeString(cancel);
  }

  @SuppressWarnings("UnusedDeclaration") public static final Creator<Confirmation> CREATOR =
      new Creator<Confirmation>() {
        @Override
        public Confirmation createFromParcel(Parcel parcel) {
          return new Confirmation(parcel.readString(), parcel.readString(), parcel.readString(),
              parcel.readString());
        }

        @Override
        public Confirmation[] newArray(int size) {
          return new Confirmation[size];
        }
      };
}