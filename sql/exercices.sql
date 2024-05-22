--CRUD START
--CREATE
INSERT INTO resources (endpointPath, serviceName, roles) VALUES
('test', 'Test Microservice', 'ADMIN');
--READ
SELECT *
FROM resources;
--UPDATE
UPDATE resources
SET roles = 'ADMIN USER_SPECIFIC'
WHERE endpointPath = '/api/discount/associate/user';
--DELETE
DELETE FROM resources
WHERE endpointPath = 'test'
--CRUD END

SELECT *
FROM resources
WHERE length(endpointPath) > length('ADMIN')
ORDER BY length(endpointPath) DESC
LIMIT 2;