package ru.matveev.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.matveev.project.models.*;
import ru.matveev.project.repositories.CountryRepository;
import ru.matveev.project.repositories.FilmRepository;
import ru.matveev.project.repositories.GenreRepository;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional(readOnly = true)
public class FilmService {
    private final Filters filters;
    private final GenreRepository genreRepository;
    private final FilmRepository filmRepository;
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final CountryRepository countryRepository;
    private final Sender sender;

    @Autowired
    public FilmService(Filters filters, GenreRepository genreRepository, FilmRepository filmRepository, RestTemplate restTemplate, HttpHeaders httpHeaders, CountryRepository countryRepository, Sender sender) {
        this.filters = filters;
        this.genreRepository = genreRepository;
        this.filmRepository = filmRepository;
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
        this.countryRepository = countryRepository;
        this.sender = sender;
    }

    public List<Film> getFilmsFromDB() {
        return filmRepository.findAll();
    }

    public List<Film> getFilms(Map<String, String> params) {
        String URL = "https://kinopoiskapiunofficial.tech/api/v2.2/films";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(URL);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder = builder.queryParam(entry.getKey(), entry.getValue());
        }
        String urlTemplate = builder.toUriString();
        urlTemplate = URLDecoder.decode(urlTemplate, StandardCharsets.UTF_8);

        HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
        ResponseEntity<Pages> response = restTemplate.exchange(urlTemplate, HttpMethod.GET, entity, Pages.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().getItems();
        } else {
            throw new RuntimeException("Ошибка при получении данных с API: " + response.getStatusCode());
        }
    }

    public Film getMoreInfo(Film film) {
        String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/" + film.getKinopoiskId();
        HttpEntity httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, Film.class).getBody();
    }

    @Transactional
    public void saveFilms(List<Film> films) {
        List<Film> filmsToSend = new ArrayList<>();
        for (Film film : films) {
            if (filmRepository.findByKinopoiskId(film.getKinopoiskId()).isEmpty()) {
                filterCountry(film);
                filterGenre(film);

                Film filmDescr = getMoreInfo(film);
                film.setDescription(filmDescr.getDescription());
                filmsToSend.add(film);
                filmRepository.save(film);
            } else {
                System.out.println("Фильм уже существует в базе: " + film.getNameRu());
            }
        }
        if (!filmsToSend.isEmpty()) {
            sender.sendMail("dmitry2sa1oksei@mail.ru", filmsToSend);
        }
    }

    private void filterCountry(Film film) {
        Set<Country> savedCountries = new HashSet<>();
        for (Country country : film.getCountries()) {
            Optional<Country> existingCountry = countryRepository.findByCountry(country.getCountry());
            if (existingCountry.isEmpty()) {
                savedCountries.add(countryRepository.save(country));
            } else {
                savedCountries.add(existingCountry.get());
            }
        }
        film.setCountries(savedCountries);
    }

    private void filterGenre(Film film) {
        Set<Genre> savedGenres = new HashSet<>();
        for (Genre genre : film.getGenres()) {
            Optional<Genre> existingGenre = genreRepository.findByGenre(genre.getGenre());
            if (existingGenre.isEmpty()) {
                savedGenres.add(genreRepository.save(genre));
            } else {
                savedGenres.add(existingGenre.get());
            }
        }
        film.setGenres(savedGenres);
    }

    public List<Film> filterFilms(String key, String value, List<Film> films) {
        if(key!=null)
        switch (key) {
            case "countries":
                films = films.stream().filter(film -> film.getCountries().stream()
                        .anyMatch(country -> country.getId() == Integer.parseInt(value)))
                        .collect(Collectors.toList());
                break;
            case "genres":
                films = films.stream().filter(film -> film.getGenres().stream()
                        .anyMatch(genre -> genre.getId() == Integer.parseInt(value)))
                        .collect(Collectors.toList());
                break;
            case "ratingFrom":
                films = films.stream().filter(film -> film.getRatingKinopoisk() >= Double.parseDouble(value))
                        .collect(Collectors.toList());
                break;
            case "ratingTo":
                films = films.stream().filter(film -> film.getRatingKinopoisk() <= Double.parseDouble(value))
                        .collect(Collectors.toList());
                break;
            case "yearFrom":
                films = films.stream().filter(film -> film.getYear() >= Integer.parseInt(value))
                        .collect(Collectors.toList());
                break;
            case "yearTo":
                films = films.stream().filter(film -> film.getYear() <= Integer.parseInt(value))
                        .collect(Collectors.toList());
                break;
            case "keyword":
                films = films.stream().filter(film -> film.getNameRu() !=null && film.getNameRu()
                        .toLowerCase().contains(value.toLowerCase()))
                        .collect(Collectors.toList());
                break;
            case "page":
                films = switchPages(films, value);
                break;
            default:
                break;
        }
        return films;
    }

    private List<Film> switchPages(List<Film> films, String value) {
        switch (value) {
            case "1":
                films = films.stream()
                        .limit(20).collect(Collectors.toList());
                break;
            case "2":
                films = films.stream().skip(20)
                        .limit(20).collect(Collectors.toList());
                break;
            case "3":
                films = films.stream().skip(40)
                        .limit(20).collect(Collectors.toList());
                break;
            case "4":
                films = films.stream().skip(60)
                        .limit(20).collect(Collectors.toList());
                break;
            case "5":
                films = films.stream().skip(80)
                        .limit(20).collect(Collectors.toList());
                break;
            default:
                break;
        }
        return films;
    }

    public Filters getFilters() {
        return filters;
    }

    @Transactional
    public void saveFilters(Filters filters) {
        countryRepository.saveAll(filters.getCountries());
        genreRepository.saveAll(filters.getGenres());
    }

}
