package de.bredex.demo.iot.snake;

import com.hivemq.client.mqtt.MqttGlobalPublishFilter;
import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt3.Mqtt3BlockingClient;
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client;
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish;
import de.bredex.demo.iot.mqtt.MqttConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Control {@link SnakeController} via mqtt messages.
 * <p>
 * The {@link Snake}s are controlled via topic {@link #TOPIC_SNAKE} with a number appended.
 * The  {@link Direction}'s value is the {@link Mqtt3Publish#getPayloadAsBytes() payload}.
 * To {@link SnakeController#setRestart(boolean) start a new game}, use {@code "RESET"} as
 * {@link Mqtt3Publish#getPayloadAsBytes() payload}.
 * </p>
 */
public class SnakeMqttController implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(SnakeMqttController.class);

    public static final String TOPIC_SNAKE = "snake";
    public static final String PAYLOAD_RESET = "RESET";

    SnakeFX application;
    private SnakeController snakeController;

    public SnakeMqttController(SnakeFX application, SnakeController snakeController) {
        this.application = application;
        this.snakeController = snakeController;
    }

    @Override
    public void run() {
        LOGGER.info("Initialize...");
        Mqtt3BlockingClient client = Mqtt3Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(MqttConst.DEFAULT_MQTT_BROKER)
                .buildBlocking();
        LOGGER.info("Connecting...");
        client.connect();

        try (final Mqtt3BlockingClient.Mqtt3Publishes publishes =
                     client.publishes(MqttGlobalPublishFilter.ALL)) {
            client.subscribeWith()
                    .topicFilter(TOPIC_SNAKE + "/+")
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

    private void received(Mqtt3Publish mqtt3Publish) {
        LOGGER.info("received: '{}', '{}'", mqtt3Publish, new String(mqtt3Publish.getPayloadAsBytes()));

        List<String> levels = mqtt3Publish.getTopic().getLevels();
        try {
            Snake snake;
            String snakeNoText = levels.get(1);
            int snakeNo = Integer.parseInt(snakeNoText);
            switch (snakeNo) {
                case 0:
                    snake = snakeController.snake0;
                    break;
                case 1:
                    snake = snakeController.snake1;
                    break;
                default:
                    throw new RuntimeException("Unknown snake: " + snakeNoText);
            }

            String payload = new String(mqtt3Publish.getPayloadAsBytes());
            if (PAYLOAD_RESET.equalsIgnoreCase(payload)) {
                application.reset();
            } else {
                Direction direction = Direction.valueOf(payload);

                // Whoohooo! Game on!
                snake.turn(direction);
            }
        } catch (Exception e) {
            LOGGER.warn("Caught Exception from Snake MQTT Listener: " + e, e);
        }

    }

    public void setSnakeController(SnakeController snakeController) {
        this.snakeController = snakeController;
    }
}
