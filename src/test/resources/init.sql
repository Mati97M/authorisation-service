INSERT INTO resources (endpoint_path, service_name) VALUES
    ('/api/authorisation/resources', 'Authorisation Microservice'),
    ('/api/blog/post', 'Blog Microservice'),
    ('/api/blog/tag', 'Blog Microservice'),
    ('/api/discount', 'Price Microservice'),
    ('/api/discount/associate/product', 'Price Microservice'),
    ('/api/discount/associate/user', 'Price Microservice'),
    ('/api/order', 'Order Service'),
    ('/api/products', 'Product Microservice'),
    ('/api/products/variant/', 'Product Microservice'),
    ('/api/products/media', 'Product Microservice'),
    ('/api/notification/confirmEmail', 'Notification Microservice'),
    ('/api/notification/deletedAccount', 'Notification Microservice'),
    ('/api/notification/resetPassword', 'Notification Microservice'),
    ('/api/notification/parcelReadyToPickup', 'Notification Microservice'),
    ('/api/notification/productsAvailable', 'Notification Microservice'),
    ('/api/notification/productsAmountIsLow', 'Notification Microservice'),
    ('/api/notification/newBlogPosted', 'Notification Microservice'),
    ('/api/notification/unhandledException', 'Notification Microservice');

INSERT INTO resources_roles (resource_id, roles) VALUES
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