package li.vin.appcore.mortarflow.android;

import android.support.annotation.NonNull;
import android.view.View;
import java.util.Set;

public class SearchSuggestionsSupport {

  public static boolean onSearchQueryChange(View childView, @NonNull String query,
      @NonNull Set<String> outSet) {
    if (childView instanceof HandlesSearchSuggestions) {
      if (((HandlesSearchSuggestions) childView).onSearchQueryChange(query, outSet)) {
        return true;
      }
    }
    return false;
  }

  public static boolean onSearchQuerySubmit(View childView, @NonNull String query) {
    if (childView instanceof HandlesSearchSuggestions) {
      if (((HandlesSearchSuggestions) childView).onSearchQuerySubmit(query)) {
        return true;
      }
    }
    return false;
  }

  private SearchSuggestionsSupport() {
  }
}
