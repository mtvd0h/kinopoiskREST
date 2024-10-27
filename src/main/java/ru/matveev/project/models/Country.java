package ru.matveev.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "countries")
public class Country {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToMany(mappedBy = "countries")
    private Set<Film> film = new HashSet<>();

    @Column(name = "country")
    private String country;

    public Country() {
    }

    public Country(Set<Film> film, String country) {
        this.film = film;
        this.country = country;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Set<Film> getFilm() {
        return film;
    }

    public void setFilm(Set<Film> film) {
        this.film = film;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
