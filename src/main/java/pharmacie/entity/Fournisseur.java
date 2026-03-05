package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@ToString
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NonNull
    @NotBlank
    @Column(length = 255)
    private String nom;

    @NonNull
    @Email
    @Column(length = 255, unique = true)
    private String email;

    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "fournisseur_categorie", joinColumns = @JoinColumn(name = "fournisseur_id"), inverseJoinColumns = @JoinColumn(name = "categorie_id"))
    private List<Categorie> categories = new LinkedList<>();

    // No-arg constructor for JPA and tests
    public Fournisseur() {
    }

    // Constructeur pour @RequiredArgsConstructor
    public Fournisseur(String nom, String email) {
        this.nom = nom;
        this.email = email;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getEmail() {
        return email;
    }

    public List<Categorie> getCategories() {
        return categories;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCategories(List<Categorie> categories) {
        this.categories = categories;
    }
}
