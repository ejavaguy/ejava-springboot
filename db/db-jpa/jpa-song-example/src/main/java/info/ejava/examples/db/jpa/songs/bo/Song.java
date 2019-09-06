package info.ejava.examples.db.jpa.songs.bo;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name="REPOSONGS_SONG")
@Getter
@ToString
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
@NamedQuery(name="Song.findByArtistGESize",
        query="select s from Song s where length(s.artist) >= :length")
@NamedQuery(name="Song.songCount",
        query="select count(s) from Song s")
@NamedQuery(name="Song.songs",
        query="select s from Song s")
@NamedQuery(name="Song.deleteSong",
        query="delete from Song s where s.id=:id")
public class Song {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "ID", nullable = false, insertable = true, updatable = false)
    private int id;
    @Setter
    @Column(name="TITLE", length=255, nullable = true, insertable = true, updatable = true)
    private String title;
    @Setter
    private String artist;
    @Setter
    private LocalDate released;
}
