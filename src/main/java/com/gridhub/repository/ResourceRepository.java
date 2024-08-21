package com.gridhub.repository;

import com.gridhub.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    void deleteByServiceNameAndEndpointPath(String serviceName, String endpointPath);

    void deleteByServiceName(String serviceName);

    Optional<Resource> findByServiceNameAndEndpointPath(String serviceName, String endpointPath);

    List<Resource> findAll();

    List<Resource> findByServiceName(String serviceName);

    @Modifying
    @Query("update Resource r set r.userSpecificId = ?1 where r.serviceName = ?2 and r.endpointPath = ?3")
    int updateByServiceNameAndEndpointPath(long userSpecificId, String serviceName, String endpointPath);

}