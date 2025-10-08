package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    void deleteById(Long id);

    @Modifying
    @Query("UPDATE User AS u SET u.name = :name, u.email = :email WHERE u.id = :id")
    void updateUser(@Param("id") Long id, @Param("name") String name, @Param("email") String email);

}
