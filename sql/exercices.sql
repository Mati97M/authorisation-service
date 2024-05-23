--CRUD START
--CREATE
INSERT INTO resources VALUES
    (19, 'test', 'TEST Microservice', NULL);

--READ
SELECT *
FROM resources;
--UPDATE
UPDATE resources
SET userSpecificId = 10
WHERE endpointPath = 'test';
--DELETE
DELETE FROM resources
WHERE endpointPath = 'test'
--CRUD END

SELECT res.id, res.endpointPath, res.serviceName, COUNT(rr.role) as roles_num
FROM resources res
JOIN resources_roles rr
ON res.id = rr.resource_id
GROUP BY res.id
HAVING COUNT(res.id) > 1
ORDER BY res.serviceName;
LIMIT 3;

SELECT res.id, res.endpointPath, res.serviceName, COUNT(rr.role) as roles_num
FROM resources res
JOIN resources_roles rr
ON res.id = rr.resource_id
GROUP BY res.id
ORDER BY res.serviceName;