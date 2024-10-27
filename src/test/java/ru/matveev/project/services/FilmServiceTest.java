package ru.matveev.project.services;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.matveev.project.models.Country;
import ru.matveev.project.models.Film;
import ru.matveev.project.models.Genre;
import ru.matveev.project.models.Pages;
import ru.matveev.project.repositories.CountryRepository;
import ru.matveev.project.repositories.FilmRepository;
import ru.matveev.project.repositories.GenreRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @Mock
    private FilmRepository filmRepository;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CountryRepository countryRepository;

    @Mock
    private Sender sender;

    @InjectMocks
    private FilmService filmService;

    private List<Film> films;

    @BeforeEach
    void setUp() {
        Film testFilm = new Film();
        testFilm.setKinopoiskId(123);
        testFilm.setNameRu("фильм");
        testFilm.setCountries(Set.of(new Country("Россия")));
        testFilm.setGenres(Set.of(new Genre("комедия")));

        films = List.of(testFilm);
    }


    @Test
    public void testGetFilmsFromDB() {
        Map<String, String> params = Map.of("genre", "комедия", "country", "Россия");
        when(filmRepository.findAll()).thenReturn(films);

        List<Film> result = filmService.getFilmsFromDB();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("фильм", result.get(0).getNameRu());
    }

    @Test
    public void testGetFilms() {
        Map<String, String> params = Map.of("genre", "комедия", "country", "Россия");

        Pages pages = new Pages();
        pages.setItems(films);

        ResponseEntity<Pages> responseEntity = new ResponseEntity<>(pages, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Pages.class)))
                .thenReturn(responseEntity);

        List<Film> result = filmService.getFilms(params);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("фильм", result.get(0).getNameRu());
    }

    @Test
    public void testGetFilms_ApiError() {
        Map<String, String> params = Map.of("genre", "комедия", "country", "россия");

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Pages.class)))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            filmService.getFilms(params);
        });

        assertEquals("Ошибка при получении данных с API: 400 BAD_REQUEST", exception.getMessage());
    }

    @Test
    void testSaveFilms() {
        Film filmWithMoreInfo = new Film();
        filmWithMoreInfo.setDescription("описание");

        when(filmRepository.findByKinopoiskId(123)).thenReturn(Optional.empty());
        when(countryRepository.findByCountry("Россия")).thenReturn(Optional.empty());
        when(countryRepository.save(any(Country.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(genreRepository.findByGenre("комедия")).thenReturn(Optional.empty());
        when(genreRepository.save(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Film.class)))
                .thenReturn(new ResponseEntity<>(filmWithMoreInfo, HttpStatus.OK));

        filmService.saveFilms(films);

        ArgumentCaptor<Film> filmCaptor = ArgumentCaptor.forClass(Film.class);
        verify(filmRepository).save(filmCaptor.capture());
        Film savedFilm = filmCaptor.getValue();

        assertEquals("фильм", savedFilm.getNameRu());
        assertEquals("описание", savedFilm.getDescription());
        verify(countryRepository, times(1)).save(any(Country.class));
        verify(genreRepository, times(1)).save(any(Genre.class));
        verify(sender, times(1)).sendMail(eq("dmitry2sa1oksei@mail.ru"), eq(films));
    }


    @Test
    void testSaveFilms_DoesNotSaveExistingFilm() {
        Film existingFilm = new Film();
        existingFilm.setKinopoiskId(123);
        existingFilm.setNameRu("фильм");

        when(filmRepository.findByKinopoiskId(123)).thenReturn(Optional.of(existingFilm));

        filmService.saveFilms(films);

        verify(filmRepository, never()).save(existingFilm);
        verify(sender, never()).sendMail(anyString(), anyList());
    }

    @Test
    void testFilterFilms_ByCountry() {
        Film film1 = new Film();
        Country country1 = new Country();
        country1.setId(1);
        film1.setCountries(Set.of(country1));

        Film film2 = new Film();
        Country country2 = new Country();
        country2.setId(2);
        film2.setCountries(Set.of(country2));

        List<Film> filmList = Arrays.asList(film1, film2);
        List<Film> filteredFilms = filmService.filterFilms("countries", "1", filmList);

        assertEquals(1, filteredFilms.size());
        assertEquals(1, filteredFilms.get(0).getCountries().iterator().next().getId());
    }

    @Test
    void testFilterFilms_ByGenre() {
        Film film1 = new Film();
        Genre genre1 = new Genre();
        genre1.setId(1);
        film1.setGenres(Set.of(genre1));

        Film film2 = new Film();
        Genre genre2 = new Genre();
        genre2.setId(2);
        film2.setGenres(Set.of(genre2));

        List<Film> filmList = Arrays.asList(film1, film2);
        List<Film> filteredFilms = filmService.filterFilms("genres", "1", filmList);

        assertEquals(1, filteredFilms.size());
        assertEquals(1, filteredFilms.get(0).getGenres().iterator().next().getId());
    }
}
