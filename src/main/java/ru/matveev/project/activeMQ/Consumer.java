package ru.matveev.project.activeMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import ru.matveev.project.models.Film;
import ru.matveev.project.models.FilmEntity;
import ru.matveev.project.repositories.FilmEntityRepository;
import ru.matveev.project.scheduler.SchedulerService;

import java.util.List;
import java.util.Optional;

@Component
public class Consumer {

    private final FilmEntityRepository filmRepository;
    private final ObjectMapper objectMapper;
    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public Consumer(FilmEntityRepository filmRepository, ObjectMapper objectMapper) {
        this.filmRepository = filmRepository;
        this.objectMapper = objectMapper;
    }


    @JmsListener(destination = "filmsQueue")
    public void readFilms(String message) {
        try {
            List<Film> films = objectMapper.readValue(message, new TypeReference<List<Film>>() {});
            logger.info("Сообщение получено: {}", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @JmsListener(destination = "filmsQueue")
    public void receiveMessage(String message) {
        try {
            List<Film> films = objectMapper.readValue(message, objectMapper.getTypeFactory().constructCollectionType(List.class, Film.class));
            for (Film film : films) {
                Optional<FilmEntity> existingFilm = filmRepository.findByKinopoiskId(film.getKinopoiskId());
                if (!existingFilm.isPresent()) {
                    FilmEntity filmEntity = new FilmEntity();
                    filmEntity.setKinopoiskId(film.getKinopoiskId());
                    filmEntity.setNameRu(film.getNameRu());
                    filmEntity.setRatingKinopoisk(film.getRatingKinopoisk());
                    filmEntity.setYear(film.getYear());
                    filmEntity.setShortDescription(film.getDescription());
                    filmRepository.save(filmEntity);
                    logger.info("Сохранен фильм: {}", filmEntity);
                } else {
                    logger.info("Фильм с kinopoiskId {} уже существует", film.getKinopoiskId());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Ошибка при обработке сообщения", e);
        }
    }

}
