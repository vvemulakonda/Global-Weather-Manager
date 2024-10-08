import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class GlobalWeatherManagerTest {

    static GlobalWeatherManager manager;

    @BeforeAll
    public static void setUp() {
        try {
            File file = new File("city_temperature.csv");
            manager = new GlobalWeatherManager(file);
        } catch (FileNotFoundException e) {
            fail("file not read; all tests are invalid");
        }
    }

    @Test
    public void testGetReadingCount() {
        assertEquals(2885064, manager.getReadingCount());
    }

    @Test
    public void testGetReading() {
        WeatherReading reading = manager.getReading(1);
        assertNotNull(reading);
        assertEquals("Algeria", reading.country());
        assertEquals("", reading.state());
        assertEquals("Algiers", reading.city());
    }

    @Test
    public void testGetReadingOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> manager.getReading(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> manager.getReading(2885067));
    }

    @Test
    public void testGetReadings() {
        WeatherReading[] readings = manager.getReadings(0, 2);
        assertEquals(2, readings.length);
        assertEquals("Algeria", readings[0].country());
        assertEquals("Algeria", readings[1].country());
    }

    @Test
    public void testGetReadingsOutOfBounds() {
        assertThrows(IndexOutOfBoundsException.class, () -> manager.getReadings(3, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> manager.getReadings(-1, 1));
    }

    @Test
    public void testGetCityListStats() {
        CityListStats stats = manager.getCityListStats("Algeria", "", "Algiers");
        assertNotNull(stats);
        assertEquals(0, stats.startingIndex());
        assertEquals(9265, stats.count());
        assertEquals(9265, stats.years().length);
    }

    @Test
    public void testGetCityListStatsInvalid() {
        assertThrows(IllegalArgumentException.class, () -> manager.getCityListStats(null,
                " ", "Algiers"));
        assertThrows(IllegalArgumentException.class, () -> manager.getCityListStats("Algeria",
                null, "Algiers"));
        assertThrows(IllegalArgumentException.class, () -> manager.getCityListStats("Algeria",
                " ", null));
    }

    @Test
    public void testGetTemperatureLinearRegressionSlope() {
        WeatherReading[] readings = new WeatherReading[]{
                new WeatherReading("North America", "US", "Arizona", "Phoenix", 1,
                        1, 2000, 60.0),
                new WeatherReading("North America", "US", "Arizona", "Phoenix", 1,
                        1, 2001, 62.0),
                new WeatherReading("North America", "US", "Arizona", "Phoenix", 1,
                        1, 2002, 64.0)
        };
        double slope = manager.getTemperatureLinearRegressionSlope(readings);
        assertEquals(2.0, slope, 0.001);
    }

    @Test
    public void testGetTemperatureLinearRegressionSlopeInvalid() {
        WeatherReading[] readings = new WeatherReading[]{
                new WeatherReading("NA", "US", "Arizona", "Phoenix", 1, 1, 2000, 60.0)
        };
        assertThrows(IllegalArgumentException.class, () ->
                manager.getTemperatureLinearRegressionSlope(readings));
    }
}
