package noredraw.core.link.strategy;

import noredraw.core.model.Relic;

public interface LinkStrategy {
    boolean linkable(Relic left, Relic right);
}
