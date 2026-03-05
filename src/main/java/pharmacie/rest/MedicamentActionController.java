package pharmacie.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.dao.LigneRepository;
import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Ligne;
import pharmacie.entity.Medicament;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/medicaments-actions")
@Slf4j
public class MedicamentActionController {

    private static final Logger log = LoggerFactory.getLogger(MedicamentActionController.class);

    private final MedicamentRepository medicamentRepository;
    private final LigneRepository ligneRepository;

    public MedicamentActionController(MedicamentRepository medicamentRepository, LigneRepository ligneRepository) {
        this.medicamentRepository = medicamentRepository;
        this.ligneRepository = ligneRepository;
    }

    /**
     * Ajoute 1 unité au stock du médicament
     */
    @PostMapping("/{reference}/ajouter-quantite")
    public ResponseEntity<Medicament> ajouterQuantite(@PathVariable Integer reference) {
        try {
            Medicament med = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));
            med.setUnitesEnStock(med.getUnitesEnStock() + 1);
            medicamentRepository.save(med);
            log.info("Ajouté 1 unité au médicament {}", reference);
            return ResponseEntity.ok(med);
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout de quantité pour {}: {}", reference, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Retire 1 unité au stock du médicament
     */
    @PostMapping("/{reference}/retirer-quantite")
    public ResponseEntity<Medicament> retirerQuantite(@PathVariable Integer reference) {
        try {
            Medicament med = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));
            if (med.getUnitesEnStock() > 0) {
                med.setUnitesEnStock(med.getUnitesEnStock() - 1);
                medicamentRepository.save(med);
            }
            log.info("Retiré 1 unité du médicament {}", reference);
            return ResponseEntity.ok(med);
        } catch (Exception e) {
            log.error("Erreur lors du retrait de quantité pour {}: {}", reference, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Modifie les informations d'un médicament
     */
    @PutMapping("/{reference}")
    public ResponseEntity<Medicament> modifierMedicament(@PathVariable Integer reference,
            @RequestBody Medicament medicamentModifie) {
        try {
            Medicament med = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));

            // Mettre à jour les champs
            med.setNom(medicamentModifie.getNom());
            med.setQuantiteParUnite(medicamentModifie.getQuantiteParUnite());
            med.setPrixUnitaire(medicamentModifie.getPrixUnitaire());
            med.setUnitesEnStock(medicamentModifie.getUnitesEnStock());
            med.setNiveauDeReappro(medicamentModifie.getNiveauDeReappro());

            if (medicamentModifie.getImageURL() != null && !medicamentModifie.getImageURL().isEmpty()) {
                med.setImageURL(medicamentModifie.getImageURL());
            }

            medicamentRepository.save(med);
            log.info("Médicament {} modifié", reference);
            return ResponseEntity.ok(med);
        } catch (Exception e) {
            log.error("Erreur lors de la modification du médicament {}: {}", reference, e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprime un médicament et ses lignes associées
     * Supprime d'abord les lignes pour éviter les conflits de clé étrangère
     */
    @DeleteMapping("/{reference}")
    public ResponseEntity<String> supprimerMedicament(@PathVariable Integer reference) {
        try {
            if (!medicamentRepository.existsById(reference)) {
                return ResponseEntity.notFound().build();
            }

            // Étape 1: Supprimer toutes les lignes associées au médicament
            // Utilise une requête DELETE directe pour éviter la cascade bidirectionnelle
            ligneRepository.deleteByMedicamentReference(reference);
            log.info("Supprimé les lignes associées au médicament {}", reference);

            // Étape 2: Supprimer le médicament
            medicamentRepository.deleteById(reference);
            log.info("Médicament {} supprimé", reference);
            return ResponseEntity.ok("Médicament supprimé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du médicament {}: {}", reference, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
