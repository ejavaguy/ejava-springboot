package info.ejava.assignments.race.client.races;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaceListDTO {
    private List<RaceDTO> races=new ArrayList<>();
}
