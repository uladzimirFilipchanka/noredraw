package creator.core.merge.strategy;

import creator.core.annotation.Named;
import creator.core.model.Relic;

import java.util.List;

@Named("NONE")
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
