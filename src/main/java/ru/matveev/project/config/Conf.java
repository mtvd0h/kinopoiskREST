package ru.matveev.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import ru.matveev.project.models.Filters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

@Configuration
public class Conf {
    private static final Logger logger = LoggerFactory.getLogger(Conf.class);

    @Value("${kinopoisk.api.key}")
    private String kinopoiskApiKey;

    @Value("${mail.username}")
    private String mailUsername;

    @Value("${mail.password}")
    private String mailPassword;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HttpHeaders httpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-API-KEY", kinopoiskApiKey);
        return headers;
    }

    @Bean
    public Filters filters() {
        String URL = "https://kinopoiskapiunofficial.tech/api/v2.2/films/filters";
        try {
            HttpEntity httpEntity = new HttpEntity<>(httpHeaders());
            return restTemplate().exchange(URL, HttpMethod.GET, httpEntity, Filters.class).getBody();
        } catch (Exception e) {
            logger.error("Ошибка при запросе фильтров с API", e);
            return new Filters();
        }
    }

    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.mail.ru");
        mailSender.setPort(465);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtps");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");
        return mailSender;
    }
}
