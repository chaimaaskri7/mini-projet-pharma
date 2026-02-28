package pharmacie.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import pharmacie.dao.MedicamentRepository;
import pharmacie.dao.FournisseurRepository;
import pharmacie.entity.Medicament;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Categorie;

@Slf4j
@Service
public class ApprovisionnementService {

    private final MedicamentRepository medicamentDao;
    private final FournisseurRepository fournisseurDao;
    private final EmailService emailService;

    public ApprovisionnementService(
            MedicamentRepository medicamentDao,
            FournisseurRepository fournisseurDao,
            EmailService emailService) {
        this.medicamentDao = medicamentDao;
        this.fournisseurDao = fournisseurDao;
        this.emailService = emailService;
    }

    // Retourne les médicaments qui doivent être réapprovisionnés
    public List<Medicament> getMedicamentsAReapprovisionner() {
        List<Medicament> tous = medicamentDao.findAll();
        return tous.stream()
                .filter(med -> med.getUnitesEnStock() < med.getNiveauDeReappro())
                .toList();
    }

    // Envoie des mails de demande de réapprovisionnement aux fournisseurs
    @Transactional
    public void lancerApprovisionnement() {
        List<Medicament> aReapprovisionner = getMedicamentsAReapprovisionner();

        if (aReapprovisionner.isEmpty()) {
            log.info("Aucun médicament à réapprovisionner");
            return;
        }

        // Récupérer tous les fournisseurs
        List<Fournisseur> fournisseurs = fournisseurDao.findAll();

        // Pour chaque fournisseur, créer et envoyer un mail
        for (Fournisseur fournisseur : fournisseurs) {
            envoyerMailAuFournisseur(fournisseur, aReapprovisionner);
        }

        log.info("Approvisionnement lancé, {} fournisseurs notifiés", fournisseurs.size());
    }

    // Envoie un mail personnalisé à un fournisseur
    private void envoyerMailAuFournisseur(Fournisseur fournisseur, List<Medicament> medicamentsDisponibles) {
        // Grouper les médicaments à réapprovisionner par catégorie
        // mais seulement ceux que ce fournisseur peut fournir
        Map<Categorie, List<Medicament>> medicamentsParCategorie = new HashMap<>();

        for (Medicament med : medicamentsDisponibles) {
            Categorie cat = med.getCategorie();

            // Vérifier si ce fournisseur peut fournir cette catégorie
            if (fournisseur.getCategories().contains(cat)) {
                medicamentsParCategorie
                        .computeIfAbsent(cat, k -> new java.util.ArrayList<>())
                        .add(med);
            }
        }

        // Si ce fournisseur ne peut rien fournir, ne pas envoyer de mail
        if (medicamentsParCategorie.isEmpty()) {
            return;
        }

        // Construire le corps du mail
        String corps = construireCorpsEmail(fournisseur, medicamentsParCategorie);

        // Envoyer le mail
        try {
            emailService.envoyerMail(fournisseur.getEmail(), "Demande de réapprovisionnement en médicaments", corps);
            log.info("Mail envoyé à {}", fournisseur.getEmail());
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du mail à {}", fournisseur.getEmail(), e);
        }
    }

    // Construit le HTML du mail
    private String construireCorpsEmail(Fournisseur fournisseur,
            Map<Categorie, List<Medicament>> medicamentsParCategorie) {
        StringBuilder html = new StringBuilder();
        html.append("<h2>Demande de réapprovisionnement</h2>");
        html.append("<p>Bonjour ").append(fournisseur.getNom()).append(",</p>");
        html.append("<p>Nous vous demandons de nous transmettre un devis pour les médicaments suivants :</p>");

        for (Map.Entry<Categorie, List<Medicament>> entry : medicamentsParCategorie.entrySet()) {
            Categorie cat = entry.getKey();
            List<Medicament> meds = entry.getValue();

            html.append("<h3>").append(cat.getLibelle()).append("</h3>");
            html.append("<ul>");

            for (Medicament med : meds) {
                int quantite = med.getNiveauDeReappro() - med.getUnitesEnStock();
                html.append("<li>")
                        .append(med.getNom())
                        .append(" - Quantité demandée : ")
                        .append(quantite)
                        .append(" (Stock actuel : ")
                        .append(med.getUnitesEnStock())
                        .append(")</li>");
            }

            html.append("</ul>");
        }

        html.append("<p>Merci de votre réponse rapide.</p>");
        html.append("<p>Cordialement,<br/>La pharmacie</p>");

        return html.toString();
    }
}
