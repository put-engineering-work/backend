package work.util.secutity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    //    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        return httpSecurity.cors(Customizer.withDefaults())
//                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/home/**"))
//                .authorizeHttpRequests(httpRequests ->
//                        httpRequests.requestMatchers("swagger-ui/*", "/v3/**").
//                        permitAll().
//                                anyRequest().authenticated())
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .oauth2ResourceServer(oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()))
//                .build();
//    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                //TODO: security without @PreAuthorize
                /* .authorizeHttpRequests(registry -> registry
                        .requestMatchers(HttpMethod.GET, "/api/**").hasRole("USER")
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("PERSON")
                        .anyRequest().authenticated()
                )*/
                .oauth2ResourceServer(oauth2Configurer -> oauth2Configurer
                        .jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(jwt -> {
                                    Map<String, Collection<String>> realmAccess = jwt.getClaim("realm_access");
                                    Collection<String> roles = realmAccess.get("roles");

                                    var grantedAuthorities = roles.stream()
                                            .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                            .collect(Collectors.toList());

                                    return new JwtAuthenticationToken(jwt, grantedAuthorities);
                                })));
        return httpSecurity.build();
    }
}

