package com.elasticsearch.poc.swagger;

import static com.elasticsearch.poc.constants.ServiceConstants.API_PREFIX;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.config.SwaggerConfigLocator;
import io.swagger.jaxrs.config.SwaggerContextService;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.swagger.web.InMemorySwaggerResourcesProvider;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Springfox Swagger Configuration
 * @author narasimhulu.chakali
 *
 */

@EnableAutoConfiguration
@Configuration
@ComponentScan(basePackageClasses = {LibraryServiceSwaggerApiListing.class})
@EnableSwagger2

public class LibraryServiceSwaggerConfiguration {
	
	@Bean
	public BeanConfig beanConfig() {
		BeanConfig beanConfig = new BeanConfig();
		beanConfig.setSchemes(new String[] {"https","http"});
		beanConfig.setVersion("0.0.1-SNAPSHOT");
		beanConfig.setResourcePackage("com.elasticsearch.poc.controller");
		beanConfig.setTitle("Library Service API");
		beanConfig.setScan();
		SwaggerConfigLocator.getInstance().putConfig(SwaggerContextService.CONFIG_ID_DEFAULT, beanConfig);
		return beanConfig;
	}

	/**
	 * 
	 * Swagger Resource Provider for redirecting to Swagger API
	 * 
	 * @param inMemorySwaggerResourcesProvider
	 * @return
	 */
	@Bean
	@Primary
	public SwaggerResourcesProvider resourceProvider(
			final InMemorySwaggerResourcesProvider inMemorySwaggerResourcesProvider) {
		return new SwaggerResourcesProvider() {

			@Override
			public List<SwaggerResource> get() {
				SwaggerResource swaggerResource = new SwaggerResource();
				swaggerResource.setLocation(API_PREFIX + "/swagger.json");
				swaggerResource.setSwaggerVersion(DocumentationType.SWAGGER_2.getVersion());
				swaggerResource.setName("Swagger");
				return Stream.concat(Stream.of(swaggerResource), inMemorySwaggerResourcesProvider.get().stream()).collect(
						Collectors.toList());
			}
		};
	}

	@Bean
	public SwaggerSerializers apiDeclarationProvider() {
		return new SwaggerSerializers();
	}

}
