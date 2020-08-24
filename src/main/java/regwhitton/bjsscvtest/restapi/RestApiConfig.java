package regwhitton.bjsscvtest.restapi;

import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
@EnableOpenApi
public class RestApiConfig {

    /**
     * Sets up the publishing of the REST API on http://localhost:8080/swagger-ui/
     */
    @Bean
    public Docket swaggerDocket() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .select()
                .apis(basePackage(thisPackage()))
                .paths(any())
                .build();
    }

    private String thisPackage() {
        return this.getClass().getPackage().getName();
    }
}