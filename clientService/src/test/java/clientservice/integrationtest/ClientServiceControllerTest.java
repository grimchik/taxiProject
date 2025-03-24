package clientservice.integrationtest;

import clientservice.dto.UserDTO;
import clientservice.dto.UpdateUserDTO;
import clientservice.entity.User;
import clientservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClientServiceControllerTest {

    private MockMvc mockMvc;
    private UserRepository userRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public void setMockMvc(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testSignUpUser() throws Exception {
        UserDTO userDTO = new UserDTO("Ivan", "password123", "+375333565900");

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testGetProfile() throws Exception {
        User user = new User();
        user.setUsername("Ivan2");
        user.setPassword("password123");
        user.setPhone("+375333565901");
        userRepository.save(user);

        mockMvc.perform(get("/api/v1/users/{id}", user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ivan2"));
    }

    @Test
    public void testUpdateProfile() throws Exception {
        User user = new User();
        user.setUsername("Ivan3");
        user.setPassword("password123");
        user.setPhone("+375333565902");
        userRepository.save(user);

        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setUsername("Ivan55");

        mockMvc.perform(patch("/api/v1/users/{id}", user.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("Ivan55"));
    }

    @Test
    public void testDeleteUser() throws Exception {
        User user = new User();
        user.setUsername("Ivan3");
        user.setPassword("password123");
        user.setPhone("+375333565903");
        userRepository.save(user);

        mockMvc.perform(delete("/api/v1/users/{id}", user.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetAllProfiles() throws Exception {
        User user = new User();
        user.setUsername("Ivan4");
        user.setPassword("password123");
        user.setPhone("+375333565904");
        User user1 = new User();
        user1.setUsername("Ivan5");
        user1.setPassword("password123");
        user1.setPhone("+375333565905");

        userRepository.save(user);
        userRepository.save(user1);

        mockMvc.perform(get("/api/v1/users")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(5));
    }
}