CREATE TABLE IF NOT EXISTS resources (
    id BIGINT PRIMARY KEY,
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
    role varchar(15),
    PRIMARY KEY (resource_id, role),
    FOREIGN KEY (resource_id) REFERENCES resources(id)
    ON DELETE CASCADE,
    FOREIGN KEY (role) REFERENCES roles(role)
    ON DELETE CASCADE
);

INSERT INTO resources (id, endpointPath, serviceName, userSpecificId) VALUES
    (1, '/api/authorisation/resources', 'Authorisation Microservice', NULL),
    (2, '/api/blog/post', 'Blog Microservice', NULL),
    (3, '/api/blog/tag', 'Blog Microservice', NULL),
    (4, '/api/discount', 'Price Microservice', NULL),
    (5, '/api/discount/associate/product', 'Price Microservice', NULL),
    (6, '/api/discount/associate/user', 'Price Microservice', NULL),
    (7, '/api/order', 'Order Service', NULL),
    (8, '/api/products', 'Product Microservice', NULL),
    (9, '/api/products/variant/', 'Product Microservice', NULL),
    (10, '/api/products/media', 'Product Microservice', NULL),
    (11, '/api/notification/confirmEmail', 'Notification Microservice', NULL),
    (12, '/api/notification/deletedAccount', 'Notification Microservice', NULL),
    (13, '/api/notification/resetPassword', 'Notification Microservice', NULL),
    (14, '/api/notification/parcelReadyToPickup', 'Notification Microservice', NULL),
    (15, '/api/notification/productsAvailable', 'Notification Microservice', NULL),
    (16, '/api/notification/productsAmountIsLow', 'Notification Microservice', NULL),
    (17, '/api/notification/newBlogPosted', 'Notification Microservice', NULL),
    (18, '/api/notification/unhandledException', 'Notification Microservice', NULL);

INSERT INTO roles VALUES
    ('ADMIN'),
    ('USER_SPECIFIC'),
    ('BUYER'),
    ('LOGGED_USER'),
    ('NOT_RESTRICTED');

INSERT INTO resources_roles VALUES
    (1, 'ADMIN'),
    (1, 'BUYER'),
    (1, 'LOGGED_USER'),
    (1, 'USER_SPECIFIC'),
    (1, 'NOT_RESTRICTED'),
    (2, 'ADMIN'),
    (3, 'ADMIN'),
    (4, 'ADMIN'),
    (5, 'ADMIN'),
    (6, 'ADMIN'),
    (6, 'USER_SPECIFIC'),
    (7, 'ADMIN'),
    (7, 'USER_SPECIFIC'),
    (8, 'ADMIN'),
    (9, 'ADMIN'),
    (10, 'ADMIN'),
    (11, 'ADMIN'),
    (12, 'ADMIN'),
    (13, 'ADMIN'),
    (14, 'ADMIN'),
    (15, 'ADMIN'),
    (16, 'ADMIN'),
    (17, 'ADMIN'),
    (18, 'ADMIN');


