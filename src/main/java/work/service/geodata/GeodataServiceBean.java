package work.service.geodata;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .bodyToMono(String.class);
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

}
