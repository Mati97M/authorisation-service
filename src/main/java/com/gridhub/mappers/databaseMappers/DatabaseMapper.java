package com.gridhub.mappers.databaseMappers;

import com.gridhub.enums.Role;
import com.gridhub.exceptions.DatabaseMapperIllegalStateException;
import com.gridhub.models.Resource;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Function;

@Slf4j
public class DatabaseMapper {
    //ResultSet has rows of joined resource and resources_roles
    public static Function<ResultSet, Resource> mapToResource() {
        return resultSet -> {
            String serviceName;
            String endpointPath;
            long userSpecificId;
            HashSet<Role> roles;
            long id = 0;

            try {
                id = resultSet.getLong("id");
                serviceName = resultSet.getString("serviceName");
                endpointPath = resultSet.getString("endpointPath");
                userSpecificId = resultSet.getLong("userSpecificId");
                roles = new HashSet<>();
                do {
                    if (resultSet.getLong("id") != id) {
                        resultSet.previous();
                        break;
                    }
                    roles.add(Role.valueOf(resultSet.getString("role")));
                } while (resultSet.next());
            } catch (SQLException e) {
                throw new DatabaseMapperIllegalStateException();
            }
            return new Resource(serviceName,
                    endpointPath,
                    new ArrayList<>(roles),
                    userSpecificId);
        };
    }

    public static Function<ResultSet, Role> mapToRoleFromString() {
        return resultSet -> {
            try {
                return Role.valueOf(resultSet.getString("role"));
            } catch (SQLException e) {
                throw new DatabaseMapperIllegalStateException();
            }
        };
    }
}