package pharmacie.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Medicament;
import pharmacie.service.ApprovisionnementService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    private final MedicamentRepository medicamentRepository;
    private final ApprovisionnementService approvisionnementService;

    public TestController(MedicamentRepository medicamentRepository,
            ApprovisionnementService approvisionnementService) {
        this.medicamentRepository = medicamentRepository;
        this.approvisionnementService = approvisionnementService;
    }

    /**
     * Endpoint de test : réduit le stock de plusieurs médicaments
     * pour déclencher l'approvisionnement et affiche les emails en logs
     */
    @PostMapping("/declencheur-approvisionnement")
    public String declencherApprovisionnementDeTest() {
        log.info("=== DÉCLENCHEUR TEST APPROVISIONNEMENT ===");

        // Médicaments à réduire : refs 4, 5, 21, 31, 41
        int[] refs = { 4, 5, 21, 31, 41 };

        for (int ref : refs) {
            Medicament med = medicamentRepository.findById(ref).orElse(null);
            if (med != null && med.getUnitesEnStock() > 0) {
                // Remplacer le stock à 0 pour déclencher le réapprovisionnement
                med.setUnitesEnStock(0);
                medicamentRepository.save(med);
                log.info("Stock du médicament {} réduit à 0", ref);
            }
        }

        // Lancer l'approvisionnement
        log.info("\n=== LANCEMENT APPROVISIONNEMENT ===\n");
        approvisionnementService.lancerApprovisionnement();

        return "Approvisionnement lancé ! Consultez les logs pour voir les mails envoyés.";
    }

    /**
     * Endpoint de test : affiche l'état des médicaments à réapprovisionner
     */
    @PostMapping("/reinitialiser")
    public String reinitialiser() {
        log.info("Réinitialisation de la base de données...");
        // La base se réinitialise automatiquement au redémarrage du serveur
        return "La base sera réinitialisée au prochain redémarrage du serveur.";
    }
}
