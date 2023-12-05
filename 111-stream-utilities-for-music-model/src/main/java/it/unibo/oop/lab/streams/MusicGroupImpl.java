package it.unibo.oop.lab.streams;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    @Override
    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    @Override
    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    @Override
    public Stream<String> orderedSongNames() {
        return this.songs.stream()
                    .map(s -> s.getSongName())
                    .sorted();
    }

    @Override
    public Stream<String> albumNames() {
        return this.albums.keySet()
                    .stream();
    }

    @Override
    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet()
                    .stream()
                    .filter(album -> album.getValue().equals(year))
                    .map(album -> album.getKey());
    }

    @Override
    public int countSongs(final String albumName) {
        return (int) this.songs.stream()
                    .filter(s -> s.getAlbumName().isPresent())
                    .filter(s -> s.getAlbumName().equals(Optional.ofNullable(albumName)))
                    .count();
    }

    @Override
    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
                    .filter(s -> s.getAlbumName().isEmpty())
                    .count();
    }

    @Override
    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.songs.stream()
                    .filter(s -> s.getAlbumName().equals(Optional.ofNullable(albumName)))
                    .mapToDouble(s -> s.getDuration())
                    .reduce((a, b) -> a=(a+b)/countSongs(albumName));
    }

    @Override
    public Optional<String> longestSong() {
        return this.songs.stream()
                    .max((s1, s2) -> Double.compare(s1.getDuration(), s2.getDuration()))
                    .map(s -> s.getSongName());
    }

    @Override
    public Optional<String> longestAlbum() {
        return this.songs.stream()
                    .filter(s -> s.getAlbumName().isPresent())
                    .collect(Collectors.groupingBy(s -> s.getAlbumName(), Collectors.averagingDouble(s -> s.getDuration())))
                    .entrySet()
                    .stream()
                    .max((a1, a2) -> Double.compare(a1.getValue(), a2.getValue()))
                    .map(a -> a.getKey())
                    .get();
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        @Override
        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
