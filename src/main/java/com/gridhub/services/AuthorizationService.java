package com.gridhub.services;

import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.UserInfo;
import com.gridhub.models.Resource;

public interface AuthorizationService {
    boolean hasPermissionToAccessResource(UserInfo userInfo, ResourceInfo resourceInfo);

    void registerResource(UserInfo userInfo, Resource resource);

    void unregisterResource(UserInfo userInfo, ResourceInfo resourceInfo);
}