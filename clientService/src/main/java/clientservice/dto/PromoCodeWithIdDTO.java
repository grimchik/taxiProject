package clientservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromoCodeWithIdDTO
{
    private Long id;
    private Long percent;
    private LocalDate activationDate;
    private String keyword;
    private LocalDate expiryDate;
}
