package in.deepak.service;

import in.deepak.dto.AuthDTO;
import in.deepak.dto.ProfileDTO;
import in.deepak.entity.ProfileEntity;
import in.deepak.repository.ProfileRepository;
import in.deepak.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Value("{app.activation.url}")
    private String activationURL;



    public ProfileDTO registerProfile(ProfileDTO profileDTO){

        ProfileEntity newProfile = toEntity(profileDTO);
        newProfile.setActivationToken(UUID.randomUUID().toString());

        newProfile = profileRepository.save(newProfile);
        //Sending the actiation email to user

        String ActivationLink = activationURL+"/api/v1.0/activate?token=" + newProfile.getActivationToken();
        String subject = "Activate your ExpenseIQ account";
        String body = "Click on the following link to activate your account: " + ActivationLink;
        emailService.sendEmail(newProfile.getEmail(), subject, body);
        return toDTO(newProfile);

    }






//The below method is a helper method to convert dto to entity
    public ProfileEntity toEntity(ProfileDTO profileDTO){

        return ProfileEntity.builder()
                .id(profileDTO.getId())
                .fullName(profileDTO.getFullName())
                .email(profileDTO.getEmail())
                .password(passwordEncoder.encode(profileDTO.getPassword()))
                .profileImageUrl(profileDTO.getProfileImageUrl())
                .createdAt(profileDTO.getCreatedAt())
                .updatedAt(profileDTO.getUpdatedAt())
                .build();
    }



//The below method is a helper method used to convert the entity to dto
public ProfileDTO toDTO(ProfileEntity profileEntity){

    return ProfileDTO.builder()
            .id(profileEntity.getId())
            .fullName(profileEntity.getFullName())
            .email(profileEntity.getEmail())
            .profileImageUrl(profileEntity.getProfileImageUrl())
            .createdAt(profileEntity.getCreatedAt())
            .updatedAt(profileEntity.getUpdatedAt())
            .build();
}

// Below method is used to validate the activation token

    public boolean activateProfile(String ActivationToken){
       return profileRepository.findByActivationToken(ActivationToken)
               .map(profile -> {
            profile.setIsActive(true);
            profile.setActivationToken(null); // Clear the token after activation
            profileRepository.save(profile);
            return true;
        })
               .orElse(false);
    }

    //below method is used to check if the account is active or not
    public boolean isAccountActive(String email){
        return profileRepository.findByEmail(email)
                .map(ProfileEntity::getIsActive)
                .orElse(false);
    }

//below method is used to get the current logged in user profile abd return the profile entity
    public ProfileEntity getCurrentProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return profileRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + authentication.getName()));

    }

    public  ProfileDTO getPublicProfile(String email){
        ProfileEntity currentUser = null;
        if(email==null) {
            currentUser = getCurrentProfile();
        }
        else{
            currentUser = profileRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("Profile not found with email: " + email));
        }

        return ProfileDTO.builder()
                .id(currentUser.getId())
                .fullName(currentUser.getFullName())
                .email(currentUser.getEmail())
                .profileImageUrl(currentUser.getProfileImageUrl())
                .createdAt(currentUser.getCreatedAt())
                .updatedAt(currentUser.getUpdatedAt())
                .build();
    }


    //This method is used to authenticate the user and generate JWT token
    public Map<String, Object> authenticteAndGenerateToken(AuthDTO authDTO) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authDTO.getEmail(),authDTO.getPassword()));
            //generate JWT token
            String token = jwtUtil.generateToken(authDTO.getEmail());
            return Map.of(
                    "token" , token,
                    "user" , getPublicProfile(authDTO.getEmail())
            );

        }
        catch(Exception e){
            throw new RuntimeException("Invalid email or password");

        }
    }
}
