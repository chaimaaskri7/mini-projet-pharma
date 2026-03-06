package pharmacie.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.persistence.EntityManager;
import pharmacie.dao.MedicamentRepository;
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
     * Supprime un médicament et toutes ses lignes associées
     * Utilise SQL natif pour supprimer les lignes directement sans cascade JPA
     */
    @Transactional
    @DeleteMapping("/{reference}")
    public ResponseEntity<String> supprimerMedicament(@PathVariable Integer reference) {
        try {
            log.info("Suppression du médicament {}", reference);

            if (!medicamentRepository.existsById(reference)) {
                return ResponseEntity.notFound().build();
            }

            // Étape 1: Supprimer TOUS les enregistrements LIGNE qui référencent ce
            // médicament
            // Utilise SQL natif pour contourner complètement les cascades JPA
            int lignesDelete = entityManager
                    .createNativeQuery("DELETE FROM LIGNE WHERE MEDICAMENT_REFERENCE = :ref")
                    .setParameter("ref", reference)
                    .executeUpdate();

            log.info("Supprimé {} lignes avant suppression du médicament {}", lignesDelete, reference);

            // Force la synchronisation avec la base de données
            entityManager.flush();

            // Étape 2: Vider la cache JPA pour ce médicament
            var medicament = medicamentRepository.findById(reference);
            if (medicament.isPresent()) {
                entityManager.detach(medicament.get());
            }

            // Étape 3: Supprimer le médicament lui-même
            medicamentRepository.deleteById(reference);
            medicamentRepository.flush();

            log.info("Médicament {} supprimé avec succès", reference);
            return ResponseEntity.ok("Médicament supprimé avec succès");

        } catch (Exception e) {
            log.error("Erreur lors de la suppression du médicament {}: {}", reference, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
