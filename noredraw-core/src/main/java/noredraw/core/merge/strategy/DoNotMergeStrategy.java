package noredraw.core.merge.strategy;

import noredraw.core.annotation.Named;
import noredraw.core.model.Relic;

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
