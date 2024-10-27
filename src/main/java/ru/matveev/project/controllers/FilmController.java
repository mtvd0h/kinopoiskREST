package ru.matveev.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.matveev.project.models.*;
import ru.matveev.project.services.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getFilms();
    }

    @PostMapping
    public ResponseEntity<HttpStatus> saveFilms() {
        List<Film> films = getAllFilms();
        filmService.saveFilms(films);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/fromDB")
    public List<Film> getFromDB(@RequestParam Map<String, String> params) {
        List<Film> list = filmService.getFilmsFromDB();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            list = filmService.filterFilms(entry.getKey(), entry.getValue(), list);
        }
        return list;
    }

    @GetMapping("/filters")
    public Filters getAllFilters() {
        return filmService.getFilters();
    }

    @PostMapping("/filters")
    public ResponseEntity<HttpStatus> saveFilters() {
        Filters filters = getAllFilters();
        filmService.saveFilters(filters);
        return ResponseEntity.ok(HttpStatus.OK);
    }

}
