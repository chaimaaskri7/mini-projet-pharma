package pharmacie.entity;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PreRemove;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
public class Medicament {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer reference = null;

	@NonNull // Lombok, génère une vérification dans le constructeur par défaut
	@Column(unique = true, length = 255)
	private String nom;

	private String quantiteParUnite = "Une boîte de 12";

	@PositiveOrZero
	private BigDecimal prixUnitaire = BigDecimal.TEN;

	/**
	 * Nombre d'unités en stock
	 * Décrémenté quand on expédie une commande contenant ce médicament
	 */
	@ToString.Exclude
	@PositiveOrZero
	private int unitesEnStock = 0;

	/**
	 * Nombre d'unités "en commande"
	 * Un médicament est "en commande" si il est dans une commande qui n'est pas
	 * encore expédiée
	 * Incrementé quand on ajoute des unités de ce médicament à une ligne de
	 * commande
	 * Décrémenté quand on expédie une commande contenant ce médicament
	 */
	@ToString.Exclude
	@PositiveOrZero
	private int unitesCommandees = 0;

	/**
	 * Niveau de reapprovisionnement
	 * Si le stock devient inférieur ou égal à ce niveau,
	 * on doit approvisionner de nouvelles unités de ce médicament auprès d'un
	 * fournisseur
	 */
	@ToString.Exclude
	@PositiveOrZero
	private int niveauDeReappro = 0;

	/**
	 * Indique si le médicament est indisponible
	 */
	@ToString.Exclude
	private boolean indisponible = false;

	/**
	 * URL de l'image du médicament (supporte base64 jusqu'à 50000 caractères)
	 */
	@Column(length = 50000, nullable = true)
	private String imageURL;

	@ToString.Exclude
	@JsonIgnoreProperties("medicaments") // pour éviter la boucle infinie si on convertit le médicament en JSON
	@NonNull // Lombok, génère une vérification dans le constructeur par défaut
	@ManyToOne(optional = false) // La clé étrangère ne peut pas être nulle dans la table Medicament
	private Categorie categorie;

	@ToString.Exclude
	@JsonIgnore // On n'inclut pas les lignes quand on convertit le médicament en JSON
	@OneToMany(mappedBy = "medicament", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH,
			CascadeType.REMOVE }, orphanRemoval = true)
	private List<Ligne> lignes = new LinkedList<>();

	// No-arg constructor for JPA and tests
	public Medicament() {
	}

	/**
	 * IMPORTANT: Vider les lignes AVANT la suppression pour éviter les conflits
	 * de contrainte de clé étrangère bidirectionnelle. Ça fonctionne indépendamment
	 * de cascadeType. Voir
	 * https://stackoverflow.com/questions/76044900/unsatisfieddependencyexception-error-creating-bean-named
	 */
	@PreRemove
	public void preRemove() {
		lignes.clear();
	}

	public Integer getReference() {
		return reference;
	}

	public void setReference(Integer reference) {
		this.reference = reference;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getQuantiteParUnite() {
		return quantiteParUnite;
	}

	public void setQuantiteParUnite(String quantiteParUnite) {
		this.quantiteParUnite = quantiteParUnite;
	}

	public BigDecimal getPrixUnitaire() {
		return prixUnitaire;
	}

	public void setPrixUnitaire(BigDecimal prixUnitaire) {
		this.prixUnitaire = prixUnitaire;
	}

	public int getUnitesEnStock() {
		return unitesEnStock;
	}

	public void setUnitesEnStock(int unitesEnStock) {
		this.unitesEnStock = unitesEnStock;
	}

	public int getUnitesCommandees() {
		return unitesCommandees;
	}

	public void setUnitesCommandees(int unitesCommandees) {
		this.unitesCommandees = unitesCommandees;
	}

	public int getNiveauDeReappro() {
		return niveauDeReappro;
	}

	public void setNiveauDeReappro(int niveauDeReappro) {
		this.niveauDeReappro = niveauDeReappro;
	}

	public boolean isIndisponible() {
		return indisponible;
	}

	public void setIndisponible(boolean indisponible) {
		this.indisponible = indisponible;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
	}

	public List<Ligne> getLignes() {
		return lignes;
	}

	public void setLignes(List<Ligne> lignes) {
		this.lignes = lignes;
	}
}