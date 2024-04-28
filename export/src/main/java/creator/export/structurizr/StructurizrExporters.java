package creator.export.structurizr;

import com.structurizr.export.DiagramExporter;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;

public enum StructurizrExporters {
    PLANT_UML(new StructurizrPlantUMLExporter());

    private final DiagramExporter exporter;

    StructurizrExporters(DiagramExporter exporter) {
        this.exporter = exporter;
    }

    public DiagramExporter getExporter() {
        return exporter;
    }
}
