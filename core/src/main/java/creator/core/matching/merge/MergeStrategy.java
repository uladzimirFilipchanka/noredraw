package creator.core.matching.merge;

import creator.core.resource.Relic;

import java.util.List;

public interface MergeStrategy {
    boolean mergeable(Relic left, Relic right);

    Relic merge(List<Relic> resources);
}
