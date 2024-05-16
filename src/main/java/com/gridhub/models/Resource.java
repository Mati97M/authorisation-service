package com.gridhub.models;

import com.gridhub.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Resource {
    private String serviceName;
    private String endpointPath;
    private List<Role> roles;
    private Long userSpecificId;
}