package com.tpSolar.halwaCityMarathon.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig implements WebMvcConfigurer {
    public Pageable resolvePageable(Map<String, String> requestParams, Pageable pageable) {
            return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
    }
}
