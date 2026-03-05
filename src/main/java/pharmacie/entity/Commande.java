package pharmacie.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Entity
@ToString
public class Commande {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(nullable = false)
	private Integer numero;

	@Basic(optional = false)
	@Column(nullable = false)
	@ToString.Exclude
	private LocalDate saisiele = LocalDate.now();

	@Basic(optional = true)
	private LocalDate envoyeele = null;

	@Column(precision = 18, scale = 2)
	@ToString.Exclude
	private BigDecimal port;

	@Size(max = 40)
	@Column(length = 40)
	private String destinataire;

	@Embedded
	private AdressePostale adresseLivraison;

	@Basic(optional = false)
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal remise = BigDecimal.ZERO;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "commande", orphanRemoval = true)
	@JsonIgnoreProperties({ "commande" })
	private List<Ligne> lignes = new LinkedList<>();

	@ManyToOne(optional = false)
	@NonNull
	@JsonIgnoreProperties({ "commandes" })
	private Dispensaire dispensaire;

	// No-arg constructor for JPA and tests
	public Commande() {
	}

	// Constructeur pour @RequiredArgsConstructor (Dispensaire est @NonNull)
	public Commande(Dispensaire dispensaire) {
		this.dispensaire = dispensaire;
	}

	// Getters
	public Integer getNumero() {
		return numero;
	}

	public LocalDate getSaisiele() {
		return saisiele;
	}

	public LocalDate getEnvoyeele() {
		return envoyeele;
	}

	public BigDecimal getPort() {
		return port;
	}

	public String getDestinataire() {
		return destinataire;
	}

	public AdressePostale getAdresseLivraison() {
		return adresseLivraison;
	}

	public BigDecimal getRemise() {
		return remise;
	}

	public List<Ligne> getLignes() {
		return lignes;
	}

	public Dispensaire getDispensaire() {
		return dispensaire;
	}

	// Setters
	public void setSaisiele(LocalDate saisiele) {
		this.saisiele = saisiele;
	}

	public void setEnvoyeele(LocalDate envoyeele) {
		this.envoyeele = envoyeele;
	}

	public void setPort(BigDecimal port) {
		this.port = port;
	}

	public void setDestinataire(String destinataire) {
		this.destinataire = destinataire;
	}

	public void setAdresseLivraison(AdressePostale adresseLivraison) {
		this.adresseLivraison = adresseLivraison;
	}

	public void setRemise(BigDecimal remise) {
		this.remise = remise;
	}

	public void setLignes(List<Ligne> lignes) {
		this.lignes = lignes;
	}

	public void setDispensaire(Dispensaire dispensaire) {
		this.dispensaire = dispensaire;
	}

}
