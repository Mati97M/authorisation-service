package com.gridhub.mappers;

import com.gridhub.dtos.ResourceAccessDTO;
import com.gridhub.dtos.ResourceDeregistrationDTO;
import com.gridhub.dtos.ResourceInfo;
import com.gridhub.dtos.ResourceRegistrationDTO;
import com.gridhub.models.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ResourceMapper {
    ResourceMapper INSTANCE = Mappers.getMapper(ResourceMapper.class);

    ResourceInfo mapToResourceInfo(ResourceAccessDTO resourceAccessDTO);

    Resource mapToResource(ResourceRegistrationDTO resourceRegistrationDTO);

    ResourceInfo mapToResourceInfo(ResourceDeregistrationDTO resourceDeregistrationDTO);
}
