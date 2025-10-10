package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Hit;

public interface StatsRepository extends JpaRepository<Hit, Long> {

}
