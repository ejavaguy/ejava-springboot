package info.ejava_student.assignment1.logging.race.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class RaceResult {
    String raceId;
    String racerId;
    Instant time;

    @Override
    public String toString() {
        String result = "RaceResult{" +
                "raceId='" + raceId + '\'' +
                ", racerId='" + racerId + '\'' +
                ", time=" + time +
                '}';
        return result;
    }
}
