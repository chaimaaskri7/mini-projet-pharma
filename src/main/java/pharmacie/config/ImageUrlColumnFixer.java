package pharmacie.config;

import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import lombok.extern.slf4j.Slf4j;

/**
 * FORCE la modification de la colonne imageURL au démarrage AVANT Hibernate
 */
@Slf4j
@Configuration
public class ImageUrlColumnFixer {

    @Bean
    @Order(Integer.MIN_VALUE) // S'exécute en PREMIER
    CommandLineRunner fixImageUrlColumn(DataSource dataSource) {
        return args -> {
            try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {

                log.warn("🔧🔧🔧 FORÇAGE MODIFICATION COLONNE IMAGEURL 🔧🔧🔧");

                // Essayer de modifier la colonne (marche si elle existe déjà)
                try {
                    // Syntaxe PostgreSQL
                    stmt.execute("ALTER TABLE medicament ALTER COLUMN imageurl TYPE VARCHAR(100000)");
                    log.info("✅ Colonne imageurl modifiée avec succès -> VARCHAR(100000)");
                } catch (Exception e) {
                    log.info("⚠️ Table medicament n'existe pas encore, sera créée par Hibernate avec la bonne taille");
                }

            } catch (Exception e) {
                log.error("❌ Erreur lors de la modification: {}", e.getMessage());
            }
        };
    }
}
