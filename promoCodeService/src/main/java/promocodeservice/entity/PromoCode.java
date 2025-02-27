package promocodeservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Table(name="promocodes")
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "percent", nullable = false)
    private Long percent;

    @Column(name = "activation_date", nullable = false)
    private LocalDate activationDate;

    @Column(name = "keyword", nullable = false, unique = true)
    private String keyword;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;
}
