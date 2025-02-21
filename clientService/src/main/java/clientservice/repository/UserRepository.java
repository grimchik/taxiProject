package clientservice.repository;

import clientservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(String phone);
    List<User> findAllByIsDeletedFalse();
    @Modifying
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.username = :username")
    void softDeleteByUsername(@Param("username") String username);
}
