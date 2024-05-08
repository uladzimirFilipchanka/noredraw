package creator.core.model;

import java.util.Map;


public interface Resource extends Matchable {
    String getName();

    String getType();

    Map<String, String> getDefinitions();
}
