package ru.matveev.project.scheduler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import ru.matveev.project.models.Film;
import ru.matveev.project.models.Pages;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableScheduling
public class SchedulerService {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerService.class);
    private final JmsTemplate jmsTemplate;
    private final RestTemplate restTemplate;
    private final HttpHeaders httpHeaders;
    private final ObjectMapper objectMapper;

    public SchedulerService(JmsTemplate jmsTemplate, RestTemplate restTemplate, HttpHeaders httpHeaders, ObjectMapper objectMapper) {
        this.jmsTemplate = jmsTemplate;
        this.restTemplate = restTemplate;
        this.httpHeaders = httpHeaders;
        this.objectMapper = objectMapper;
    }

//    @Scheduled(cron = "0 * * * * ?")
//    public void testScheduler() {
//        logger.info("Текущие время: {}", LocalDateTime.now());
//    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void execute() {
        List<Film> films = getFilms(getGenres());
        sendFilms(films);
    }

    public String getGenres() {
        LocalDate today = LocalDate.now();
        DayOfWeek dayOfWeek = today.getDayOfWeek();
        String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films?genres=";

        switch (dayOfWeek) {
            case MONDAY:
                url += "1";
                break;
            case TUESDAY:
                url += "2";
                break;
            case WEDNESDAY:
                url += "3";
                break;
            case THURSDAY:
                url += "4";
                break;
            case FRIDAY:
                url += "5";
                break;
            case SATURDAY:
                url += "6";
                break;
            case SUNDAY:
                url += "7";
                break;
        }
        return url;
    }

    public List<Film> getFilms(String url) {
        List<Film> allFilms = new ArrayList<>();
        int page = 1;
        while (allFilms.size() < 50) {
            HttpEntity<String> entity = new HttpEntity<>(httpHeaders);
            ResponseEntity<Pages> response = restTemplate.exchange(url + "&page=" + page, HttpMethod.GET, entity, Pages.class);
            List<Film> films = response.getBody().getItems();
            if(films == null || films.isEmpty()) {
                break;
            }
            allFilms.addAll(films);
            page++;

            if(allFilms.size()>=50) {
                break;
            }
        }
        return allFilms.size() > 50 ? allFilms.subList(0, 50) : allFilms;
    }

    private void sendFilms(List<Film> films) {
        try {
            String json = objectMapper.writeValueAsString(films);
            jmsTemplate.convertAndSend("filmsQueue", json);
            logger.info("Отправлено сообщение: {}", json);
        } catch (Exception e) {
            logger.error("Ошибка при отправке сообщения", e);
        }
    }
}
