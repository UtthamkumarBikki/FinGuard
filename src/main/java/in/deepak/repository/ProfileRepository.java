package in.deepak.repository;

import in.deepak.entity.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<ProfileEntity,Long> {

  Optional<ProfileEntity> findByEmail(String email);

  // Method to find profile by activation token
  Optional<ProfileEntity> findByActivationToken(String token);
}
