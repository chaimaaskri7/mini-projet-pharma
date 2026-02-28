package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@ToString
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @NonNull
    @NotBlank
    @Column(length = 255)
    private String nom;

    @NonNull
    @Email
    @Column(length = 255, unique = true)
    private String email;

    // Un fournisseur peut fournir plusieurs catégories
    // Une catégorie est fournie par plusieurs fournisseurs
    @ToString.Exclude
    @ManyToMany
    @JoinTable(name = "fournisseur_categorie", joinColumns = @JoinColumn(name = "fournisseur_id"), inverseJoinColumns = @JoinColumn(name = "categorie_id"))
    private List<Categorie> categories = new LinkedList<>();
}
