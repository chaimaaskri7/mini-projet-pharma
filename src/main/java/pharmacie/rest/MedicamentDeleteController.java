package pharmacie.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.dao.MedicamentRepository;
import pharmacie.dao.LigneRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/medicaments")
@Slf4j
public class MedicamentDeleteController {

    private static final Logger log = LoggerFactory.getLogger(MedicamentDeleteController.class);

    private final MedicamentRepository medicamentRepository;
    private final EntityManager entityManager;

    public MedicamentDeleteController(MedicamentRepository medicamentRepository, EntityManager entityManager) {
        this.medicamentRepository = medicamentRepository;
        this.entityManager = entityManager;
    }

    /**
     * Supprime un médicament et ses lignes associées
     * Utilise SQL native pour éviter les cascades bidirectionnelles
     */
    @Transactional
    @DeleteMapping("/{reference}")
    public ResponseEntity<String> supprimerMedicament(@PathVariable Integer reference) {
        try {
            log.info("Suppression du médicament {}", reference);

            if (!medicamentRepository.existsById(reference)) {
                return ResponseEntity.notFound().build();
            }

            // Étape 1: Supprimer les lignes via SQL native
            // Cela évite les cascades bidirectionnelles problématiques
            int lignesSupprimees = entityManager.createNativeQuery(
                    "DELETE FROM LIGNE WHERE MEDICAMENT_REFERENCE = ?1")
                    .setParameter(1, reference)
                    .executeUpdate();

            log.info("Supprimé {} lignes pour le médicament {}", lignesSupprimees, reference);

            // Force l'exécution de la requête DELETE avant de continuer
            entityManager.flush();

            // Étape 2: Supprimer le médicament via SQL native
            int medicamentsSupprimees = entityManager.createNativeQuery(
                    "DELETE FROM MEDICAMENT WHERE REFERENCE = ?1")
                    .setParameter(1, reference)
                    .executeUpdate();

            if (medicamentsSupprimees == 0) {
                return ResponseEntity.notFound().build();
            }

            log.info("Médicament {} supprimé avec succès", reference);
            return ResponseEntity.ok("Médicament supprimé avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de la suppression du médicament {}: {}", reference, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
