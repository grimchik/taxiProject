package clientservice.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class RidePageResponseDTO {
    private List<RideWithIdDTO> content;
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private boolean isFirst;
    private boolean isLast;
    private boolean isEmpty;
}
