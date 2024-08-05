package noredraw.export.structurizr;

import com.structurizr.export.dot.DOTExporter;
import com.structurizr.export.mermaid.MermaidDiagramExporter;
import noredraw.core.annotation.Named;

public class StructurizrExporters {

    @Named("PLANT_UML")
    public static class PlantUmlStructurizrExporter extends StructurizrBasedExporter {
        public PlantUmlStructurizrExporter() {
            super(new CustomStructurizrPlantUMLExporter());
        }
    }

    @Named("MERMAID")
    public static class MermaidStructurizrExporter extends StructurizrBasedExporter {
        public MermaidStructurizrExporter() {
            super(new MermaidDiagramExporter());
        }
    }

    @Named("DOT")
    public static class DotStructurizrExporter extends StructurizrBasedExporter {
        public DotStructurizrExporter() {
            super(new DOTExporter());
        }
    }
}
