package ru.matveev.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "films")
public class Film {

    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "kinopoisk_id")
    @JsonProperty("kinopoiskId")
    private int kinopoiskId;


    @Column(name = "name_ru")
    @JsonProperty("nameRu")
    private String nameRu;


    @ManyToMany
    @JoinTable(
            name = "film_country",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "country_id")
    )
    private Set<Country> countries = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "film_genre",
            joinColumns = @JoinColumn(name = "film_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @Column(name = "rating_kinopoisk")
    @JsonProperty("ratingKinopoisk")
    private Double ratingKinopoisk;


    @Column(name = "year")
    @JsonProperty("year")
    private Integer year;

    @Column(name = "description")
    @JsonProperty("shortDescription")
    private String description;

    public Film(Long id, int kinopoiskId, String nameRu, Set<Country> countries, Set<Genre> genres, Double ratingKinopoisk, Integer year, String description) {
        this.id = id;
        this.kinopoiskId = kinopoiskId;
        this.nameRu = nameRu;
        this.countries = countries;
        this.genres = genres;
        this.ratingKinopoisk = ratingKinopoisk;
        this.year = year;
        this.description = description;
    }

    public Film() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getKinopoiskId() {
        return kinopoiskId;
    }

    public void setKinopoiskId(int kinopoiskId) {
        this.kinopoiskId = kinopoiskId;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }

    public Set<Country> getCountries() {
        return countries;
    }

    public void setCountries(Set<Country> countries) {
        this.countries = countries;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Double getRatingKinopoisk() {
        return ratingKinopoisk;
    }

    public void setRatingKinopoisk(Double ratingKinopoisk) {
        this.ratingKinopoisk = ratingKinopoisk;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
