package creator;

import com.beust.jcommander.IStringConverter;
import creator.export.Exporter;
import creator.export.structurizr.StructurizrBasedExporter;
import creator.export.structurizr.StructurizrExporters;

public class ExporterConverter implements IStringConverter<Exporter> {
    @Override
    public Exporter convert(String value) {
        switch (value) {
            case "PLANT_UML":
                return defaultExporter();
            default:
                throw new IllegalArgumentException("Type of exporter not found: " + value);
        }
    }

    public static StructurizrBasedExporter defaultExporter() {
        return new StructurizrBasedExporter(StructurizrExporters.PLANT_UML);
    }
}
