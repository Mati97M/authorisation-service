package com.gridhub.dtos;

import com.gridhub.enums.Role;

public record ResourceDeregistrationDTO(String serviceName, String endpointPath, Role role, Long userSpecificId) {
}
