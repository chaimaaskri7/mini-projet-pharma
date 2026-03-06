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
import pharmacie.dao.LigneRepository;
import pharmacie.entity.Medicament;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/medicaments")
@Slf4j
public class MedicamentDeleteController {

    private static final Logger log = LoggerFactory.getLogger(MedicamentDeleteController.class);

    private final MedicamentRepository medicamentRepository;
    private final LigneRepository ligneRepository;

    public MedicamentDeleteController(MedicamentRepository medicamentRepository, LigneRepository ligneRepository) {
        this.medicamentRepository = medicamentRepository;
        this.ligneRepository = ligneRepository;
    }

    /**
     * Supprime un médicament et ses lignes associées
     */
    @DeleteMapping("/{reference}")
    @Transactional
    public ResponseEntity<String> supprimerMedicament(@PathVariable Integer reference) {
        try {
            log.info("Suppression du médicament {}", reference);

            Medicament med = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));

            // Supprimer les lignes d'abord
            ligneRepository.deleteByMedicamentReference(reference);

            // Puis supprimer le médicament
            medicamentRepository.deleteById(reference);
            log.info("Médicament {} supprimé avec succès", reference);

            return ResponseEntity.ok("Médicament supprimé avec succès");
        } catch (Exception e) {
            log.error("Erreur suppression médicament {}: {}", reference, e.getMessage());
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
