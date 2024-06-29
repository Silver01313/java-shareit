package ru.practicum.shareit.userTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;


@DataJpaTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {

    private final UserRepository userRepository;

    @Test
    void shouldCreateUser() {
        User user = new User();
        user.setName("name");
        user.setEmail("s@y.com");

        User savedUser = userRepository.save(user);

       assertEquals(savedUser.getId(), 1L);
        assertEquals(savedUser.getName(), "name");
    }
}
