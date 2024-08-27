package com.example.music.repository;

import com.example.music.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    Optional<Album> findByNameAndArtistAndReleaseDate(String name, String artist, String releaseDate);
}
