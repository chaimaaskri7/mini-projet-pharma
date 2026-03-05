package pharmacie.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${postmark.api-key:}")
    private String postmarkApiKey;

    public EmailService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // Envoie un mail
    public void envoyerMail(String destinataire, String sujet, String contenuHtml) {
        // Si pas de clé API configurée, mode test
        if (postmarkApiKey == null || postmarkApiKey.isEmpty() || postmarkApiKey.contains("your-api-key")) {
            afficherMailEnTest(destinataire, sujet, contenuHtml);
            return;
        }

        // Sinon, tenter d'envoyer via Postmark
        try {
            envoyerViaPostmark(destinataire, sujet, contenuHtml);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du mail à {}", destinataire, e);
        }
    }

    // Envoie le mail via Postmark en appelant l'API
    private void envoyerViaPostmark(String destinataire, String sujet, String contenuHtml) throws Exception {
        // Construire le JSON du mail
        ObjectNode mailJson = objectMapper.createObjectNode();
        mailJson.put("From", "noreply@pharmacie.fr");
        mailJson.put("To", destinataire);
        mailJson.put("Subject", sujet);
        mailJson.put("HtmlBody", contenuHtml);

        // Préparer l'en-tête HTTP avec la clé API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Postmark-Server-Token", postmarkApiKey);

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(mailJson), headers);

        // Appel à l'API Postmark
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api.postmarkapp.com/email",
                    request,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Mail envoyé avec succès à {} via Postmark", destinataire);
            } else {
                log.error("Erreur Postmark - Code: {} - Corps: {}", response.getStatusCode(), response.getBody());
            }
        } catch (Exception e) {
            log.error("Erreur lors de l'appel API Postmark", e);
            throw e;
        }
    }

    // Affiche le mail dans les logs (mode test/développement)
    private void afficherMailEnTest(String destinataire, String sujet, String contenuHtml) {
        log.warn("========================================");
        log.warn("📧 MAIL SIMULÉ (Mode Test)");
        log.warn("========================================");
        log.warn("De: askrichayma7+pharmacy@gmail.com");
        log.warn("À: {}", destinataire);
        log.warn("Sujet: {}", sujet);
        log.warn("----------------------------------------");
        log.warn("Contenu HTML:\n{}", contenuHtml);
        log.warn("========================================");
    }
}
