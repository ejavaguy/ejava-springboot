package info.ejava.examples.db.jpa.songs.dao;

import info.ejava.examples.db.jpa.songs.bo.Song;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@Component
@RequiredArgsConstructor
@Slf4j
@Transactional
public class JpaSongDAO {
    private final EntityManager em;

    public boolean existsById(int id) {
        return em.createQuery("select count(s) from Song s where s.id=:id",
                Number.class)
                .setParameter("id", id)
                .getSingleResult()
                .longValue()==1L;
    }

    public Song findById(int id) {
        return em.find(Song.class, id);
    }

    public void create(Song song) {
        em.persist(song);
    }

    public Song update(Song song) {
        return em.merge(song);
    }

    public void delete(Song song) {
        em.remove(song);
    }

    public void deleteById(int id) {
        em.createNamedQuery("Song.deleteSong")
                .setParameter("id", id)
                .executeUpdate();
    }

    public void deleteAll() {
        em.createNativeQuery("delete from REPOSONGS_SONG")
                .executeUpdate();
    }

    public void flush() {
        em.flush();
    }

    public void clear() {
        em.clear();
    }
}
