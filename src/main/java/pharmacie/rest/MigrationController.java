package pharmacie.rest;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Endpoint pour migrer manuellement la colonne imageURL
 */
@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class MigrationController {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;

    @PostMapping("/migrate-imageurl")
    public ResponseEntity<String> migrateImageUrl() {
        try {
            log.info("🔧 Début migration colonne imageURL...");

            // Détection du type de base de données
            String dbUrl = dataSource.getConnection().getMetaData().getURL();
            String sql;

            if (dbUrl.contains("h2")) {
                // H2 Database
                sql = "ALTER TABLE medicament ALTER COLUMN imageurl VARCHAR(50000)";
                log.info("Détecté: H2 Database");
            } else if (dbUrl.contains("postgresql")) {
                // PostgreSQL
                sql = "ALTER TABLE medicament ALTER COLUMN imageurl TYPE TEXT";
                log.info("Détecté: PostgreSQL");
            } else {
                return ResponseEntity.status(500)
                        .body("Base de données non supportée: " + dbUrl);
            }

            jdbcTemplate.execute(sql);

            log.info("✅ Migration réussie");
            return ResponseEntity.ok("Migration réussie: imageURL supporte maintenant les grandes images base64");
        } catch (Exception e) {
            log.error("❌ Erreur migration: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body("Erreur migration: " + e.getMessage());
        }
    }
}
