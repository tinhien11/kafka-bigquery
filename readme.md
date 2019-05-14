# Kafka streaming messages to google big query API

1. Create data set and table name "emp" from bigquery UI

2. Config ENV  
Download json file from the GCP Console API Manager→Credentials page, select "Create credentials→Service account key".   
export GOOGLE_APPLICATION_CREDENTIALS=<path_to_service_account_file>

3. Create a topic to stream 
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic input-topic

4. Build
mvn clean
mvn compile

5. Run producer (terminal 1)
mvn exec:java -Dexec.mainClass="com.kafka.streams.ProducerApp"

6. Run streaming app (terminal 2)
export GOOGLE_APPLICATION_CREDENTIALS=<path_to_service_account_file>
mvn exec:java -Dexec.mainClass="com.kafka.streams.StreamsApp"
