package creator.core.merge.strategy;

import creator.core.model.Relic;

import java.util.List;

public interface MergeStrategy {
    boolean mergeable(Relic left, Relic right);

    Relic merge(List<Relic> resources);
}
