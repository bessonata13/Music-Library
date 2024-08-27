package com.example.music.entity;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.util.*;
import java.util.OptionalDouble;

@Data
@Entity
@Table(name="album")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String artist;

    @Column(nullable = false)
    private String releaseDate;

    @Column
    private double averageRating;

    @ElementCollection
    private List<Integer> ratings = new ArrayList<>();

    public Album() {
    }
    public Album(String name, String artist) {
        this.name = name;
        this.artist = artist;
    }

    public Album(String name, String artist, String releaseDate, List<Integer> ratings) {
        this.name = name;
        this.artist = artist;
        this.releaseDate = releaseDate;
        this.ratings = ratings;
        this.averageRating = calculateAverageRating(this.ratings);
    }

    public void addRating(int rating) {
        ratings.add(rating);
        this.averageRating = calculateAverageRating(this.ratings);
    }

    public double calculateAverageRating (List<Integer> ratings){
        if (ratings.isEmpty()) {
            return 0.0;
        }
        OptionalDouble average = ratings.stream()
                .mapToInt(Integer::intValue)
                .average();

        return Math.round(average.orElse(0.0) * 10.0) / 10.0;
    }
}