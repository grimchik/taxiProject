package promocodeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import promocodeservice.entity.PromoCode;

import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode,Long> {
    Optional<PromoCode> findByKeyword(String keyword);
}
