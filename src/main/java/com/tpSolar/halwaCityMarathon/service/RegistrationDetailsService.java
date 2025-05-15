package com.tpSolar.halwaCityMarathon.service;

import com.tpSolar.halwaCityMarathon.dto.RegistrationDto;
import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;


public interface RegistrationDetailsService {

    Page<RegistrationDto> getRegistrationDetails(Map<String, String> requestParams, Pageable pageable);
}
