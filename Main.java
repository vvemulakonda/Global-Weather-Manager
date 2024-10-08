import java.io.File;
import java.io.FileNotFoundException;

/**
 * @author Vivek Vemulakonda
 * @version 1.0
 * This is meant to show some of the things I thought were cool about this project
 */

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        File tempInfo = new File("city_temperature.csv");
        GlobalWeatherManager temperature = new GlobalWeatherManager(tempInfo);
        System.out.println("Total amount of data points are " + temperature.getReadingCount());
        System.out.println("City Stats for Algiers are " +
                temperature.getCityListStats("Algeria", "", "Algiers"));
        WeatherReading[] readings = new WeatherReading[3];
        readings[0]=new WeatherReading("North America","US","South Dakota","Rapid City",11,11,
                2005,57.6);
        readings[1]=new WeatherReading("North America","US","South Carolina","Charleston",7,12,
                2004,78.9);
        readings[2]=new WeatherReading("North America","US","Pennsylvania","Wilkes Barre",4,13,
                2003,45.1);
        System.out.println("Lin Regression Slope is "
                + temperature.getTemperatureLinearRegressionSlope(readings));

    }
}