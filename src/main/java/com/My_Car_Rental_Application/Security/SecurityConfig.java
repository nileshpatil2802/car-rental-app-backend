//
//package com.My_Car_Rental_Application.Security;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import java.util.List;  
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        
//        return http
//                .csrf(csrf -> csrf.disable())
//                .cors(Customizer.withDefaults()) 
//                // 1. Change session policy to allow sessions for logged-in users
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//                .authorizeHttpRequests(auth -> {
//                    
//                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
//
//                    auth.requestMatchers(
//                            "/",
//                            "/home/**",
//                            "/car-images/**",
//                            "/uploads/**",
//                            "/user/TripDetailsForHistory/**"
//                    ).permitAll();
//                    
//                    // 2. Keep your Admin security rule intact
////                    auth.requestMatchers("/user/**").hasAnyRole("USER", "ADMIN");
////                    auth.requestMatchers("/auth/admin/**").hasRole("ADMIN");
//                    auth.requestMatchers("/user/getUserByEmail/**").hasAuthority("USER");
//                    auth.requestMatchers("/user/**").hasAnyAuthority("USER", "ADMIN");
//                    auth.requestMatchers("/auth/admin/**").hasAuthority("ADMIN");
//
//                    auth.anyRequest().authenticated();
//                })
//                .httpBasic(httpBasic -> httpBasic.disable())
//               // .httpBasic(Customizer.withDefaults())
//                .build();
//    }
//    
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    AuthenticationManager authenticationManager(
//            AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        configuration.setAllowedOrigins(
//                List.of(
//                        "https://car-rental-app-frontend-1.vercel.app",
//                        "https://car-rental-app-frontend-1-38jskv6d1-nilesh-02b3.vercel.app",
//                        "https://*.vercel.app",
//                        "http://localhost:3000",
//                        "http://localhost:5173"
//                    ));
//
//        configuration.setAllowedMethods(
//                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//
//        configuration.setAllowedHeaders(
//                List.of("*"));
//
//        configuration.setAllowCredentials(true); // Crucial for sending cookies/sessions from React
//
//        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
//                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
//
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
//}
//package com.My_Car_Rental_Application.Security;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.CorsConfigurationSource;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Bean
//    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        return http
//                .csrf(csrf -> csrf.disable())
//                .cors(Customizer.withDefaults())
//
//                // Frontend sends credentials on every request (Basic header),
//                // so no server-side session is needed.
//                .sessionManagement(session ->
//                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//
//                .authorizeHttpRequests(auth -> {
//
//                    // CORS preflight
//                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
//
//                    // Public endpoints
//                    auth.requestMatchers(
//                            "/",
//                            "/home/**",
//                            "/car-images/**",
//                            "/uploads/**",
//                            "/user/TripDetailsForHistory/**"
//                    ).permitAll();
//
//                    // Admin endpoints
//                    auth.requestMatchers("/auth/admin/**")
//                            .hasAnyAuthority("ADMIN", "ROLE_ADMIN");
//
//                    // Logged-in user endpoints (includes /user/getUserByEmail/**)
//                    // Both "USER" and "ROLE_USER" styles are accepted.
//                    auth.requestMatchers("/user/**")
//                            .hasAnyAuthority("USER", "ADMIN", "ROLE_USER", "ROLE_ADMIN");
//
//                    auth.anyRequest().authenticated();
//                })
//
//                // FIX: Basic auth must be ENABLED, because the React app sends
//                // "Authorization: Basic ..." on every request.
//                // The custom entry point returns a plain 401 without the
//                // WWW-Authenticate header, so the browser never shows the
//                // native username/password popup.
//                .httpBasic(basic -> basic.authenticationEntryPoint(
//                        (request, response, authException) ->
//                                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized")))
//                .build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//
//    @Bean
//    CorsConfigurationSource corsConfigurationSource() {
//
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // FIX: wildcards only work with setAllowedOriginPatterns, not setAllowedOrigins
//        configuration.setAllowedOriginPatterns(List.of(
//                "https://car-rental-app-frontend-1.vercel.app",
//                "https://*.vercel.app",
//                "http://localhost:3000",
//                "http://localhost:5173"
//        ));
//
//        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//        configuration.setAllowedHeaders(List.of("*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return source;
//    }
//}

package com.My_Car_Rental_Application.Security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                // Explicitly attach the CORS configuration source
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> {

                    // 1. Allow all CORS preflight requests
                    auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();

                    // 2. CRITICAL: Allow /error so error responses include CORS headers
                    auth.requestMatchers("/error").permitAll();

                    // 3. Public endpoints
                    auth.requestMatchers(
                            "/",
                            "/home/**",
                            "/car-images/**",
                            "/uploads/**",
                            "/user/TripDetailsForHistory/**"
                    ).permitAll();

                    // 4. Admin endpoints
                    auth.requestMatchers("/auth/admin/**")
                            .hasAnyAuthority("ADMIN", "ROLE_ADMIN");

                    // 5. User endpoints
                    auth.requestMatchers("/user/**")
                            .hasAnyAuthority("USER", "ADMIN", "ROLE_USER", "ROLE_ADMIN");

                    auth.anyRequest().authenticated();
                })

                .httpBasic(basic -> basic.authenticationEntryPoint(
                        (request, response, authException) ->
                                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized")))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Allows ANY Vercel deployment URL, custom domains, and localhost
        configuration.setAllowedOriginPatterns(List.of("*"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
