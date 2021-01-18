# TODO

- [ ] Better Documentations...
- [ ] Upgrading to Apache Camel-Component to 2.24
- [ ] Avro w. Confluent.cloud Schema Registry
- [ ] Kafka Producer Intermitent Errors
- [ ] Kafka Producer Random Slow Response
- [ ] Duplication/Idempotency
- [ ] 
---

## Schema Registry
Benefit:
1. describing contract that defines what a message should look like
2. validating whether emitted events comply with an expected schema
3. validating whether schemas are backward compatible as version mgmt
---
## Kafka Producer Intermitent Error  14/ 24hr period

Below error from failed schedule runs that happens randomly:
>Processing Time: 30 sec 220 ms
Error Details
org.apache.kafka.common.errors.NetworkException: The server disconnected before a response was received.

Another example w. this process over 1min:

>Processing Time: 1 min 734 ms
Error Details
org.apache.kafka.common.errors.NetworkException: The server disconnected before a response was received.

3rd exp w. timeout explicitly:
>Processing Time: 1 min 83 ms
Error Details
org.apache.kafka.common.errors.TimeoutException: Failed to update metadata after 60000 ms.

---
## Kafka Producer Random Slow Response
This maybe related to do with above errors when Confluent.cloud broker is not reachable:

![iFlow random performace time](./images/iFlow%20Random%20complete%20time.png){:height="50%" width="50%"}

---
## Duplication/Idempotency
ID field identifies the event which producers ensure is unique for a given source.  Surrogate key of combinate of key column and docuemnt posting/update field (BUDAT/DABRZ) to be used.



