package work.service.geodata;

import reactor.core.publisher.Mono;

public interface GeodataService {
    Mono<String> getAddressAutocomplete(String input);

    Mono<String> getAddressFromCoordinates(double latitude, double longitude);
}
