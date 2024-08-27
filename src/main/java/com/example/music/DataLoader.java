package com.example.music;

import com.example.music.entity.Album;
import com.example.music.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private AlbumRepository albumRepository;

    public DataLoader() {
    }

    public DataLoader(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        albumRepository.save(new Album("What's Going On", "Marvin Gaye", "1971", List.of(1, 2, 3)));
        albumRepository.save(new Album("Abbey Road", "The Beatles", "1969", List.of(3, 5, 5, 4, 3, 2, 1, 5, 4, 3)));
        albumRepository.save(new Album("Stevie Wonder", "Stevie Wonder", "1976", List.of(5, 4, 5, 3, 1)));
        albumRepository.save(new Album("Nevermind", "Nirvana", "1991", List.of(1, 4, 3, 2, 5, 1, 5, 5, 4, 2, 3)));
        albumRepository.save(new Album("Thriller", "Michael Jackson", "1982", List.of(5, 4, 2, 3, 3, 3, 3, 2, 1)));
        albumRepository.save(new Album("Exile on Main Street", "The Rolling Stones", "1972", List.of(3, 4, 2, 1, 3, 5, 4, 5, 4, 5, 4, 1, 2)));
        albumRepository.save(new Album("Are You Experienced", "Jimi Hendrix", "1967", List.of(5, 5, 5, 5, 5, 3, 5, 5, 5, 5, 5, 5, 1)));
        albumRepository.save(new Album("Lemonade", "Beyoncé", "2016", List.of(2, 2, 1, 2, 1, 2, 1, 3, 5, 4, 2)));
        albumRepository.save(new Album("Back to Black", "Amy Winehouse", "2006", List.of(2, 2, 1, 2, 1, 2, 1, 3, 5, 4, 2)));
        albumRepository.save(new Album("The Blueprint", "Jay-Z", "2001", List.of()));
    }
}
