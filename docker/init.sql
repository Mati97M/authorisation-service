CREATE TABLE IF NOT EXISTS resources (
    id SERIAL PRIMARY KEY,
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

INSERT INTO resources (endpointPath, serviceName, userSpecificId) VALUES
    ('/api/authorisation/resources', 'Authorisation Microservice', NULL),
    ('/api/blog/post', 'Blog Microservice', NULL),
    ('/api/blog/tag', 'Blog Microservice', NULL),
    ('/api/discount', 'Price Microservice', NULL),
    ('/api/discount/associate/product', 'Price Microservice', NULL),
    ('/api/discount/associate/user', 'Price Microservice', NULL),
    ('/api/order', 'Order Service', NULL),
    ('/api/products', 'Product Microservice', NULL),
    ('/api/products/variant/', 'Product Microservice', NULL),
    ('/api/products/media', 'Product Microservice', NULL),
    ('/api/notification/confirmEmail', 'Notification Microservice', NULL),
    ('/api/notification/deletedAccount', 'Notification Microservice', NULL),
    ('/api/notification/resetPassword', 'Notification Microservice', NULL),
    ('/api/notification/parcelReadyToPickup', 'Notification Microservice', NULL),
    ('/api/notification/productsAvailable', 'Notification Microservice', NULL),
    ('/api/notification/productsAmountIsLow', 'Notification Microservice', NULL),
    ('/api/notification/newBlogPosted', 'Notification Microservice', NULL),
    ('/api/notification/unhandledException', 'Notification Microservice', NULL);

INSERT INTO roles VALUES
    ('ADMIN'),
    ('USER_SPECIFIC'),
    ('BUYER'),
    ('LOGGED_USER'),
    ('NOT_RESTRICTED');

INSERT INTO resources_roles VALUES
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/authorisation/resources' AND serviceName = 'Authorisation Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/authorisation/resources' AND serviceName = 'Authorisation Microservice'), 'BUYER'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/authorisation/resources' AND serviceName = 'Authorisation Microservice'), 'LOGGED_USER'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/authorisation/resources' AND serviceName = 'Authorisation Microservice'), 'USER_SPECIFIC'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/authorisation/resources' AND serviceName = 'Authorisation Microservice'), 'NOT_RESTRICTED'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/blog/post' AND serviceName = 'Blog Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/blog/tag' AND serviceName = 'Blog Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/discount' AND serviceName = 'Price Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/discount/associate/product' AND serviceName = 'Price Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/discount/associate/user' AND serviceName = 'Price Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/discount/associate/user' AND serviceName = 'Price Microservice'), 'USER_SPECIFIC'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/order' AND serviceName = 'Order Service'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/order' AND serviceName = 'Order Service'), 'USER_SPECIFIC'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/products' AND serviceName = 'Product Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/products/variant/' AND serviceName = 'Product Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/products/media' AND serviceName = 'Product Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/notification/confirmEmail' AND serviceName = 'Notification Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/notification/deletedAccount' AND serviceName = 'Notification Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/notification/resetPassword' AND serviceName = 'Notification Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/notification/parcelReadyToPickup' AND serviceName = 'Notification Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/notification/productsAvailable' AND serviceName = 'Notification Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/notification/productsAmountIsLow' AND serviceName = 'Notification Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/notification/newBlogPosted' AND serviceName = 'Notification Microservice'), 'ADMIN'),
    ((SELECT id resources FROM resources WHERE endpointPath = '/api/notification/unhandledException' AND serviceName = 'Notification Microservice'), 'ADMIN');