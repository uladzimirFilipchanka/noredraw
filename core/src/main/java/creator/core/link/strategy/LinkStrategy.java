package creator.core.link.strategy;

import creator.core.model.Relic;

public interface LinkStrategy {
    boolean linkable(Relic left, Relic right);
}
