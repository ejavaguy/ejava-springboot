package info.ejava.assignments.race.client.racers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RacerListDTO {
    private List<RacerDTO> racers = new ArrayList<>();
}
