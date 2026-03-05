package pharmacie.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import pharmacie.entity.Commande;
import pharmacie.entity.Ligne;
import pharmacie.entity.Medicament;

import java.util.List;

// This will be AUTO IMPLEMENTED by Spring into a Bean called LigneRepository

public interface LigneRepository extends JpaRepository<Ligne, Integer> {
    List<Ligne> findByCommande(Commande commande);

    List<Ligne> findByMedicamentReference(Integer reference);

    List<Ligne> findByCommandeNumero(Integer numero);

    /**
     * On trouve au plus une ligne pour une commande donnée et un médicament donné
     * 
     * @param commande   la commande cherchée
     * @param medicament le médicament cherché
     * @return la ligne correspondante (optionnelle)
     */
    Optional<Ligne> findByCommandeAndMedicament(Commande commande, Medicament medicament);

    /**
     * Supprime toutes les lignes associées à un médicament
     * 
     * @param reference la référence du médicament
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM Ligne l WHERE l.medicament.reference = :reference")
    void deleteByMedicamentReference(@Param("reference") Integer reference);
}
