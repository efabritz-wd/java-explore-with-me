package ru.practicum.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByName(String name);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findAllById(List<Long> ids);
}