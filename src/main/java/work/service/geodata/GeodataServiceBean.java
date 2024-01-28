package work.service.geodata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import work.util.exception.CustomException;

@Service
public class GeodataServiceBean implements GeodataService {

    private final WebClient webClient;

    @Value("${google.api.key}")
    private String googleApiKey;

    public GeodataServiceBean(WebClient.Builder webClient, @Value("${google.api.key}") String googleApiKey) {
        this.webClient = webClient.baseUrl("https://maps.googleapis.com").build();
        this.googleApiKey = googleApiKey;
    }

    @Override
    public Mono<String> getAddressAutocomplete(String input) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/api/place/autocomplete/json")
                        .queryParam("input", input)
                        .queryParam("types", "(cities)")
                        .queryParam("key", googleApiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(this::transformResponse);
    }

    private Mono<String> transformResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode root = objectMapper.readTree(response);
            ArrayNode predictions = (ArrayNode) root.path("predictions");

            return Flux.fromIterable(predictions)
                    .flatMap(prediction -> {
                        String placeId = prediction.path("place_id").asText();
                        String description = prediction.path("description").asText();

                        return getCoordinatesFromPlaceId(placeId)
                                .map(coordinatesNode -> {
                                    double longitude = coordinatesNode.path("lng").asDouble();
                                    double latitude = coordinatesNode.path("lat").asDouble();

                                    ObjectNode newPrediction = objectMapper.createObjectNode();
                                    newPrediction.put("description", description);
                                    newPrediction.put("place_id", placeId);
                                    newPrediction.put("latitude", latitude);
                                    newPrediction.put("longitude", longitude);

                                    return newPrediction;
                                });
                    })
                    .collectList()
                    .map(newPredictions -> {
                        ObjectNode newNode = objectMapper.createObjectNode();
                        newNode.putArray("predictions").addAll(newPredictions);
                        return newNode.toString();
                    });

        } catch (Exception e) {
        }
        return Mono.just("{}");
    }

    public Mono<String> getAddressFromCoordinates(double latitude, double longitude) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/api/geocode/json")
                        .queryParam("latlng", latitude + "," + longitude)
                        .queryParam("key", googleApiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class);
    }


    private Mono<JsonNode> getCoordinatesFromPlaceId(String placeId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maps/api/place/details/json")
                        .queryParam("place_id", placeId)
                        .queryParam("fields", "geometry/location")
                        .queryParam("key", googleApiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(jsonNode -> jsonNode.path("result").path("geometry").path("location"));
    }

}
