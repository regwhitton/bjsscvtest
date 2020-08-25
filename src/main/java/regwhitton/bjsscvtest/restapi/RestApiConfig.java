package regwhitton.bjsscvtest.restapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestApiConfig {

    /**
     * Sets up the publishing of the REST API on http://localhost:8080/swagger-ui/
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("BJSS CV Test API"));
    }
}