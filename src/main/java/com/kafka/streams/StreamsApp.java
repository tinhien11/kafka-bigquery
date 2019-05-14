package com.kafka.streams;

import com.google.api.services.bigquery.model.TableDataInsertAllRequest;
import com.google.cloud.bigquery.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import java.util.*;
import org.json.*;
import java.util.Map.Entry;


public class StreamsApp {
    private  static final BigQuery bigquery = BigQueryOptions.getDefaultInstance().getService();

    private static void deliverDataToBigQuery(Integer key, String value) {
        System.out.println(key);
        System.out.println(value);

        TableId tableId = TableId.of("emp", "emp");

        JSONObject obj = new JSONObject(value);

        // Define rows to insert
        Map<String, Object> firstRow = new HashMap<>();
        Map<String, Object> secondRow = new HashMap<>();
        firstRow.put("first_name", obj.get("first_name"));
        firstRow.put("last_name", obj.get("last_name"));
        firstRow.put("enter_time", obj.get("timestamp"));

        // Create an insert request
        InsertAllRequest insertRequest = InsertAllRequest.newBuilder(tableId)
                .addRow(firstRow)
                .build();
        // Insert rows
        InsertAllResponse insertResponse = bigquery.insertAll(insertRequest);

        System.out.println(insertResponse);

        // Check if errors occurred
        if (insertResponse.hasErrors()) {
            System.out.println("Errors occurred while inserting rows");
        }
    }

    public static void main(String[] args) {
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-starter-app");
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        KStreamBuilder builder = new KStreamBuilder();
        builder.stream(Serdes.Integer(), Serdes.String(), "input-topic")
                .foreach((key, value) -> deliverDataToBigQuery(key, value));

        KafkaStreams streams = new KafkaStreams(builder, config);
        streams.cleanUp(); // only do this in dev - not in prod
        streams.start();

        // print the topology
        System.out.println(streams.toString());

        // shutdown hook to correctly close the streams application
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }

}
