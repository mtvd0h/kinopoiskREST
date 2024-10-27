package ru.matveev.project.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import ru.matveev.project.models.Country;
import ru.matveev.project.models.Film;
import ru.matveev.project.models.Filters;
import ru.matveev.project.models.Genre;
import ru.matveev.project.repositories.FilmRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class FilmControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private FilmRepository filmRepository;


    @Test
    void testGetAllFilms() {
        ResponseEntity<Film[]> responseEntity = restTemplate.getForEntity("/films", Film[].class);

        Film[] films = responseEntity.getBody();
        assertNotNull(films);
        assertEquals(OK, responseEntity.getStatusCode());
    }

    @Test
    void testSaveFilms() {
        Film testFilm = new Film();
        testFilm.setKinopoiskId(456);
        testFilm.setNameRu("Новый тестовый фильм");
        testFilm.setCountries(Set.of(new Country("Россия")));
        testFilm.setGenres(Set.of(new Genre("драма")));

        Film existingFilm = filmRepository.findByKinopoiskId(456).orElse(null);
        if (existingFilm == null) {
            ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/films", testFilm, Void.class);

            assertEquals(OK, responseEntity.getStatusCode());

            filmRepository.save(testFilm);

            Film savedFilm = filmRepository.findByKinopoiskId(456).orElse(null);
            assertNotNull(savedFilm);
            assertEquals("Новый тестовый фильм", savedFilm.getNameRu());
        } else {
            System.out.println("Фильм с таким ID уже существует в базе данных: " + existingFilm.getNameRu());
        }
    }

    @Test
    void testGetFromDB() {
        ResponseEntity<Film[]> responseEntity = restTemplate.getForEntity("/films/fromDB?keyword=Новый тестовый фильм", Film[].class);
        List<Film> films = Arrays.stream(responseEntity.getBody()).toList();

        assertNotNull(films);
        assertEquals(1, films.size());
        assertEquals("Новый тестовый фильм", films.get(0).getNameRu());
    }

    @Test
    void testGetAllFilters() {
        ResponseEntity<Filters> responseEntity = restTemplate.getForEntity("/films/filters", Filters.class);

        Filters filters = responseEntity.getBody();
        assertNotNull(filters);
    }

    @Test
    void testSaveFilters() {
        Filters filters = new Filters();
        filters.setCountries(List.of(new Country("Россия")));
        filters.setGenres(List.of(new Genre("комедия")));

        ResponseEntity<Void> responseEntity = restTemplate.postForEntity("/films/filters", filters, Void.class);

        assertEquals(OK, responseEntity.getStatusCode());

        ResponseEntity<Filters> savedFiltersResponse = restTemplate.getForEntity("/films/filters", Filters.class);
        Filters savedFilters = savedFiltersResponse.getBody();
        assertNotNull(savedFilters);

        List<Country> countries = savedFilters.getCountries();
        List<Genre> genres = savedFilters.getGenres();

        assertNotNull(countries);
        assertNotNull(genres);

        boolean countryFound = countries.stream().anyMatch(country -> "Россия".equals(country.getCountry()));
        boolean genreFound = genres.stream().anyMatch(genre -> "комедия".equals(genre.getGenre()));

        assertEquals(true, countryFound);
        assertEquals(true, genreFound);
    }
}
