package ru.matveev.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "genres")
public class Genre {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonIgnore
    @ManyToMany(mappedBy = "genres")
    private Set<Film> film = new HashSet<>();

    @Column(name = "genre")
    private String genre;

    public Genre() {
    }

    public Genre(Set<Film> film, String genre) {
        this.film = film;
        this.genre = genre;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }
}
