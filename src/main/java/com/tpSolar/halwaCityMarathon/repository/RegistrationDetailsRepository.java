package com.tpSolar.halwaCityMarathon.repository;

import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationDetailsRepository extends JpaRepository<RegistrationDetails, Long> {

    @Query(value= "select count(*) from registration_details where participant_aadhar = ?1 ",nativeQuery = true)
    Long alreadyRegisteredParticipant(String aadhar);

    @Query(value= "select participant_number from registration_details where participant_aadhar = ?1 ",nativeQuery = true)
    String getParticipantNumber (String aadhar);
    
    @Query(value = "select participant_number, event_name, participant_name, dob, participant_age, participant_gender, participant_aadhar, participant_blood_group, " +
            "participant_email, participant_contact, participant_emergency_contact, participant_tsize from registration_details ", nativeQuery = true)
    List<Object[]> getRegistrationsInfo();
}
