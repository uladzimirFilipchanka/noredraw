package creator.args;

import com.beust.jcommander.IStringConverter;
import creator.core.link.strategy.LinkStrategy;
import creator.core.link.strategy.MatchingDefinitionLinkStrategy;
import creator.utils.NamedClassUtils;

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
