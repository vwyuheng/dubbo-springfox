package io.hulk.dubbo.springfox.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;


@Configuration
public class OpenAPIConfig {

	  @Bean
	  public OpenAPI openAPI() {
	      return new OpenAPI()
	              .info(new Info().title("Pet Shop API")
	              .description("API Pet Shop")
	              .version("v0.0.1")
	              .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
	              .externalDocs(new ExternalDocumentation()
	              .description("API para gerenciamento de pet shop")
	              .url(""));
	  }

}
