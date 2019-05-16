package de.bredex.demo.iot.mqtt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

public class AbstractSensorPush {

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

    protected AbstractSensorPush(String id, String name) {
        LOGGER = LoggerFactory.getLogger(this.getClass());
        sensors.incrementAndGet();

        this.id = id;
        this.topic = MqttConst.TOPIC_PREFIX + name + "/";
        this.serverHostname = MqttConst.DEFAULT_MQTT_BROKER;
    }


}
