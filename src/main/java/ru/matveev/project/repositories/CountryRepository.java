package ru.matveev.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.matveev.project.models.Country;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, String> {
    Optional<Country> findByCountry(String country);
}
