package de.sty.demo.iot;

import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractSensorPolling implements Runnable {

    public static final String TOPIC_PREFIX = "sensor/";
    public static final String DEFAULT_MQTT_BROKER = "broker.hivemq.com";

    protected static final AtomicLong msgSend = new AtomicLong();
    protected static final AtomicLong sensors = new AtomicLong();

    private final Logger LOGGER;

    /**
     * MQTT uses an UUID as id. For demo purposes we can use a shorter id.
     */
    protected String id;
    protected String topic;
    protected String serverHostname;

    /**
     * Poll sensor as long as {@link #running} is {@code true}.
     */
    protected boolean running;

    protected AbstractSensorPolling(String id, String name) {
        LOGGER = LoggerFactory.getLogger(this.getClass());
        sensors.incrementAndGet();

        this.id = id;
        this.topic = TOPIC_PREFIX + name + "/";
        this.serverHostname = DEFAULT_MQTT_BROKER;
    }

    public void run() {
        running = true;

        log("Initialize " + topic);
        Mqtt5BlockingClient client = initializeConnection();

        log("Connecting...");
        client.connect();

        sendStatusMessage(client);

        while (running) {
            String message = null;
            try {
                message = createMessage();
                log(message);
                sendMessage(client, message);
            } catch (Exception e) {
                e.printStackTrace();
            }

            running = running && sleep();
        }
    }

    protected Mqtt5BlockingClient initializeConnection() {
        return MqttClient.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(getServerHostname())
                .useMqttVersion5()
                .buildBlocking();
    }

    protected String createStatusMessage() {
        return "online";
    }

    protected void sendStatusMessage(Mqtt5BlockingClient client) {
        client.publishWith()
                .topic(topic + "/status")
                .qos(MqttQos.AT_LEAST_ONCE)
                .retain(true)
                .payload(createStatusMessage().getBytes())
                .send();
    }

    abstract protected String createMessage() throws Exception;

    protected void sendMessage(Mqtt5BlockingClient client, String message) {
        client.publishWith()
                .topic(topic)
                .qos(MqttQos.AT_LEAST_ONCE)
                .retain(true)
                .payload(message.getBytes())
                .send();
        msgSend.getAndIncrement();
    }

    protected boolean sleep() {
        boolean ret = true;
        try {
            synchronized (this) {
                this.wait(10);
            }
        } catch (InterruptedException ie) {
            ret = false;
        }
        return ret;
    }

    public void stop() {
        running = false;
        synchronized (this) {
            this.notifyAll();
        }
    }

    public String getServerHostname() {
        return serverHostname;
    }

    public void setServerHostname(String serverHostname) {
        this.serverHostname = serverHostname;
    }

    protected String getId() {
        return "[" + id + "]";
    }

    // ----------------------------------------------------------------------
    // ugly stuff

    protected void log(String s) {
        //System.out.println(/*LocalDateTime.now() + */
        //System.out.println("   SEN " + s);
    }

}
