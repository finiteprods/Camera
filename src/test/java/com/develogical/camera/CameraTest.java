package com.develogical.camera;

import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.syntax.ReceiverClause;
import org.junit.Rule;
import org.junit.Test;

public class CameraTest {

    private static final byte[] IMAGE = new byte[4];

    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    // don't have a Sensor and MemoryCard impl, mock them
    Sensor sensor = context.mock(Sensor.class);
    MemoryCard memCard = context.mock(MemoryCard.class);

    Camera camera = new Camera(sensor, memCard);

    @Test
    public void switchingTheCameraOnPowersUpTheSensor() {

        // set expectation: sensor.powerUp() should be invoked once
        context.checking(new Expectations() {
            {
                exactly(1).of(sensor).powerUp();
            }
        });

         camera.powerOn();
    }

    @Test
    public void switchingTheCameraOffPowersDownTheSensor() {

        switchCameraOn();

        context.checking(new Expectations() {
            {
                exactly(1).of(sensor).powerDown();
            }
        });

        camera.powerOff();
    }

    @Test
    public void pressingShutterWithPowerOnCopiesDataFromSensorToMemCard() {

        switchCameraOn();

        // IMAGE represents the byte[] returned by sensor.readData()
        // doesn't really matter what it actually is, only that we have a name for it
        // so that we can say what's called next is memCard.write(IMAGE)
        context.checking(new Expectations() {
            {
                exactly(1).of(sensor).readData(); will(returnValue(IMAGE));
                exactly(1).of(memCard).write(IMAGE);
            }
        });

        camera.pressShutter();
    }

    @Test
    public void pressingShutterWithPowerOffDoesNothing() {

        // you could just have no expectations, but this is to be explicit
        context.checking(new Expectations() {
            {
                never(sensor);
                never(memCard);
            }
        });

        camera.pressShutter();
    }

    @Test
    public void doesNotPowerDownSensorUntilWritingDone() {

        switchCameraOn();

        context.checking(new Expectations() {
            {
                exactly(1).of(sensor).readData(); will(returnValue(IMAGE));
                exactly(1).of(memCard).write(IMAGE);
            }
        });
        camera.pressShutter();

        context.checking(new Expectations() {
            {
                never(sensor);
            }
        });
        camera.powerOff(); // but sensor still in mid-write

        context.checking(new Expectations() {
            {
                exactly(1).of(sensor).powerDown();
            }
        });
        camera.writeComplete();
    }

    private void switchCameraOn() {
        context.checking(new Expectations() {
            {
                ignoring(sensor).powerUp();
            }
        });
        camera.powerOn();
    }
}
