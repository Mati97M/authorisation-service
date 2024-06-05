package com.gridhub.repositories;

import com.gridhub.enums.Role;
import com.gridhub.models.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public interface Repository {
    void saveResource(Resource resource);

    void deleteResource(String serviceName, String endpointPath);

    default void deleteResource(String serviceName) {
    }

    Optional<Resource> findResource(String serviceName, String endpointPath);

    default List<Resource> findAllResources() {
        return List.of();
    }

    default List<Resource> findResourceByServiceName(String serviceName) {
        return List.of();
    }

    default void updateResource(Resource resource) {
    }

    default Optional<Role> findRole(String role) {
        return Optional.empty();
    }

    default List<Role> findAllRoles() {
        return List.of();
    }

    default Map<Role, Integer> findRoleCounts(Predicate<Resource> predicate) {
        return Map.of();
    }
}
