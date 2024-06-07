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
#### Query: 
```sql
EXPLAIN ANALYZE SELECT userSpecificId FROM resources WHERE userSpecificId < 30;
```

### without index:
Results -> full scan on the whole table:
```
Seq Scan on resources  (cost=0.00..52647.57 rows=693082 width=8) (actual time=0.037..7178.865 rows=591159 loops=1)
  Filter: (userspecificid < 30)
  Rows Removed by Filter: 1408859
Planning Time: 0.080 ms
Execution Time: 13805.979 ms

```
---
### with index:
Creating index on userSpecificId column:
```sql
CREATE INDEX usi ON resources(userSpecificId);
```

Results when index exists
```
Index Only Scan using usi on resources  (cost=0.43..12418.99 rows=593975 width=8) (actual time=0.096..6747.253 rows=590767 loops=1)
  Index Cond: (userspecificid < 30)
  Heap Fetches: 0
Planning Time: 0.255 ms
Execution Time: 13238.777 ms
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