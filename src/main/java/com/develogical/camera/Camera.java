package com.develogical.camera;

public class Camera implements WriteListener {

    private Sensor sensor;
    private MemoryCard memCard;
    private boolean powerOn = false;
    private boolean writing = false;

    public Camera(Sensor sensor, MemoryCard memCard) {
        this.sensor = sensor;
        this.memCard = memCard;
    }

    public void pressShutter() {
        // do nothing if power off
        // else write data from sensor to memory card
        if (powerOn) {
            memCard.write(sensor.readData());
            writing = true;
        }
    }

    public void powerOn() {
        // power up sensor
        powerOn = true;
        sensor.powerUp();
    }

    public void powerOff() {
        // power down sensor
        // unless mid-writing data - wait for finish first
        if (!writing) {
            sensor.powerDown();
        }
        powerOn = false;
    }

    @Override
    public void writeComplete() {
        writing = false;
        if (!powerOn)
            powerOff();
    }
}

