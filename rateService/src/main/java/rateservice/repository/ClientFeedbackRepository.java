package rateservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import rateservice.entity.ClientFeedback;

import java.util.List;

@Repository
public interface ClientFeedbackRepository extends JpaRepository<ClientFeedback,Long> {
    Page<ClientFeedback> findAllByUserId(Long userId, Pageable pageable);

    List<ClientFeedback> findByUserId(Long userId);
}
