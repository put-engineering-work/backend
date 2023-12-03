package work.web.geodata;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

@Api(value = "Geodata", tags = "Geodata")
@Tag(name = "Geodata", description = "Geodata API")
public interface GeodataController {
    @GetMapping("/autocomplete/{input}")
    Mono<String> getAddressAutocomplete(@PathVariable("input") String input);
}
