package ru.matveev.project.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;

import java.util.HashSet;
import java.util.Set;
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "countries")
public class Country {

    @Id
    private int id;

    @XmlTransient
    @JsonIgnore
    @ManyToMany(mappedBy = "countries")
    private Set<Film> film = new HashSet<>();

    @Column(name = "country")
    private String country;

    public Country() {
    }

    public Country(String country) {
        this.country = country;
    }
    public Country(Set<Film> film, String country) {
        this.film = film;
        this.country = country;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
