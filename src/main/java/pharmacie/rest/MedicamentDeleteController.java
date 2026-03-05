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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/medicaments")
@Slf4j
public class MedicamentDeleteController {

    private static final Logger log = LoggerFactory.getLogger(MedicamentDeleteController.class);

    private final MedicamentRepository medicamentRepository;

    public MedicamentDeleteController(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
    }

    /**
     * Supprime un médicament et ses lignes associées
     * Approche: charger le médic, vider les lignes, sauvegarder, puis supprimer
     */
    @Transactional
    @DeleteMapping("/{reference}")
    public ResponseEntity<String> supprimerMedicament(@PathVariable Integer reference) {
        try {
            log.info("Suppression du médicament {}", reference);

            // Étape 1: Charger le médicament avec ses lignes
            var medicament = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));

            log.info("Médicament trouvé. Voici ses lignes: {}", medicament.getLignes().size());

            // Étape 2: Vider la liste des lignes (déclenche orphanRemoval)
            medicament.getLignes().clear();

            // Étape 3: Sauvegarder le médicament SANS ses lignes
            medicamentRepository.save(medicament);
            medicamentRepository.flush();

            log.info("Lignes supprimées via orphanRemoval");

            // Étape 4: Supprimer le médicament
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
