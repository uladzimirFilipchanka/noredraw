package creator.core.matching.merge;

import creator.core.resource.Relic;

import java.util.List;

public class DoNotMergeStrategy implements MergeStrategy {
    @Override
    public boolean mergeable(Relic left, Relic right) {
        return false;
    }

    @Override
    public Relic merge(List<Relic> resources) {
        return null;
    }
}
