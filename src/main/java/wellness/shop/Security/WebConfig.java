package wellness.shop.Security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;

import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig {

//    @Bean
//    public WebMvcConfigurer corsConfigurer() {
//        return new WebMvcConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                        .allowedOrigins("http://127.0.0.1:5501","http://127.0.0.1:5500", "http://localhost:3000") // Specify frontend origins
//                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                        .allowedHeaders("*")
//                        .allowCredentials(true); // Allow credentials (cookies, Authorization headers)
//            }
//        };
//    }



//
//    @Bean
//    public CorsFilter corsFilter() {
//        CorsConfiguration configuration = new CorsConfiguration();
//
//        // Allow specific origins (frontend URLs)
//        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
//
//        // Allow specific HTTP methods
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
//
//        // Allow headers needed for the request
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//
//        // Allow credentials (for cookies or authorization headers)
//        configuration.setAllowCredentials(true);
//
//        // Expose specific headers
//        configuration.setExposedHeaders(Arrays.asList("Authorization"));
//
//        // Apply the configuration to all endpoints
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//
//        return new CorsFilter(source);
//    }
}
