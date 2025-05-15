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

import java.util.ArrayList;
import java.util.Base64;
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
        List<RegistrationDetails> registrationList = registrationDetailsRepository.findAll();
        List<RegistrationResponseDto> participantRegistrationList = new ArrayList<>();
        for(RegistrationDetails registrations: registrationList){
            logger.info("The Participant Registration --{}",registrations);
            RegistrationResponseDto totalRegistrationList = new RegistrationResponseDto();
            totalRegistrationList.setAadhar(registrations.getAadhar());
            totalRegistrationList.setAge(registrations.getAge());
            totalRegistrationList.setContactNumber(registrations.getContactNumber());
            totalRegistrationList.setBloodGroup(registrations.getBloodGroup());
            totalRegistrationList.setDob(registrations.getDob());
            totalRegistrationList.setGender(registrations.getGender());
            totalRegistrationList.setEmergencyContact(registrations.getEmergencyContact());
            totalRegistrationList.setEmail(registrations.getEmail());
            totalRegistrationList.setEventName(registrations.getEventName());
            totalRegistrationList.setTsize(registrations.getTsize());
            totalRegistrationList.setImage(registrations.getImage()!= null ? Base64.getEncoder().encodeToString(registrations.getImage()) : null);
            totalRegistrationList.setParticipantId(registrations.getId());
            totalRegistrationList.setParticipantName(registrations.getParticipantName());
            participantRegistrationList.add(totalRegistrationList);
        }

        // Apply pagination manually
        int start = (int) resolvedPageable.getOffset();
        int end = Math.min((start + resolvedPageable.getPageSize()), participantRegistrationList.size());
        List<RegistrationResponseDto> pagedList = participantRegistrationList.subList(start, end);

        return new PageImpl<>(pagedList, resolvedPageable, participantRegistrationList.size());
    }
}
