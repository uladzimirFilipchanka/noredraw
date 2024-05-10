package noredraw.args;

import com.beust.jcommander.IStringConverter;
import noredraw.core.merge.strategy.MergeStrategy;
import noredraw.core.merge.strategy.SameNameDifferentSourceFileMergeStrategy;
import noredraw.utils.NamedClassUtils;

import java.util.Map;
import java.util.Optional;

public class MergeStrategyConverter implements IStringConverter<MergeStrategy> {

    @Override
    public MergeStrategy convert(String value) {
        Map<String, MergeStrategy> exporterMap = NamedClassUtils.findNamedClassesOf(MergeStrategy.class);

        return Optional.ofNullable(exporterMap.get(value.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("Type of strategy not found: " + value));
    }

    public static MergeStrategy defaultStrategy() {
        return new SameNameDifferentSourceFileMergeStrategy();
    }
}
