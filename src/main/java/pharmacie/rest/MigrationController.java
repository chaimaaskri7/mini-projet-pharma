package pharmacie.rest;

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

    @PostMapping("/migrate-imageurl")
    public ResponseEntity<String> migrateImageUrl() {
        try {
            log.info("🔧 Début migration colonne imageURL...");

            // Modifier la colonne imageURL vers TEXT
            jdbcTemplate.execute("ALTER TABLE medicament ALTER COLUMN imageurl TYPE TEXT");

            log.info("✅ Migration réussie");
            return ResponseEntity.ok("Migration réussie: imageURL est maintenant TEXT");
        } catch (Exception e) {
            log.error("❌ Erreur migration: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body("Erreur migration: " + e.getMessage());
        }
    }
}
