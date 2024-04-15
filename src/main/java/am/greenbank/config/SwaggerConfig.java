package am.greenbank.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;


@OpenAPIDefinition(
    info = @Info(
        title = "GreenBank API",
        description = "GreenBank API description"
    ),
    servers = {
        @Server(
            description = "Prod ENV",
            url = "https://api-greenbank.coderepublic.am"
        ),
        @Server(
            description = "Local ENV",
            url = "/"
        )
    }
)
@SecurityScheme(
    name = "bearerAuth",
    description = "JWT Auth description",
    scheme = "bearer",
    type = SecuritySchemeType.HTTP,
    bearerFormat = "JWT",
    in = SecuritySchemeIn.HEADER
)
public class SwaggerConfig {

}

