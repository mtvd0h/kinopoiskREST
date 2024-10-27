package ru.matveev.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matveev.project.models.FilmEntity;

import java.util.Optional;

public interface FilmEntityRepository extends JpaRepository<FilmEntity, Long> {
    Optional<FilmEntity> findByKinopoiskId(int kinopoiskId);
}
