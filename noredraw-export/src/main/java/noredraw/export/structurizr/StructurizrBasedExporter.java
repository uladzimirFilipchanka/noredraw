package noredraw.export.structurizr;

import com.structurizr.Workspace;
import com.structurizr.export.DiagramExporter;
import com.structurizr.model.CustomElement;
import com.structurizr.model.Model;
import com.structurizr.model.Tags;
import com.structurizr.view.*;
import lombok.extern.slf4j.Slf4j;
import noredraw.core.model.Relic;
import noredraw.core.model.graph.Graph;
import noredraw.core.model.source.CompositeSource;
import noredraw.core.model.source.SimpleSource;
import noredraw.core.model.source.Source;
import noredraw.export.Exporter;
import noredraw.export.model.Diagram;
import noredraw.export.model.TextDiagram;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class StructurizrBasedExporter implements Exporter {
    public static final String SOURCE_TAG = "SOURCE";
    private final DiagramExporter structurizrExporter;
    private final static boolean SHOW_SOURCES_AS_ELEMENTS = true;

    public StructurizrBasedExporter(DiagramExporter structurizrExporter) {
        this.structurizrExporter = structurizrExporter;
    }

    @Override
    public Diagram export(Graph<Relic> graph, String title) {
        Workspace workspace = convertGraph(graph, title);
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

    private Workspace convertGraph(Graph<Relic> graph, String title) {
        // TODO: pass name of the diagram
        Workspace workspace = new Workspace(UUID.randomUUID().toString(), "Temp workspace");
        Model model = workspace.getModel();
        Map<Relic, CustomElement> resourceElementMap = new HashMap<>();
        for (Relic resource : graph.getVertices()) {
            // TODO: resource should have a name btw
            resourceElementMap.put(resource, createElement(resource, model));
        }
        for (Relic source : graph.getVertices()) {
            Map<Relic, String> destinations = graph.getNamedEdges(source);
            destinations.forEach((key, value) -> resourceElementMap.get(source).uses(resourceElementMap.get(key), value));
        }

        ViewSet views = workspace.getViews();
        CustomView contextView = views.createCustomView(title, title, title);
        contextView.addAllCustomElements();
        contextView.addDefaultElements();

        Configuration configuration = views.getConfiguration();
        Styles styles = configuration.getStyles();
        styles.addElementStyle(Tags.ELEMENT)
                .background("#a5d8ff")
                .stroke("#000000")
                .color("#000000")
                .shape(Shape.RoundedBox);
        styles.addElementStyle(SOURCE_TAG)
                .background("#ffc9c9")
                .stroke("#000000")
                .color("#000000")
                .shape(Shape.RoundedBox);
        styles.addRelationshipStyle(Tags.RELATIONSHIP)
                .color("#000000");

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
        if (source == null) {
            return;
        }
        if (source instanceof SimpleSource simpleSource) {
            CustomElement sourceElement = model.getCustomElementWithName(simpleSource.getName());
            if (sourceElement == null) {
                sourceElement = model.addCustomElement(simpleSource.getName());
                sourceElement.addTags(SOURCE_TAG);
            }
            sourceElement.uses(parent, source.describe());
        } else if (source instanceof CompositeSource compositeSource) {
            compositeSource.getSources().forEach(single -> createAndRelateSource(parent, single, model));
        } else {
            log.warn("Could not determine source type {}", source.getClass());
        }
    }

    private static String toDescription(Relic resource) {
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
