package pharmacie.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.service.ApprovisionnementService;
import pharmacie.entity.Medicament;

@RestController
@RequestMapping("/api/approvisionnement")
public class ApprovisionnementController {

	private final ApprovisionnementService approvisionnementService;

	public ApprovisionnementController(ApprovisionnementService approvisionnementService) {
		this.approvisionnementService = approvisionnementService;
	}

	// GET : Voir les médicaments à réapprovisionner
	@GetMapping("/medicaments-manquants")
	public List<Medicament> getMedicamentsAReapprovisionner() {
		return approvisionnementService.getMedicamentsAReapprovisionner();
	}

	// POST : Lancer l'approvisionnement (envoie les mails)
	@PostMapping("/lancer")
	public String lancerApprovisionnement() {
		approvisionnementService.lancerApprovisionnement();
		return "Approvisionnement lancé !";
	}
}
