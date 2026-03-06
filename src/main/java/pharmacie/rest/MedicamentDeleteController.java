package pharmacie.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.dao.CategorieRepository;
import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Medicament;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/medicaments")
@Slf4j
public class MedicamentDeleteController {

    private static final Logger log = LoggerFactory.getLogger(MedicamentDeleteController.class);

    private final MedicamentRepository medicamentRepository;
    private final CategorieRepository categorieRepository;

    public MedicamentDeleteController(MedicamentRepository medicamentRepository,
            CategorieRepository categorieRepository) {
        this.medicamentRepository = medicamentRepository;
        this.categorieRepository = categorieRepository;
    }

    /**
     * Crée un nouveau médicament (POST)
     * Accepte categorieCode directement au lieu d'une URL de catégorie
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> creerMedicament(@RequestBody Map<String, Object> data) {
        try {
            log.info("Création d'un nouveau médicament: {}", data);

            // Extraire et valider les données
            String nom = (String) data.get("nom");
            Integer categorieCode = data.get("categorieCode") instanceof Number
                    ? ((Number) data.get("categorieCode")).intValue()
                    : null;

            if (nom == null || nom.isBlank()) {
                return ResponseEntity.badRequest().body("Le nom est requis");
            }
            if (categorieCode == null) {
                return ResponseEntity.badRequest().body("Le code catégorie est requis");
            }

            // Récupérer la catégorie
            Categorie categorie = categorieRepository.findById(categorieCode)
                    .orElseThrow(() -> new RuntimeException("Catégorie non trouvée: " + categorieCode));

            // Créer le médicament
            Medicament med = new Medicament();
            med.setNom(nom);
            med.setCategorie(categorie);

            // Champs optionnels
            if (data.containsKey("quantiteParUnite")) {
                med.setQuantiteParUnite((String) data.get("quantiteParUnite"));
            }
            if (data.containsKey("unitesEnStock")) {
                med.setUnitesEnStock(((Number) data.get("unitesEnStock")).intValue());
            }
            if (data.containsKey("imageURL")) {
                med.setImageURL((String) data.get("imageURL"));
            }
            if (data.containsKey("prixUnitaire")) {
                med.setPrixUnitaire(BigDecimal.valueOf(((Number) data.get("prixUnitaire")).doubleValue()));
            }
            if (data.containsKey("unitesCommandees")) {
                med.setUnitesCommandees(((Number) data.get("unitesCommandees")).intValue());
            }
            if (data.containsKey("niveauDeReappro")) {
                med.setNiveauDeReappro(((Number) data.get("niveauDeReappro")).intValue());
            }
            if (data.containsKey("indisponible")) {
                med.setIndisponible((Boolean) data.get("indisponible"));
            }

            Medicament saved = medicamentRepository.save(med);
            log.info("Médicament créé avec succès: ID {}", saved.getReference());

            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            log.error("Erreur lors de la création du médicament: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Modifie partiellement un médicament (PATCH)
     * Accepte n'importe quel champ du médicament et met à jour uniquement ceux
     * fournis
     */
    @PatchMapping("/{reference}")
    @Transactional
    public ResponseEntity<?> modifierMedicament(@PathVariable Integer reference,
            @RequestBody Map<String, Object> updates) {
        try {
            log.info("Modification partielle du médicament {} avec {}", reference, updates);

            Medicament med = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé avec la référence: " + reference));

            // Appliquer les modifications
            updates.forEach((key, value) -> {
                switch (key) {
                    case "nom" -> med.setNom((String) value);
                    case "quantiteParUnite" -> med.setQuantiteParUnite((String) value);
                    case "unitesEnStock" -> med.setUnitesEnStock(((Number) value).intValue());
                    case "imageURL" -> med.setImageURL((String) value);
                    case "prixUnitaire" -> med.setPrixUnitaire(BigDecimal.valueOf(((Number) value).doubleValue()));
                    case "unitesCommandees" -> med.setUnitesCommandees(((Number) value).intValue());
                    case "niveauDeReappro" -> med.setNiveauDeReappro(((Number) value).intValue());
                    case "indisponible" -> med.setIndisponible((Boolean) value);
                    default -> log.warn("Champ ignoré: {}", key);
                }
            });

            Medicament saved = medicamentRepository.save(med);
            log.info("Médicament {} modifié avec succès", reference);

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            log.error("Erreur lors de la modification du médicament {}: {}", reference, e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
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
