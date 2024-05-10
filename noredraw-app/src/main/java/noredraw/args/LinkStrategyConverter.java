package noredraw.args;

import com.beust.jcommander.IStringConverter;
import noredraw.core.link.strategy.LinkStrategy;
import noredraw.core.link.strategy.MatchingDefinitionLinkStrategy;
import noredraw.utils.NamedClassUtils;

import java.util.Map;
import java.util.Optional;

public class LinkStrategyConverter implements IStringConverter<LinkStrategy> {

    @Override
    public LinkStrategy convert(String value) {
        Map<String, LinkStrategy> strategyMap = NamedClassUtils.findNamedClassesOf(LinkStrategy.class);

        return Optional.ofNullable(strategyMap.get(value.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("Type of strategy not found: " + value));
    }

    public static LinkStrategy defaultStrategy() {
        return new MatchingDefinitionLinkStrategy();
    }
}
