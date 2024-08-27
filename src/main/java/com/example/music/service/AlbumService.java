package com.example.music.service;

import com.example.music.exception.BadRequestException;
import com.example.music.exception.ForbiddenException;
import com.example.music.entity.Album;
import com.example.music.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlbumService {
    @Autowired
    private AlbumRepository albumRepository;

    public static final String ALBUM_DOES_NOT_EXIST = "Album with the given ID: %d does not exist. Please try again";
    public static final String CAN_NOT_DELETE = "Cannot delete albums with the rating more than 4 points and and at least 10 ratings.";
    public static final String WRONG_RATING = "You can only rate with round points 1-5, please enter an valid rating.";
    public static final String ALBUM_EXISTS = "Album '%s' by '%s' already exists in the music library.";

    public List<Album> getAllAlbums() {
        return albumRepository.findAll();
    }

    public Album getAlbumById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ALBUM_DOES_NOT_EXIST, id));
    }

    public Album saveAlbum(Album album) {
        checkIfAlbumExists(album);
        return albumRepository.save(album);
    }

    public Album updateAlbum(Album album) {
        if (!albumRepository.existsById(album.getId())) {
            throw new BadRequestException(ALBUM_DOES_NOT_EXIST, album.getId());
        }
        updateAverageRating(album);
        return albumRepository.save(album);
    }

    public void deleteAlbum(Long id) {
        Album album = albumRepository.findById(id).orElse(null);

        if (album != null) {
            if (album.getAverageRating() > 4 && album.getRatings().size() >= 10) {
                throw new ForbiddenException(CAN_NOT_DELETE);
            }
            albumRepository.delete(album);
        } else {
            throw new BadRequestException(ALBUM_DOES_NOT_EXIST, id);
        }
    }

    public Album rateAlbum(Long id, String rating) {
        if (rating.matches(".*\\D.*")) {
            throw new ForbiddenException(WRONG_RATING);
        }

        Album album = albumRepository.findById(id).orElse(null);
        int intRating = Integer.parseInt(rating);

        if (album == null) {
            throw new BadRequestException(ALBUM_DOES_NOT_EXIST, id);
        }
        if (intRating < 1 || intRating > 5) {
            throw new ForbiddenException(WRONG_RATING);
        } else {
            album.addRating(intRating);
            albumRepository.save(album);
            return album;
        }
    }

    private static void updateAverageRating(Album album) {
        double averageRating = album.calculateAverageRating(album.getRatings());
        album.setAverageRating(averageRating);
    }

    private void checkIfAlbumExists(Album album) {
        boolean albumExists = albumRepository
                .findByNameAndArtistAndReleaseDate(album.getName(), album.getArtist(), album.getReleaseDate())
                .isPresent();

        if (albumExists) {
            throw new ForbiddenException(String.format(ALBUM_EXISTS, album.getName(), album.getArtist()));
        }
    }
}