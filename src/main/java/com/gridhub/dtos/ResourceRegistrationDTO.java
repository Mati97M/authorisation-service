package com.gridhub.dtos;

import com.gridhub.enums.Role;

import java.util.List;

public record ResourceRegistrationDTO(String serviceName, String endpointPath, List<Role> roles, Long userSpecificId,
                                      Role role) {
}