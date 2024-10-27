package ru.matveev.project.models;

import java.util.List;

public class Filters {

    private List<Country> countries;
    private List<Genre> genres;

    public Filters() {

    }

    public List<Country> getCountries() {
        return countries;
    }

    public void setCountries(List<Country> countries) {
        this.countries = countries;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }
}
