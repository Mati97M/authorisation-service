package com.gridhub.utilities;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.msg")
public record AuthorizationMessages(String accessForbidden,
                                    String accessToResourceGranted,
                                    String resourceNotFound,
                                    String resourceWasSuccessfullyRegistered,
                                    String methodNotAllowed,
                                    String resourceWasSuccessfullyUnregistered,
                                    String unknownError) {
}