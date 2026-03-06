package pharmacie.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.dao.CategorieRepository;
import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Medicament;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
     * Récupère tous les médicaments (GET)
     * Format compatible avec Spring Data REST pour le frontend
     */
    @GetMapping
    public ResponseEntity<?> listerMedicaments() {
        try {
            List<Medicament> medicaments = medicamentRepository.findAll();

            // Format Spring Data REST attendu par le frontend
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> embedded = new HashMap<>();
            embedded.put("medicaments", medicaments);
            response.put("_embedded", embedded);

            log.info("Liste de {} médicaments retournée", medicaments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des médicaments: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Recherche des médicaments par nom (GET)
     * Endpoint compatible avec Spring Data REST:
     * /search/findByNomContainingIgnoreCase?nom=xxx
     * IMPORTANT: Doit être AVANT GET /{reference} pour éviter que "search" soit
     * capturé comme référence
     */
    @GetMapping("/search/findByNomContainingIgnoreCase")
    public ResponseEntity<?> rechercherParNom(@RequestParam("nom") String nom) {
        try {
            List<Medicament> medicaments = medicamentRepository.findAll().stream()
                    .filter(m -> m.getNom().toLowerCase().contains(nom.toLowerCase()))
                    .toList();

            // Format Spring Data REST
            Map<String, Object> response = new HashMap<>();
            Map<String, Object> embedded = new HashMap<>();
            embedded.put("medicaments", medicaments);
            response.put("_embedded", embedded);

            log.info("Recherche '{}': {} résultats", nom, medicaments.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Récupère un médicament par son ID (GET)
     * IMPORTANT: Doit être APRÈS /search/... pour éviter les conflits de routing
     */
    @GetMapping("/{reference}")
    public ResponseEntity<?> getMedicament(@PathVariable Integer reference) {
        try {
            Medicament med = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé: " + reference));
            return ResponseEntity.ok(med);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du médicament {}: {}", reference, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erreur: " + e.getMessage());
        }
    }

    /**
     * Crée un nouveau médicament (POST)
     * Accepte categorieCode directement au lieu d'une URL de catégorie
     */
    @PostMapping
    public ResponseEntity<?> creerMedicament(@RequestBody Map<String, Object> data) {
        try {
            log.info("Création d'un nouveau médicament: {}", data);

            // Extraire et valider les données
            String nom = (String) data.get("nom");

            // Accepter soit categorieCode (number) soit categorie (URL)
            Integer tempCategorieCode = null;
            if (data.get("categorieCode") instanceof Number) {
                tempCategorieCode = ((Number) data.get("categorieCode")).intValue();
            } else if (data.get("categorie") instanceof String) {
                // Extraire l'ID de l'URL: "https://.../api/categories/1" -> 1
                String categorieUrl = (String) data.get("categorie");
                String[] parts = categorieUrl.split("/");
                tempCategorieCode = Integer.parseInt(parts[parts.length - 1]);
            }

            if (nom == null || nom.isBlank()) {
                return ResponseEntity.badRequest().body("Le nom est requis");
            }
            if (tempCategorieCode == null) {
                return ResponseEntity.badRequest()
                        .body("Le code catégorie est requis (utilisez 'categorieCode' ou 'categorie')");
            }

            // Variable finale pour utilisation dans lambda
            final Integer categorieCode = tempCategorieCode;

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
    public ResponseEntity<?> supprimerMedicament(@PathVariable Integer reference) {
        try {
            log.info("Suppression du médicament {}", reference);

            Medicament med = medicamentRepository.findById(reference)
                    .orElseThrow(() -> new RuntimeException("Médicament non trouvé avec la référence: " + reference));

            // Vider la liste des lignes pour activer la cascade delete
            med.getLignes().clear();

            // Supprimer le médicament (les lignes seront supprimées par cascade)
            medicamentRepository.deleteById(reference);
            log.info("Médicament {} et ses lignes supprimés avec succès", reference);

            // Renvoyer 204 No Content (pas de body) pour compatibilité frontend
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du médicament {}: {}", reference, e.getMessage(), e);
            // Renvoyer JSON pour les erreurs
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
