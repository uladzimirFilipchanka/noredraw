package creator.export.structurizr;

import com.structurizr.Workspace;
import com.structurizr.export.DiagramExporter;
import com.structurizr.model.CustomElement;
import com.structurizr.model.Model;
import com.structurizr.model.Tags;
import com.structurizr.view.CustomView;
import com.structurizr.view.Styles;
import com.structurizr.view.ViewSet;
import creator.core.matching.model.Graph;
import creator.core.resource.*;
import creator.export.Diagram;
import creator.export.Exporter;
import creator.export.TextDiagram;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class StructurizrBasedExporter implements Exporter {
    private final DiagramExporter structurizrExporter;
    private final static boolean SHOW_SOURCES_AS_ELEMENTS = false;

    public StructurizrBasedExporter(StructurizrExporters structurizrExporter) {
        this.structurizrExporter = structurizrExporter.getExporter();
    }

    @Override
    public Diagram export(Graph<Relic> graph) {
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

    private Workspace convertGraph(Graph<Relic> graph) {
        // TODO: pass name of the diagram
        Workspace workspace = new Workspace(UUID.randomUUID().toString(), "Temp workspace");
        Model model = workspace.getModel();
        Map<Resource, CustomElement> resourceElementMap = new HashMap<>();
        for (Relic resource : graph.getVertices()) {
            // TODO: resource should have a name btw
            resourceElementMap.put(resource, createElement(resource, model));
        }
        for (Relic source : graph.getVertices()) {
            Map<Relic, String> destinations = graph.getNamedEdges(source);
            destinations.forEach((key, value) -> resourceElementMap.get(source).uses(resourceElementMap.get(key), value));
        }

        ViewSet views = workspace.getViews();
        CustomView contextView = views.createCustomView("viewKey", "viewTitle", "viewDescription");
        contextView.addAllCustomElements();

        Styles styles = views.getConfiguration().getStyles();
        styles.addElementStyle(Tags.ELEMENT).background("#1168bd").color("#ffffff");

        return workspace;
    }

    private CustomElement createElement(Relic resource, Model model) {
        // TODO: name should be unique!
        CustomElement customElement = model.addCustomElement(resource.getName() + "\n" + "[" + resource.getType() + "]", "", toDescription(resource));

        Source source = resource.getSource();
        if (SHOW_SOURCES_AS_ELEMENTS) {
            createAndRelateSource(customElement, source, model);
        }
        return customElement;
    }

    private void createAndRelateSource(CustomElement parent, Source source, Model model) {
        if (source instanceof SimpleSource simpleSource) {
            CustomElement sourceElement = model.getCustomElementWithName(simpleSource.getName());
            if (sourceElement == null) {
                sourceElement = model.addCustomElement(simpleSource.getName());
            }
            sourceElement.uses(parent, source.describe());
        } else if (source instanceof CompositeSource compositeSource) {
            compositeSource.getSources().forEach(single -> createAndRelateSource(parent, single, model));
        }
        log.warn("Could not determine source type {}", source.getClass());
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
