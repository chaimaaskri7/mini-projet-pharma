package pharmacie.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@ToString
public class Dispensaire {

    @Id
    @Basic(optional = false)
    @NonNull
    @Size(min = 1, max = 5)
    @Column(nullable = false, length = 5)
    private String code;

    @Basic(optional = false)
    @NonNull
    @Size(min = 1, max = 40)
    @Column(nullable = false, length = 40)
    private String nom;

    @Size(max = 30)
    @Column(length = 30)
    private String contact;

    @Size(max = 30)
    @Column(length = 30)
    private String fonction;

    @Embedded
    private AdressePostale adresse;

    @Size(max = 24)
    @Column(length = 24)
    private String telephone;

    @Size(max = 24)
    @Column(length = 24)
    private String fax;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "dispensaire")
    @ToString.Exclude
    @JsonIgnoreProperties({ "dispensaire", "lignes" })
    private List<Commande> commandes = new ArrayList<>();

    // Constructeur pour @RequiredArgsConstructor (code et nom sont @NonNull)
    public Dispensaire(String code, String nom) {
        this.code = code;
        this.nom = nom;
    }

    // Getters
    public String getCode() {
        return code;
    }

    public String getNom() {
        return nom;
    }

    public String getContact() {
        return contact;
    }

    public String getFonction() {
        return fonction;
    }

    public AdressePostale getAdresse() {
        return adresse;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getFax() {
        return fax;
    }

    public List<Commande> getCommandes() {
        return commandes;
    }

    // Setters
    public void setCode(String code) {
        this.code = code;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public void setFonction(String fonction) {
        this.fonction = fonction;
    }

    public void setAdresse(AdressePostale adresse) {
        this.adresse = adresse;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public void setCommandes(List<Commande> commandes) {
        this.commandes = commandes;
    }

}
