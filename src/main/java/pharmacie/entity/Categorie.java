package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@ToString
public class Categorie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer code;

	@NonNull
	@Size(min = 1, max = 255)
	@Column(unique = true, length = 255)
	@NotBlank // pour éviter les libellés vides
	private String libelle;

	@Size(max = 255)
	@Column(length = 255)
	private String description;

	@ToString.Exclude
	// CascadeType.ALL signifie que toutes les opérations CRUD sur la catégorie sont
	// également appliquées à ses médicaments
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "categorie")
	// pour éviter la boucle infinie si on convertit la catégorie en JSON
	@JsonIgnoreProperties({ "categorie", "lignes" })
	private List<Medicament> medicaments = new LinkedList<>();

	// Une catégorie peut être fournie par plusieurs fournisseurs
	@ToString.Exclude
	@ManyToMany(mappedBy = "categories")
	@JsonIgnoreProperties("categories")
	private List<Fournisseur> fournisseurs = new LinkedList<>();

	// No-arg constructor for JPA and tests
	public Categorie() {
	}

	// Constructeur avec libelle (Lombok @RequiredArgsConstructor)
	public Categorie(String libelle) {
		this.libelle = libelle;
	}

	// Getters
	public Integer getCode() {
		return code;
	}

	public String getLibelle() {
		return libelle;
	}

	public String getDescription() {
		return description;
	}

	public List<Medicament> getMedicaments() {
		return medicaments;
	}

	public List<Fournisseur> getFournisseurs() {
		return fournisseurs;
	}

	// Setters
	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setMedicaments(List<Medicament> medicaments) {
		this.medicaments = medicaments;
	}

	public void setFournisseurs(List<Fournisseur> fournisseurs) {
		this.fournisseurs = fournisseurs;
	}

}
