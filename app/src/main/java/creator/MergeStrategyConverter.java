package creator;

import com.beust.jcommander.IStringConverter;
import creator.core.matching.merge.DoNotMergeStrategy;
import creator.core.matching.merge.MergeStrategy;
import creator.core.matching.merge.SameNameDifferentSourceFileMergeStrategy;

public class MergeStrategyConverter implements IStringConverter<MergeStrategy> {

    @Override
    public MergeStrategy convert(String value) {
        return switch (value) {
            case "SAME_NAME_DIFF_SOURCE" -> defaultStrategy();
            case "NONE" -> new DoNotMergeStrategy();
            default -> throw new IllegalStateException("Unexpected value for merge strategy: " + value);
        };
    }

    public static MergeStrategy defaultStrategy() {
        return new SameNameDifferentSourceFileMergeStrategy();
    }
}
