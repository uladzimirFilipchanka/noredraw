package creator.core.merge;

import creator.core.model.Relic;

import java.util.List;

public interface MergingService {
    List<Relic> merge(List<Relic> resources);
}
