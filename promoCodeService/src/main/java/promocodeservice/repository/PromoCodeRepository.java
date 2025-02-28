package promocodeservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import promocodeservice.entity.PromoCode;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode,Long> {
}
