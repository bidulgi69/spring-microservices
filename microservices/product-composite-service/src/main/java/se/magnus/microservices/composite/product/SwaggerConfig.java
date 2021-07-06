package se.magnus.microservices.composite.product;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static java.util.Collections.emptyList;
import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;
import static springfox.documentation.spi.DocumentationType.SWAGGER_2;

@Configuration
@EnableSwagger2
@EnableWebFlux
public class SwaggerConfig implements WebFluxConfigurer {
    @Value("${api.common.version}") String apiVersion;
    @Value("${api.common.title}") String apiTitle;
    @Value("${api.common.description}") String apiDescription;
    @Value("${api.common.termsOfServiceUrl}") String apiTermsOfServiceUrl;
    @Value("${api.common.license}") String apiLicense;
    @Value("${api.common.licenseUrl}") String apiLicenseUrl;
    @Value("${api.common.contact.name}") String apiContactName;
    @Value("${api.common.contact.url}") String apiContactUrl;
    @Value("${api.common.contact.email}") String apiContactEmail;

    @Bean
    public Docket apiDocumentation() {
        return new Docket(SWAGGER_2)
                .select()
                .apis(basePackage("se.magnus.microservices.composite.product"))
                .paths(PathSelectors.any())
                .build()
                .globalResponses(HttpMethod.GET, emptyList())
                .apiInfo(new ApiInfo(
                        apiTitle,
                        apiDescription,
                        apiVersion,
                        apiTermsOfServiceUrl,
                        new Contact(apiContactName, apiContactUrl, apiContactEmail),
                        apiLicense,
                        apiLicenseUrl,
                        emptyList()
                ));
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
