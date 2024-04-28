package creator.core.matching.merge;

import creator.core.resource.Relic;

import java.util.List;

public interface MergingService {
    List<Relic> merge(List<Relic> resources);
}
