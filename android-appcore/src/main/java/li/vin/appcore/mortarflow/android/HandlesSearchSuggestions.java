package li.vin.appcore.mortarflow.android;

import android.support.annotation.NonNull;
import java.util.Set;

public interface HandlesSearchSuggestions {
  boolean onSearchQuerySubmit(@NonNull String query);
  boolean onSearchQueryChange(@NonNull String query, @NonNull Set<String> outSet);
}
