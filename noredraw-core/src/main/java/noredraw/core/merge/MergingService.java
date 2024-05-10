package noredraw.core.merge;

import noredraw.core.model.Relic;

import java.util.List;

public interface MergingService {
    List<Relic> merge(List<Relic> resources);
}
