package de.bredex.demo.iot.temp;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import de.bredex.demo.iot.AbstractSensorPolling;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Some simple MQTT sender for MCP9805 temperature sensors on the raspi.
 */
public class TemperatureSensorPollingImpl extends AbstractSensorPolling {

    private final NumberFormat FORMATTER = new DecimalFormat("##0.##");

    public TemperatureSensorPollingImpl(String id) {
        super(id, "temperature");
    }

    @Override
    protected String createMessage() throws Exception {
        String ret;

        double temperature = readSensor();
        ret = FORMATTER.format(temperature);

        return ret;
    }

    private double readSensor() throws IOException, I2CFactory.UnsupportedBusNumberException, InterruptedException {
        double ret;

        // Create I2C bus
        I2CBus Bus = I2CFactory.getInstance(I2CBus.BUS_1);
        // Get I2C device, MCP9805 I2C address is 0x18(24)
        I2CDevice device = Bus.getDevice(0x18);
        Thread.sleep(300);

        // Select configuration register
        // Continuous conversion mode, Power-up
        byte[] config = new byte[2];
        config[0] = 0x00;
        config[1] = 0x00;
        device.write(0x01, config, 0, 2);
        // Select resolution rgister
        // Resolution = +0.0625 / C
        device.write(0x08, (byte) 0x03);
        Thread.sleep(300);

        // Read 2 bytes of data from address 0x05(05)
        // temp msb, temp lsb
        byte[] data = new byte[2];
        device.read(0x05, data, 0, 2);

        // Convert the data to 13-bits
        int temp = ((data[0] & 0x1F) * 256 + (data[1] & 0xFF));
        if (temp > 4095) {
            temp -= 8192;
        }
        ret = temp * 0.0625;

        return ret;
    }

}
