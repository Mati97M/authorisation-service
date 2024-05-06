package com.gridhub.mappers;

import com.gridhub.dtos.ResourceAccessDTO;
import com.gridhub.dtos.ResourceDeregistrationDTO;
import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.ResourceRegistrationDTO;
import com.gridhub.dtos.UserInfo;
import com.gridhub.models.Resource;

public class ResourceDTOMapper {
    public UserInfo mapToUserInfo(ResourceAccessDTO resourceAccessDTO) {
        return new UserInfo(resourceAccessDTO.role(), resourceAccessDTO.userSpecificId());
    }

    public ResourceInfo mapToResourceInfo(ResourceAccessDTO resourceAccessDTO) {
        return new ResourceInfo(resourceAccessDTO.serviceName(), resourceAccessDTO.endpointPath());
    }

    public UserInfo mapToUserInfo(ResourceRegistrationDTO resourceRegistrationDTO) {
        return new UserInfo(resourceRegistrationDTO.role(), resourceRegistrationDTO.userSpecificId());
    }

    public Resource mapToResource(ResourceRegistrationDTO resourceRegistrationDTO) {
        return new Resource(resourceRegistrationDTO.serviceName(), resourceRegistrationDTO.endpointPath(), resourceRegistrationDTO.roles(), resourceRegistrationDTO.userSpecificId());
    }

    public UserInfo mapToUserInfo(ResourceDeregistrationDTO resourceDeregistrationDTO) {
        return new UserInfo(resourceDeregistrationDTO.role(), resourceDeregistrationDTO.userSpecificId());
    }

    public ResourceInfo mapToResourceInfo(ResourceDeregistrationDTO resourceDeregistrationDTO) {
        return new ResourceInfo(resourceDeregistrationDTO.serviceName(), resourceDeregistrationDTO.endpointPath());
    }
}