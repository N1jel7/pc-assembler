package by.bsu.n1jel.pc.assembler.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "N1jel",
                        url = "https://github.com/n1jel7"
                ),
                description = "OpenApi documentation for PC Assembler",
                title = "OpenApi specification - PC Assembler",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Local environment",
                        url = "http://localhost:8080"
                )
        }
)

public class OpenApiConfig {
}
