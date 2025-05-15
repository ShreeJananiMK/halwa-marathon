package com.tpSolar.halwaCityMarathon.repository;

import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationDetailsRepository extends JpaRepository<RegistrationDetails, Long> {

    @Query(value= "select exists (select 1 from registration_details where participant_aadhar = ?1 )",nativeQuery = true)
    Long alreadyRegisteredParticipant(String aadhar);

}
