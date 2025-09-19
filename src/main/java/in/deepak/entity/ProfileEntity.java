package in.deepak.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="tbl_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;

    //every email should be unique
    @Column(unique = true)
    private String email;

    private String password;
    private String profileImageUrl;

    //her updatable is making to false beacuse no one can update the creation time stamp in db
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private Boolean isActive;
    private String activationToken;



    //This prepersist annotation is used to call this method before making the change in the entity when the table is createded and data is going to get inserted
    @PrePersist
    public void prePersist(){
        if(this.isActive==null){
            isActive = false;
        }
    }

}
