package pharmacie.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Migration automatique de la base de données au démarrage
 * Modifie la colonne imageURL pour supporter les images base64
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseMigration {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void migrateDatabase() {
        try {
            log.info("🔧 Migration: Modification de la colonne imageURL pour supporter les grandes images...");

            // Modifier la colonne imageURL de VARCHAR(500) vers TEXT
            jdbcTemplate.execute("ALTER TABLE medicament ALTER COLUMN imageurl TYPE TEXT");

            log.info("✅ Migration réussie: La colonne imageURL supporte maintenant les images illimitées");
        } catch (Exception e) {
            // Si la colonne est déjà TEXT, l'erreur sera ignorée
            log.warn("⚠️ Migration imageURL: {}", e.getMessage());
        }
    }
}
