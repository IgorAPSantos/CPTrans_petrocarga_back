package com.cptrans.petrocarga.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;


@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI CustomOpenAPI() {
		final String securitySchemeName = "bearerAuth";

		Contact contact = new Contact();
		contact.setEmail("email@email.com");
		contact.setName("Serratec Developers");
		contact.setUrl("https://github.com/RTIC-STEM/2025_2_CPTRANS_Projeto_Nome");

		License license = new License();
		license.setName("Apache License 2.0");
		license.setUrl("https://www.apache.org/licenses/LICENSE-2.0.html");

		Info info = new Info();
		info.setTitle("PetroCarga API");
		info.setVersion("1.0.0");
		info.setDescription("API para gerenciamento e reserva de vagas para estacionamento de veículos em locais de carga e descarga na cidade de Petrópolis.");
		info.setContact(contact);
		info.setLicense(license);
		info.setTermsOfService("https://swagger.io/terms/");
		return new OpenAPI().info(info) .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .in(SecurityScheme.In.HEADER)
                                        .description("Insira o token JWT no formato: Bearer <token>")));
    }
}
