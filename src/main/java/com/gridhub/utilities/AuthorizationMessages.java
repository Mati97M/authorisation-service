package com.gridhub.utilities;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationProperties(prefix = "auth.msg")
@ConfigurationPropertiesScan
public record AuthorizationMessages(String accessForbidden,
                                    String accessToResourceGranted,
                                    String resourceNotFound,
                                    String resourceWasSuccessfullyRegistered,
                                    String methodNotAllowed,
                                    String resourceWasSuccessfullyUnregistered,
                                    String unknownError) {
}