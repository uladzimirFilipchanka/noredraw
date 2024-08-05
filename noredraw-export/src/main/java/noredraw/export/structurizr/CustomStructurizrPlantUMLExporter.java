package noredraw.export.structurizr;

import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;

public class CustomStructurizrPlantUMLExporter extends StructurizrPlantUMLExporter {
    public CustomStructurizrPlantUMLExporter() {
        addSkinParam("arrowFontSize", "16");
        addSkinParam("rectangleBorderThickness", "2");
    }
}
