package com.gridhub.dtos;

import com.gridhub.enums.Role;

public record UserInfo(Role role, Long userSpecificId) {
}
