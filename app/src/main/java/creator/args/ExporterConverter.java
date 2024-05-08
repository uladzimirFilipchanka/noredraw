package creator.args;

import com.beust.jcommander.IStringConverter;
import creator.export.Exporter;
import creator.export.structurizr.StructurizrBasedExporter;
import creator.export.structurizr.StructurizrExporters;
import creator.utils.NamedClassUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;

@Slf4j
public class ExporterConverter implements IStringConverter<Exporter> {
    @Override
    public Exporter convert(String value) {
        Map<String, Exporter> exporterMap = NamedClassUtils.findNamedClassesOf(Exporter.class);

        return Optional.ofNullable(exporterMap.get(value.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("Type of exporter not found: " + value));
    }

    public static StructurizrBasedExporter defaultExporter() {
        return new StructurizrExporters.PlantUmlStructurizrExporter();
    }
}
