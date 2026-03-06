package pharmacie.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("deploy")
public class DatabaseConfig {

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        // Récupère DATABASE_URL de Render (format: postgres://user:pass@host:port/db)
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null) {
            throw new RuntimeException("DATABASE_URL n'est pas définie");
        }

        // Parse l'URL
        URI dbUri = new URI(databaseUrl);

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        // Configure le DataSource
        return DataSourceBuilder.create()
                .url(dbUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}
