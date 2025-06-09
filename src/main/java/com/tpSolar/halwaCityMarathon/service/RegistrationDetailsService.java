package com.tpSolar.halwaCityMarathon.service;

import com.tpSolar.halwaCityMarathon.dto.RegistrationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;


public interface RegistrationDetailsService {

    Page<RegistrationResponseDto> getRegistrationDetails(Map<String, String> requestParams, Pageable pageable) throws Exception;
}
