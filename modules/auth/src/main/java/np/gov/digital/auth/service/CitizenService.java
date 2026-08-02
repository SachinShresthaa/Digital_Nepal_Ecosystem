package np.gov.digital.auth.service;

import lombok.RequiredArgsConstructor;
import np.gov.digital.auth.entity.Citizen;
import np.gov.digital.auth.repository.CitizenRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CitizenService {

    private final CitizenRepository repository;

    public Citizen updateCitizen(Citizen citizen) {
        return repository.save(citizen);
    }

}