package noredraw.core.merge.strategy;

import noredraw.core.model.Relic;

import java.util.List;

public interface MergeStrategy {
    boolean mergeable(Relic left, Relic right);

    Relic merge(List<Relic> resources);
}
