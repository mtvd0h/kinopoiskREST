package ru.matveev.project.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.matveev.project.models.*;
import ru.matveev.project.scheduler.SchedulerService;
import ru.matveev.project.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);
    private final FilmService filmService;
    private final SchedulerService schedulerService;

    @Autowired
    public FilmController(FilmService filmService, SchedulerService schedulerService) {
        this.filmService = filmService;
        this.schedulerService = schedulerService;
    }

    @GetMapping
    public List<Film> getAllFilms(@RequestParam Map<String, String> params) {
        logger.info("Получение всех фильмов с параметрами: {}", params);
        try {
            return filmService.getFilms(params);
        } catch (Exception e) {
            logger.error("Ошибка при получении всех фильмов", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
        }
    }

    @PostMapping
    public ResponseEntity<HttpStatus> saveFilms(@RequestParam Map<String, String> params) {
        logger.info("Сохранение фильмов с параметрами: {}", params);
        try {
            List<Film> films = getAllFilms(params);
            filmService.saveFilms(films);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при сохранении фильмов", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
        }
    }

    @GetMapping("/fromDB")
    public List<Film> getFromDB(@RequestParam Map<String, String> params) {
        logger.info("Получение фильмов из базы данных с параметрами: {}", params);
        List<Film> list = filmService.getFilmsFromDB();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            list = filmService.filterFilms(entry.getKey(), entry.getValue(), list);
        }
        return list;
    }

    @GetMapping("/filters")
    public Filters getAllFilters() {
        logger.info("Получение всех фильтров");
        return filmService.getFilters();
    }

    @PostMapping("/filters")
    public ResponseEntity<HttpStatus> saveFilters() {
        logger.info("Сохранение фильтров");
        try {
            Filters filters = getAllFilters();
            filmService.saveFilters(filters);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Ошибка при сохранении фильтров", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка сервера");
        }
    }

//    @PostMapping("/send")
//    public ResponseEntity<HttpStatus> message() {
//        schedulerService.execute();
//        return ResponseEntity.ok(HttpStatus.OK);
//    }
}
