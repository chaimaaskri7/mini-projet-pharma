package pharmacie.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Medicament;
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
     * Supprime un médicament et ses lignes associées (via cascade)
     */
    @DeleteMapping("/{reference}")
    @Transactional
    public ResponseEntity<String> supprimerMedicament(@PathVariable Integer reference) {
        try {
            log.info("Suppression du médicament {}", reference);

            Medicament med = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé avec la référence: " + reference));

            // Vider la liste des lignes pour activer la cascade delete
            med.getLignes().clear();

            // Supprimer le médicament (les lignes seront supprimées par cascade)
            medicamentRepository.deleteById(reference);
            log.info("Médicament {} et ses lignes supprimés avec succès", reference);

            return ResponseEntity.ok("Médicament supprimé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du médicament {}: {}", reference, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
