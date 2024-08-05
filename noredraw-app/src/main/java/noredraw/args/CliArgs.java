package noredraw.args;

import com.beust.jcommander.Parameter;
import noredraw.core.link.strategy.LinkStrategy;
import noredraw.core.merge.strategy.MergeStrategy;
import noredraw.export.Exporter;
import lombok.Getter;

@Getter
public class CliArgs {
    @Parameter(names = {"-s", "--source"}, description = "Path to the source code", required = true)
    private String source = "/source";

    @Parameter(names = {"-m", "--merge"}, converter = MergeStrategyConverter.class, description = "Merge strategy to use")
    private MergeStrategy mergeStrategy = MergeStrategyConverter.defaultStrategy();

    @Parameter(names = {"-l", "--link"}, converter = LinkStrategyConverter.class, description = "Link strategy to use")
    private LinkStrategy linkStrategy = LinkStrategyConverter.defaultStrategy();

    @Parameter(names = {"-e", "--export"}, converter = ExporterConverter.class, description = "Type of the diagram you want to get")
    private Exporter exporter = ExporterConverter.defaultExporter();

    @Parameter(names = {"-o", "--output"}, description = "Path and name for the target diagram")
    private String output = "diagram.out";

    @Parameter(names = {"-t", "--title"}, description = "Name of the diagram itself. Could be used as a title for the diagram")
    private String title;

}
