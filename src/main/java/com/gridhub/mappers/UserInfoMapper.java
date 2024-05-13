package com.gridhub.mappers;

import com.gridhub.dtos.ResourceAccessDTO;
import com.gridhub.dtos.ResourceDeregistrationDTO;
import com.gridhub.dtos.ResourceRegistrationDTO;
import com.gridhub.dtos.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserInfoMapper {
    UserInfoMapper INSTANCE = Mappers.getMapper(UserInfoMapper.class);

    UserInfo mapToUserInfo(ResourceAccessDTO resourceAccessDTO);

    UserInfo mapToUserInfo(ResourceRegistrationDTO resourceRegistrationDTO);

    UserInfo mapToUserInfo(ResourceDeregistrationDTO resourceDeregistrationDTO);
}
