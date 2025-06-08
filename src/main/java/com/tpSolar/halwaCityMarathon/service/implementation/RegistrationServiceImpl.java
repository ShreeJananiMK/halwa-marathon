package com.tpSolar.halwaCityMarathon.service.implementation;

import com.tpSolar.halwaCityMarathon.config.WebConfig;
import com.tpSolar.halwaCityMarathon.dto.RegistrationRequestDto;
import com.tpSolar.halwaCityMarathon.dto.RegistrationResponseDto;
import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import com.tpSolar.halwaCityMarathon.repository.RegistrationDetailsRepository;
import com.tpSolar.halwaCityMarathon.service.RegistrationDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RegistrationServiceImpl implements RegistrationDetailsService {
    @Autowired
    WebConfig webConfig;

    private final Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    @Autowired
    RegistrationDetailsRepository registrationDetailsRepository;
    @Override
    public Page<RegistrationResponseDto> getRegistrationDetails(Map<String, String> requestParams, Pageable pageable) {
        Pageable resolvedPageable = webConfig.resolvePageable(requestParams, pageable);
        List<Object[]> registrationList = registrationDetailsRepository.getRegistrationsInfo();
        List<RegistrationResponseDto> participantRegistrationList = new ArrayList<>();
        for(Object[] registrations: registrationList){
            logger.info("The Participant Registration --{}",registrations);
            RegistrationResponseDto totalRegistrationList = new RegistrationResponseDto();
            totalRegistrationList.setAadhar(registrations[6].toString());
            totalRegistrationList.setAge(registrations[4].toString());
            totalRegistrationList.setContactNumber(registrations[9].toString());
            totalRegistrationList.setBloodGroup(registrations[7].toString());
            totalRegistrationList.setDob(LocalDate.parse(registrations[3].toString()));
            totalRegistrationList.setGender(registrations[5].toString());
            totalRegistrationList.setEmergencyContact(registrations[10].toString());
            totalRegistrationList.setEmail(registrations[8].toString());
            totalRegistrationList.setEventName(registrations[1].toString());
            totalRegistrationList.setTsize(registrations[11].toString());
            //totalRegistrationList.setImage(registrations.getImage()!= null ? Base64.getEncoder().encodeToString(registrations.getImage()) : null);
            totalRegistrationList.setImage(null);
            totalRegistrationList.setParticipantId((Long) registrations[0]);
            totalRegistrationList.setParticipantName(registrations[2].toString());
            participantRegistrationList.add(totalRegistrationList);
        }

        // Apply pagination manually
        int start = (int) resolvedPageable.getOffset();
        int end = Math.min((start + resolvedPageable.getPageSize()), participantRegistrationList.size());
        List<RegistrationResponseDto> pagedList = participantRegistrationList.subList(start, end);

        return new PageImpl<>(pagedList, resolvedPageable, participantRegistrationList.size());
    }
}
