package de.sty.demo.iot;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DemoTimeConsumer implements Runnable {

    static final AtomicLong msgReceived = new AtomicLong();

    private static void log(Object s) {
        System.out.println(/*LocalDateTime.now() + " " +*/ "CON    " + s);
    }

    @Override
    public void run() {
        log("Initialize...");
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("broker.hivemq.com")
                .buildBlocking();
        log("Connecting...");
        client.connect();

        try (final Mqtt5BlockingClient.Mqtt5Publishes publishes = client.publishes(MqttGlobalPublishFilter.ALL)) {
            client.subscribeWith()
                    .topicFilter("test/topic/+")
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .send();

            while (true) {
                publishes.receive(1, TimeUnit.SECONDS).ifPresent(this::received);
            }

        } catch (InterruptedException ie) {
            throw new RuntimeException(ie);
        } finally {
            client.disconnect();
        }
    }

    private void received(Mqtt5Publish mqtt5Publish) {
        String msg = new String(mqtt5Publish.getPayloadAsBytes());
        msgReceived.getAndIncrement();
        //log(mqtt5Publish + ": " + msg);
        //log(msg);
    }

}
