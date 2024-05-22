CREATE TABLE IF NOT EXISTS resources (
    endpointPath TEXT PRIMARY KEY,
    serviceName TEXT NOT NULL,
    roles TEXT NOT NULL,
    userSpecificId BIGINT
);

INSERT INTO resources (endpointPath, serviceName, roles, userSpecificId) VALUES
('/api/authorisation/resources', 'Authorisation Microservice', 'ADMIN BUYER LOGGED_USER USER_SPECIFIC NOT_RESTRICTED', NULL),
('/api/blog/post', 'Blog Microservice', 'ADMIN', NULL),
('/api/blog/tag', 'Blog Microservice', 'ADMIN', NULL),
('/api/discount', 'Price Microservice', 'ADMIN', NULL),
('/api/discount/associate/product', 'Price Microservice', 'ADMIN', NULL),
('/api/discount/associate/user', 'Price Microservice', 'ADMIN', NULL),
('/api/order', 'Order Service', 'ADMIN USER_SPECIFIC', NULL),
('/api/products', 'Product Microservice', 'ADMIN', NULL),
('/api/products/variant/', 'Product Microservice', 'ADMIN', NULL),
('/api/products/media', 'Product Microservice', 'ADMIN', NULL),
('/api/notification/confirmEmail', 'Notification Microservice', 'ADMIN', NULL),
('/api/notification/deletedAccount', 'Notification Microservice', 'ADMIN', NULL),
('/api/notification/resetPassword', 'Notification Microservice', 'ADMIN', NULL),
('/api/notification/parcelReadyToPickup', 'Notification Microservice', 'ADMIN', NULL),
('/api/notification/productsAvailable', 'Notification Microservice', 'ADMIN', NULL),
('/api/notification/productsAmountIsLow', 'Notification Microservice', 'ADMIN', NULL),
('/api/notification/newBlogPosted', 'Notification Microservice', 'ADMIN', NULL),
('/api/notification/unhandledException', 'Notification Microservice', 'ADMIN', NULL);



