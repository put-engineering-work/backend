package work.user.web.user;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;


public interface UserController {

    @Operation(summary = "Add a new Automobile", description = "Creates a new automobile entity", tags = {"Automobile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Automobile created"),
            @ApiResponse(responseCode = "400", description = "Unauthorized")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping
//    @PreAuthorize("hasAnyRole('ADMIN', 'PERSON')")
    String getUserDetails(HttpServletRequest request);

    @Operation(summary = "Add a new Automobile", description = "Creates a new automobile entity", tags = {"Automobile"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Automobile created"),
            @ApiResponse(responseCode = "400", description = "Unauthorized")})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/DWASDWA")
//    @PreAuthorize("hasAnyRole('ADMIN', 'PERSON')")
    Object newdrwadsad();

}
