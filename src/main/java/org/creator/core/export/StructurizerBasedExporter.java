package org.creator.core.export;

import com.structurizr.Workspace;
import com.structurizr.export.DiagramExporter;
import com.structurizr.model.CustomElement;
import com.structurizr.model.Model;
import com.structurizr.model.Tags;
import com.structurizr.view.CustomView;
import com.structurizr.view.Styles;
import com.structurizr.view.ViewSet;
import org.creator.core.matching.model.Graph;
import org.creator.core.resource.Resource;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class StructurizerBasedExporter implements Exporter {
    private final DiagramExporter structurizrExporter;

    public StructurizerBasedExporter(DiagramExporter structurizrExporter) {
        this.structurizrExporter = structurizrExporter;
    }

    @Override
    public Diagram export(Graph graph) {
        Workspace workspace = convertGraph(graph);
        Collection<com.structurizr.export.Diagram> diagram = structurizrExporter.export(workspace);
        return diagram.stream()
                .findFirst()
                .map(com.structurizr.export.Diagram::getDefinition)
                .map(this::covertFromStructurizerDiagram)
                .orElseThrow(() -> new RuntimeException("No structurizr diagram generated"));
    }

    private Diagram covertFromStructurizerDiagram(String definition) {
        return TextDiagram.builder().definition(definition).build();
    }

    private Workspace convertGraph(Graph graph) {
        // TODO: pass name of the diagram
        Workspace workspace = new Workspace(UUID.randomUUID().toString(), "Temp workspace");
        Model model = workspace.getModel();
        Map<Resource, CustomElement> resourceElementMap = new HashMap<>();
        for (Resource resource : graph.getVertices()) {
            // TODO: resource should have a name btw
            resourceElementMap.put(resource, createElement(resource, model));
        }
        for (Resource source : graph.getVertices()) {
            List<Resource> destinations = graph.getAdjacentVertices(source);
            destinations.forEach(destination ->
                    resourceElementMap.get(source).uses(resourceElementMap.get(destination), "uses")
            );
        }

        ViewSet views = workspace.getViews();
        CustomView contextView = views.createCustomView("viewKey", "viewTitle", "viewDescription");
        contextView.addAllCustomElements();

        Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle(Tags.ELEMENT).background("#1168bd").color("#ffffff");

        return workspace;
    }

    private static CustomElement createElement(Resource resource, Model model) {
        return model.addCustomElement( resource.getName() + "\n" + "[" + resource.getClass().getSimpleName() + "]", "", toDescription(resource));
    }

    private static String toDescription(Resource resource) {
        Map<String, String> definitions = resource.getDefinitions();
        if (definitions == null) {
            return "";
        }

        return definitions.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .sorted()
                .collect(Collectors.joining("\n"));
    }
}
