package clientservice.integrationtest;

import clientservice.entity.User;
import clientservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class ClientServiceRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindUser() {
        User user = new User();
        user.setUsername("Ivan");
        user.setPhone("+375333565900");
        user.setPassword("password123");
        userRepository.save(user);

        Optional<User> savedUser = userRepository.findById(user.getId());
        assertTrue(savedUser.isPresent());
        assertEquals("Ivan", savedUser.get().getUsername());
    }

    @Test
    public void testFindByUsername() {
        User user = new User();
        user.setUsername("Ivan1");
        user.setPhone("+375333565901");
        user.setPassword("password123");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("Ivan1");
        assertTrue(foundUser.isPresent());
        assertEquals("Ivan1", foundUser.get().getUsername());
    }

    @Test
    public void testFindByPhone() {
        User user = new User();
        user.setUsername("Ivan2");
        user.setPhone("+375333565902");
        user.setPassword("password123");
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByPhone("+375333565902");
        assertTrue(foundUser.isPresent());
        assertEquals("Ivan2", foundUser.get().getUsername());
    }

    @Test
    public void testSoftDeleteByUsername() {
        User user = new User();
        user.setUsername("Ivan3");
        user.setPhone("+375333565903");
        user.setPassword("password123");
        userRepository.save(user);

        userRepository.softDeleteByUsername("Ivan3");

        Optional<User> deletedUser = userRepository.findByUsername("Ivan3");
        assertTrue(deletedUser.isPresent());
        assertTrue(deletedUser.get().getIsDeleted());
    }

    @Test
    public void testFindAllByIsDeletedFalse() {
        User user1 = new User();
        user1.setUsername("Ivan4");
        user1.setPhone("+375333565904");
        user1.setPassword("password123");
        userRepository.save(user1);

        User user2 = new User();
        user2.setUsername("Pavel");
        user2.setPhone("+375333565905");
        user2.setPassword("password123");
        userRepository.save(user2);

        userRepository.softDeleteByUsername("Ivan4");

        List<User> activeUsers = userRepository.findAllByIsDeletedFalse();
        assertEquals(1, activeUsers.size());
        assertEquals("Pavel", activeUsers.get(0).getUsername());
    }
}