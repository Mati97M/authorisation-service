CREATE TABLE IF NOT EXISTS resources (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    endpointPath TEXT NOT NULL,
    serviceName TEXT NOT NULL,
    userSpecificId BIGINT,
    UNIQUE (endpointPath, serviceName)
);

CREATE TABLE IF NOT EXISTS roles (
    role varchar(15) PRIMARY KEY
);

CREATE TABLE IF NOT EXISTS resources_roles (
    resource_id BIGINT,
    role varchar(15) DEFAULT 'ADMIN',
    PRIMARY KEY (resource_id, role),
    FOREIGN KEY (resource_id) REFERENCES resources(id)
    ON DELETE CASCADE,
    FOREIGN KEY (role) REFERENCES roles(role)
    ON DELETE CASCADE
);