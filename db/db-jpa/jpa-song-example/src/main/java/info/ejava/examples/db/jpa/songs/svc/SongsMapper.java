package info.ejava.examples.db.jpa.songs.svc;

import info.ejava.examples.db.jpa.songs.bo.Song;
import info.ejava.examples.db.jpa.songs.dto.SongDTO;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SongsMapper {
    public Date map(LocalDate ld) {
        Date dt=null;
        if (ld!=null) {
            Instant instant = ld.atStartOfDay().toInstant(ZoneOffset.UTC);
            dt=Date.from(instant);
        }
        return dt;
    }

    public LocalDate map(Date dt) {
        LocalDate ld = null;
        if (dt!=null) {
            Instant instant = dt.toInstant();
            ld = LocalDate.ofInstant(instant, ZoneOffset.UTC);
        }
        return ld;
    }

    public Song map(SongDTO dto) {
        Song bo = null;
        if (dto!=null) {
            bo = Song.builder()
                    .id(dto.getId())
                    .artist(dto.getArtist())
                    .title(dto.getTitle())
                    .released(dto.getReleased())
                    .build();
        }
        return bo;
    }

    public SongDTO map(Song bo) {
        SongDTO dto = null;
        if (bo!=null) {
            dto = SongDTO.builder()
                    .id(bo.getId())
                    .artist(bo.getArtist())
                    .title(bo.getTitle())
                    .released(bo.getReleased())
                    .build();
        }
        return dto;
    }

    public List<Song> map(Collection<SongDTO> dtos) {
        List<Song> bos = null;
        if (dtos!=null) {
            bos = dtos.stream().map(dto->map(dto)).collect(Collectors.toList());
        }
        return bos;
    }

    public Page<SongDTO> map(Page<Song> bos) {
        Page<SongDTO> dtos = null;
        if (bos!=null) {
            dtos = bos.map(bo->map(bo));
        }
        return dtos;
    }
}
