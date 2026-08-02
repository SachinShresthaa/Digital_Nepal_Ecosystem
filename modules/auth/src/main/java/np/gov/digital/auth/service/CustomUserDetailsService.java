package np.gov.digital.auth.service;

import lombok.RequiredArgsConstructor;
import np.gov.digital.auth.exception.ResourceNotFoundException;
import np.gov.digital.auth.repository.UserRepository;
import np.gov.digital.auth.security.CustomUserDetails;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        return repository.findByEmail(email)
                .map(CustomUserDetails::new)
                .orElseThrow(() ->
                    new ResourceNotFoundException("User not found"));
    }
}