# Kafka streaming messages to google big query API

1. Create data set and table name "emp" from bigquery UI

2. Create a streaming topic
```bash
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic input-topic
```

3. Build
```bash
mvn clean
mvn compile
```

4. Run producer (terminal 1):  
Producer send a simple message like:
{"first_name": "joe", "last_name": "f", "timestamp": 1557804338} . 
Message no following Avro schema
```bash
mvn exec:java -Dexec.mainClass="com.kafka.streams.ProducerApp"
```


6. Run streaming app (terminal 2):   
Kafka streams message from "input-topic" and insert to table "emp" by using google cloud bigquery client   

Download json file from the GCP Console API Manager→Credentials page, select "Create credentials→Service account key".   
```bash
export GOOGLE_APPLICATION_CREDENTIALS=<path_to_service_account_file>
mvn exec:java -Dexec.mainClass="com.kafka.streams.StreamsApp"
```

