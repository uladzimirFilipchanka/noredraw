package creator.core.matching.match;

import creator.core.matching.model.Graph;
import creator.core.resource.Relic;

import java.util.List;

public interface MatchingService {
    Graph<Relic> match(List<Relic> resources);
}
