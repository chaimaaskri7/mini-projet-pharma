package pharmacie.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import lombok.*;

@Entity
@ToString
@Table(uniqueConstraints = {
		@UniqueConstraint(columnNames = { "COMMANDE_NUMERO", "MEDICAMENT_REFERENCE" })
})
public class Ligne {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false)
	private Integer id;

	@JoinColumn(nullable = false)
	@ManyToOne(optional = false)
	@NonNull
	@JsonIgnoreProperties({ "lignes", "dispensaire" })
	private Commande commande;

	@JoinColumn(nullable = false)
	@ManyToOne(optional = false)
	@NonNull
	@JsonIgnoreProperties({ "lignes", "categorie" })
	private Medicament medicament;

	@Basic(optional = false)
	@Column(nullable = false)
	@NonNull
	private Integer quantite;

	// Constructeur pour @RequiredArgsConstructor
	public Ligne(Commande commande, Medicament medicament, Integer quantite) {
		this.commande = commande;
		this.medicament = medicament;
		this.quantite = quantite;
	}

	// Getters
	public Integer getId() {
		return id;
	}

	public Commande getCommande() {
		return commande;
	}

	public Medicament getMedicament() {
		return medicament;
	}

	public Integer getQuantite() {
		return quantite;
	}

	// Setters
	public void setCommande(Commande commande) {
		this.commande = commande;
	}

	public void setMedicament(Medicament medicament) {
		this.medicament = medicament;
	}

	public void setQuantite(Integer quantite) {
		this.quantite = quantite;
	}

}
