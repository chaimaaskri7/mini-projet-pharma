package pharmacie.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pharmacie.dao.FournisseurRepository;
import pharmacie.entity.Fournisseur;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurController {

    private final FournisseurRepository fournisseurDao;

    public FournisseurController(FournisseurRepository fournisseurDao) {
        this.fournisseurDao = fournisseurDao;
    }

    // GET : Lister tous les fournisseurs
    @GetMapping
    public List<Fournisseur> listerFournisseurs() {
        return fournisseurDao.findAll();
    }

    // POST : Ajouter un nouveau fournisseur
    @PostMapping
    public Fournisseur ajouterFournisseur(@RequestBody Fournisseur fournisseur) {
        return fournisseurDao.save(fournisseur);
    }
}
