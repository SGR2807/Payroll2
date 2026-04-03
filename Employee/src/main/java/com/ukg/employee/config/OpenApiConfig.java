package com.ukg.employee.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI employeeServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Service API")
                        .description("REST APIs for Employee Management")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Payroll Team")));
    }
}
