package info.ejava_student.assignment3.security.race.racerregistrations;

import info.ejava_student.assignment2.api.race.client.raceregistrations.RacerRegistrationDTO;
import info.ejava_student.assignment2.api.race.client.raceregistrations.RacerRegistrationListDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class SecureRacerRegistrationServiceImpl /* implements RacerRegistrationService */ {
//    private final RacerRegistrationService serviceImpl;
//...

    public RacerRegistrationDTO createRegistration(String raceId, String racerId) {
//        RacerRegistrationDTO registration = serviceImpl.createRegistration(raceId, racerId);
        return null;
    }

    public RacerRegistrationDTO getRegistrationById(String id) {
//        return serviceImpl.getRegistrationById(id);
        return null;
    }

    public RacerRegistrationListDTO getRegistrations(Integer pageSize, Integer pageNumber) {
//        return serviceImpl.getRegistrations(pageSize, pageNumber);
        return null;
    }

    public void deleteRegistrationById(String id) {
//        serviceImpl.deleteRegistrationById(id);
    }

    public void deleteAllRegistrations() {
//        serviceImpl.deleteAllRegistrations();
    }
}
