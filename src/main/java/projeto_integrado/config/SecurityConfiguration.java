package projeto_integrado.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import projeto_integrado.Infra.SecurityFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .userDetailsService(authorizationService)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                        .requestMatchers("/", "/Coinvert").permitAll()
                        .requestMatchers("/simulacao").permitAll()
                        .requestMatchers(HttpMethod.GET, "/Coinvert/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/logado/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/login", "/cadastro").permitAll()
                        .requestMatchers(HttpMethod.GET, "/cambio/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/Perfil").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login2").permitAll()
                        .requestMatchers(HttpMethod.POST, "/cadastro").permitAll()
                        .requestMatchers(HttpMethod.POST, "/logado/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/Coinvert/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/cambio/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/cadastro/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/logout").permitAll()


                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
