package com.example.music;

import com.example.music.exception.BadRequestException;
import com.example.music.exception.ForbiddenException;
import com.example.music.entity.Album;
import com.example.music.repository.AlbumRepository;
import com.example.music.service.AlbumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlbumServiceTest {

    @InjectMocks
    private AlbumService albumService;
    @Mock
    private AlbumRepository albumRepository;
    private Album testAlbum;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testAlbum = new Album("Test Album", "Test Artist", "2004", new ArrayList<>(List.of(2, 4, 5)));
        testAlbum.setId(1L);
    }

    @Test
    public void getAllAlbumsTest() {
        List<Album> albums = List.of(testAlbum);
        when(albumRepository.findAll()).thenReturn(albums);

        List<Album> result = albumService.getAllAlbums();
        assertEquals(1, result.size());
        assertEquals(testAlbum, result.get(0));
    }

    @Test
    public void getAlbumByIdFoundTest() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));

        Album result = albumService.getAlbumById(1L);
        assertEquals(testAlbum, result);
    }

    @Test
    public void getAlbumByIdNotFoundTest() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            albumService.getAlbumById(1L);
        });

        assertEquals(String.format(AlbumService.ALBUM_DOES_NOT_EXIST, 1L), thrown.getMessage());
    }

    @Test
    void saveAlbumNotDoesNotExistTest() {
        when(albumRepository.findByNameAndArtistAndReleaseDate(
                testAlbum.getName(), testAlbum.getArtist(), testAlbum.getReleaseDate()))
                .thenReturn(Optional.empty());
        when(albumRepository.save(any(Album.class))).thenReturn(testAlbum);

        Album savedAlbum = albumService.saveAlbum(testAlbum);

        assertNotNull(savedAlbum);
        assertEquals(testAlbum, savedAlbum);
        verify(albumRepository, times(1)).save(testAlbum);
    }

    @Test
    void saveAlbumAlbumAlreadyExistsTest() {
        when(albumRepository.findByNameAndArtistAndReleaseDate(
                testAlbum.getName(), testAlbum.getArtist(), testAlbum.getReleaseDate()))
                .thenReturn(Optional.of(testAlbum));

        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            albumService.saveAlbum(testAlbum);
        });

        assertEquals(String.format(AlbumService.ALBUM_EXISTS, testAlbum.getName(), testAlbum.getArtist()), thrown.getMessage());
        verify(albumRepository, never()).save(testAlbum);
    }

    @Test
    public void updateAlbumFoundTest() {
        when(albumRepository.existsById(1L)).thenReturn(true);
        when(albumRepository.save(any(Album.class))).thenReturn(testAlbum);

        Album result = albumService.updateAlbum(testAlbum);
        assertEquals(testAlbum, result);
        verify(albumRepository, times(1)).save(testAlbum);
    }

    @Test
    public void updateAlbumNotFoundTest() {
        when(albumRepository.existsById(1L)).thenReturn(false);

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            albumService.updateAlbum(testAlbum);
        });

        assertEquals(String.format(AlbumService.ALBUM_DOES_NOT_EXIST, 1L), thrown.getMessage());
    }

    @Test
    public void deleteAlbumFoundTest() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        doNothing().when(albumRepository).delete(any(Album.class));

        albumService.deleteAlbum(1L);
        verify(albumRepository, times(1)).delete(testAlbum);
    }

    @Test
    public void deleteAlbumNotFoundTest() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            albumService.deleteAlbum(1L);
        });

        assertEquals(String.format(AlbumService.ALBUM_DOES_NOT_EXIST, 1L), thrown.getMessage());
    }

    @Test
    public void deleteAlbumForbiddenTest() {
        testAlbum.setAverageRating(4.5); // Average rating must be greater than 4
        testAlbum.setRatings(List.of(1, 2, 3, 4, 5, 5, 5, 5, 5, 5)); // At least 10 ratings

        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));

        ForbiddenException thrown = assertThrows(ForbiddenException.class, () -> {
            albumService.deleteAlbum(1L);
        });

        assertEquals(AlbumService.CAN_NOT_DELETE, thrown.getMessage());
    }

    @Test
    public void rateAlbumValidRatingTest() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));
        when(albumRepository.save(any(Album.class))).thenReturn(testAlbum);

        Album result = albumService.rateAlbum(1L, "4");

        assertEquals(testAlbum, result);
        verify(albumRepository, times(1)).save(testAlbum);
    }

    @Test
    void rateAlbumInvalidRatingDigitsTest() {
        assertThrows(ForbiddenException.class, () -> {
            albumService.rateAlbum(1L, "rate5");
        });
    }

    @Test
    void rateAlbumInvalidRatingRangeTest() {
        when(albumRepository.findById(1L)).thenReturn(Optional.of(testAlbum));

        assertThrows(ForbiddenException.class, () -> {
            albumService.rateAlbum(1L, "10");
        });
    }

    @Test
    public void rateAlbumNotFoundTest() {
        when(albumRepository.findById(1L)).thenReturn(Optional.empty());

        BadRequestException thrown = assertThrows(BadRequestException.class, () -> {
            albumService.rateAlbum(1L, "3");
        });

        assertEquals(String.format(AlbumService.ALBUM_DOES_NOT_EXIST, 1L), thrown.getMessage());
    }
}
