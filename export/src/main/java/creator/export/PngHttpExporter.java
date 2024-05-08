package creator.export;

import creator.core.annotation.Named;
import creator.core.model.Relic;
import creator.core.model.graph.Graph;
import creator.export.model.BinaryDiagram;
import creator.export.model.Diagram;
import creator.export.structurizr.StructurizrExporters;
import creator.export.utils.Retryable;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.code.TranscoderSmart;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

// TODO: overcome url length limit
@Slf4j
@Named("PNG")
public class PngHttpExporter implements Exporter {
    public static final String BASE_URL = "https://www.planttext.com/api/plantuml/png/";
    private final Exporter plantUMLExporter = new StructurizrExporters.PlantUmlStructurizrExporter();
    private final TranscoderSmart transcoder = new TranscoderSmart();

    @Override
    public Diagram export(Graph<Relic> graph, String title) {
        Diagram plantUml = plantUMLExporter.export(graph, title);
        String encoded = encode(plantUml);
        byte[] png = Retryable.withRetries(() -> sendRequest(encoded), 5, 1000);
        return BinaryDiagram.builder().data(png).build();
    }

    @SneakyThrows
    private String encode(Diagram plantUml) {
        return transcoder.encode(new String(plantUml.getData(), StandardCharsets.UTF_8));
    }

    public byte[] sendRequest(String encodedDiagram) {
        try {
            URL url = new URL(BASE_URL + encodedDiagram);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);

            if (connection.getResponseCode() > 204) {
                throw new RuntimeException("Failed to fetch data: HTTP response code " + connection.getResponseCode());
            }

            return connection.getInputStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException("Failed to send HTTP request to " + BASE_URL, e);
        }
    }
}
