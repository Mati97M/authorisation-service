package com.gridhub.exceptions;

import java.sql.SQLException;

public class RepositoryIllegalStateException extends IllegalStateException {
    public RepositoryIllegalStateException(SQLException e) {
        super(e);
    }
}
