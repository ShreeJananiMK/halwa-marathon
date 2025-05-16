package com.tpSolar.halwaCityMarathon.repository;

import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationDetailsRepository extends JpaRepository<RegistrationDetails, Long> {

    @Query(value= "select count(*) from registration_details where participant_aadhar = ?1 ",nativeQuery = true)
    Long alreadyRegisteredParticipant(String aadhar);

    @Query(value= "select participant_number from registration_details where participant_aadhar = ?1 ",nativeQuery = true)
    String getParticipantNumber (String aadhar);
}
