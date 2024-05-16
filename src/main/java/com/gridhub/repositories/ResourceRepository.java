package com.gridhub.repositories;

import com.gridhub.models.Resource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceRepository {
    private final Map<String, Resource> repository = new HashMap<>();
    private static ResourceRepository instance;

    public static ResourceRepository getInstance() {
        if (instance == null) {
            instance = new ResourceRepository();
        }
        return instance;
    }

    public void registerResource(Resource resource) {
        repository.put(resource.getServiceName(), resource);
    }

    public void unregisterResource(String serviceName) {
        repository.put(serviceName, null);
    }

    public Optional<Resource> getResource(String serviceName) {
        return Optional.ofNullable(repository.get(serviceName));
    }
}