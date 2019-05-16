package de.sty.demo.iot;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DemoTimeSensor implements Runnable {

    static final AtomicLong msgSend = new AtomicLong();
    private static int sensors = 0;

    private String id;

    DemoTimeSensor() {
        // int ran = (int) (Main.SENSORS * Math.random());
        // id = new DecimalFormat("0000").format(ran);
        id = "" + (char) ((int) 'A' + sensors++);
    }

    public void run() {
        log("Initialize...");
        Mqtt5BlockingClient client = MqttClient.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost("broker.hivemq.com")
                .useMqttVersion5()
                .buildBlocking();

        log("Connecting...");
        client.connect();

        try {
            client.publishWith()
                    .topic("sensor/" + id + "/status")
                    .qos(MqttQos.AT_LEAST_ONCE)
                    .retain(true)
                    .payload("online".getBytes())
                    .send();

            while (true) {
                String message = createMessage();
                log(message);

                client.publishWith()
                        .topic("test/topic/" + id)
                        .qos(MqttQos.AT_LEAST_ONCE)
                        .retain(true)
                        .payload(message.getBytes())
                        .send();
                msgSend.getAndIncrement();

                Sleeper.sleep(10);
            }
        } finally {
            client.disconnect();
        }
    }

    private String createMessage() {
        LocalDateTime now = LocalDateTime.now();
        return "[" + this.id + "] " + now.format(DateTimeFormatter.ISO_TIME);
    }

    private void log(String s) {
        //System.out.println(/*LocalDateTime.now() + */
        //System.out.println("   SEN " + s);
    }

    private String getId() {
        return "[" + id + "]";
    }
}
