# RDBMS tasks
## Transactions
demoNoTransactions updates only resource table, but should also resources_roles. There is inconsistent state.
  Thanks to Transactions, in case of some failure demoWithTransactions will not insert data at all.
## Isolation
demoWithBadIsolation may cause data inconsistency in e.g. generated reports, due to improper isolation level. demoWithGoodIsolation has TRANSACTION_SERIALIZABLE isolation level which prevents those risks.
## IndexDemo
  Generating 2 mln rows in the resources table
```sql
DO $$
BEGIN
  FOR i in 1..2000000 loop
    INSERT INTO resources (endpointPath, serviceName, userSpecificId)
    VALUES ('/api/authorisation/resources', CONCAT('Authorisation Microservice', i), random()*100);
  END LOOP;
END;
$$;
```
Query without index:
```sql
EXPLAIN ANALYZE SELECT userSpecificId FROM resources WHERE userSpecificId % 2 = 0;
```
Results -> full scan on the whole table:
```
Gather  (cost=1000.00..41157.11 rows=10000 width=8) (actual time=3.665..12320.265 rows=1000779 loops=1)
  Workers Planned: 2
  Workers Launched: 2
  ->  Parallel Seq Scan on resources  (cost=0.00..39157.11 rows=4167 width=8) (actual time=0.054..4118.240 rows=333593 loops=3)
        Filter: ((userspecificid % '2'::bigint) = 0)
        Rows Removed by Filter: 333080
Planning Time: 0.358 ms
Execution Time: 24246.440 ms

```
---
Query with index:
* Creating index on userSpecificId column:
```sql
CREATE INDEX usi ON resources(userSpecificId);
```
```sql
EXPLAIN ANALYZE SELECT userSpecificId FROM resources WHERE userSpecificId < 30;
```

Results when index exists
```
Index Only Scan using usi on resources  (cost=0.43..12277.57 rows=587265 width=8) (actual time=0.530..6636.635 rows=590508 loops=1)
  Index Cond: (userspecificid < 30)
  Heap Fetches: 0
Planning Time: 0.320 ms
Execution Time: 13075.578 ms
```
* Optimization on searching with indexes, using `WHERE` clause with `AND`:
    When a index returns few rows and b index returns a lot of them, then DB can only use a index, and then filter the results with the second condition. Without using index b at all.

## Compound a.k.a Composite index
It is an index, which is created upon more than one column. The order of the columns matters.
```sql
CREATE INDEX ON test (a,b);
```
* bitmap index scan will be used when:
If `WHERE` clause has `condition with a`, or `condition with a AND condition with b`
IF `WHERE` clause has `condition with a OR condition with b` <b>and</b> providing index for b

* parallel seq scan will be used when:
If `WHERE` clause has only `condition with b`
IF `WHERE` clause has `condition with a OR condition with b`

### examples
```sql
--created earlier 2 mln rows
CREATE INDEX ON resources (serviceName,userSpecificId);
EXPLAIN ANALYZE SELECT serviceName FROM resources WHERE serviceName = 'Authorisation Microservice' AND userSpecificId < 6; 
```
Output:
```
Index Only Scan using resources_servicename_userspecificid_idx on resources  (cost=0.55..4.57 rows=1 width=33) (actual time=0.086..0.182 rows=0 loops=1)
  Index Cond: ((servicename = 'Authorisation Microservice'::text) AND (userspecificid < 6))
  Heap Fetches: 0
Planning Time: 0.405 ms
Execution Time: 0.254 ms
```

```sql
EXPLAIN ANALYZE SELECT serviceName FROM resources WHERE serviceName = 'Authorisation Microservice' OR userSpecificId < 6; 
```
Output:
```
Gather  (cost=1000.00..50197.51 rows=100404 width=33) (actual time=0.321..1346.750 rows=109565 loops=1)
  Workers Planned: 2
  Workers Launched: 2
  ->  Parallel Seq Scan on resources  (cost=0.00..39157.11 rows=41835 width=33) (actual time=0.040..498.865 rows=36522 loops=3)
        Filter: ((servicename = 'Authorisation Microservice'::text) OR (userspecificid < 6))
        Rows Removed by Filter: 630151
Planning Time: 0.085 ms
Execution Time: 2572.350 ms
```