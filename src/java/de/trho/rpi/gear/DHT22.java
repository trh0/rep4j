package de.trho.rpi.gear;

import static com.pi4j.wiringpi.Gpio.HIGH;
import static com.pi4j.wiringpi.Gpio.INPUT;
import static com.pi4j.wiringpi.Gpio.LOW;
import static com.pi4j.wiringpi.Gpio.OUTPUT;
import static com.pi4j.wiringpi.Gpio.delay;
import static com.pi4j.wiringpi.Gpio.delayMicroseconds;
import static com.pi4j.wiringpi.Gpio.digitalRead;
import static com.pi4j.wiringpi.Gpio.digitalWrite;
import static com.pi4j.wiringpi.Gpio.pinMode;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Ported Java version of lol_dht22 c script
 * 
 * @author lolware, trh0
 * @see https://github.com/technion/lol_dht22
 * @see https://github.com/technion/lol_dht22/blob/master/dht22.c
 */
public class DHT22 {

  public interface Reading {
    float getTemperature();

    float getHumidity();

    long getTime();

    static Reading newReading(float temp, float humid) {
      return new Reading() {

        private long  time = System.currentTimeMillis();
        private float h    = humid, t = temp;

        @Override
        public float getTemperature() {
          return t;
        }

        @Override
        public float getHumidity() {
          return h;
        }

        @Override
        public long getTime() {
          return time;
        }

        @Override
        public String toString() {
          return String.format("{\"time\":\"%s\",\"temp\":%s,\"humid\":%s}\n",
              new java.util.Date(getTime()), getTemperature(), getHumidity());
        }
      };
    }
  }

  private static final int    MAXBUFF    = 1000;
  private static final int    MAXTIMINGS = 85;
  private int                 DHTPIN;
  int[]                       dht22_dat  = {0, 0, 0, 0, 0};
  private final List<Reading> readings;
  private Reading             last       = Reading.newReading(-9999, -9999);

  public DHT22(int datapin) {
    this.DHTPIN = datapin;
    this.readings = new java.util.ArrayList<DHT22.Reading>();
  }

  private void addReading(float t, float h) {
    if (this.readings.size() == MAXBUFF) {
      readings.remove(0);
    }
    final Reading r = Reading.newReading(t, h);
    if (!readings.contains(r)) {
      readings.add(r);
    }
  }

  public Collection<Reading> getReadings() {
    return Collections.unmodifiableCollection(this.readings);
  }

  public int readData() {
    int laststate = HIGH;
    int counter = 0;
    int j = 0, i;
    @SuppressWarnings("unused")
    float f; /* fahrenheit */

    dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;
    pinMode(DHTPIN, OUTPUT);
    /* set init state for data pin to high */
    digitalWrite(DHTPIN, HIGH);
    /* wait 10ms to ensure the dht22 can notice this */
    delay(10);
    /* pull pin down for 18 milliseconds */
    digitalWrite(DHTPIN, LOW);
    delay(18);
    /* then pull it up for 40 microseconds */
    digitalWrite(DHTPIN, HIGH);
    delayMicroseconds(40);
    /* prepare to read the pin */
    pinMode(DHTPIN, INPUT);

    /* detect change and read data */
    for (i = 0; i < MAXTIMINGS; i++) {
      counter = 0;
      while (digitalRead(DHTPIN) == laststate) {
        counter++;
        delayMicroseconds(2);
        if (counter == 255)
          break;
      }
      laststate = digitalRead(DHTPIN);

      if (counter == 255)
        break;

      /* ignore first 3 transitions */
      if ((i >= 4) && (i % 2 == 0)) {
        /* shove each bit into the storage bytes */
        dht22_dat[j / 8] <<= 1;
        if (counter > 16)
          dht22_dat[j / 8] |= 1;
        j++;
      }
    }

    /*
     * check we read 40 bits (8bit x 5 ) + verify checksum in the last byte print it out if data is
     * good
     */
    if ((j >= 40)
        && (dht22_dat[4] == ((dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3]) & 0xFF))) {
      float t, h;
      h = (float) dht22_dat[0] * 256 + dht22_dat[1];
      h /= 10;
      t = (float) (dht22_dat[2] & 0x7F) * 256 + dht22_dat[3];
      t /= 10.0;
      if ((dht22_dat[2] & 0x80) != 0)
        t *= -1;
      addReading(t, h);
      return 1;
    } else {
      return 0;
    }
  }

  public Reading getLastReading() {
    if (readings.isEmpty())
      return null;
    Reading reading = this.readings.get(readings.size() - 1);
    if (reading.getTime() > last.getTime()) {
      last = reading;
      return reading;
    }
    return null;
  }

}
