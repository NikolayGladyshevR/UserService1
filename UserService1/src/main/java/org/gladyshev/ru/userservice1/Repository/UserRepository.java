package org.gladyshev.ru.userservice1.Repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.gladyshev.ru.userservice1.Entity.User;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByName(
            @UniqueElements(message = "Данное имя уже существует")
            @NotNull(message = "Имя не может быть пустым")
            @Size(min = 6, max = 49, message = "Число символов в имени должно быть от 6 до 49") String name);

    boolean existsByName(String name);
    boolean existsByEmail(String email);
}
