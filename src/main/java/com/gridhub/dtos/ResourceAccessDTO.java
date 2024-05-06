package com.gridhub.dtos;

import com.gridhub.enums.Role;

public record ResourceAccessDTO(String serviceName, String endpointPath, Role role, Long userSpecificId) {
}