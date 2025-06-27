package org.gladyshev.ru.userservice1.Repository;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.gladyshev.ru.userservice1.Entity.User;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByName(String name);
    Optional<User> findById(Integer id);
    boolean existsByName(String name);
    boolean existsByEmail(String email);
}
