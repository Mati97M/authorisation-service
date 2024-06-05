package com.gridhub.utilities;

import lombok.Getter;

@Getter
public enum ConnectionProperties {
    POSTGRES("jdbc:postgresql:auth", "dev", "MyPass"),
    H2("jdbc:h2:mem:test;INIT=runscript from './src/test/create.sql'\\;runscript from './src/test/init.sql'",
            "dev", "testing");

    private final String url;
    private final String user;
    private final String password;

    ConnectionProperties(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
}
