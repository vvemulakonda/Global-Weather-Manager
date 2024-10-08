import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 *Looks at weather data and provides many operations and methods for use
 * Implements GlobalWeatherManagerInterface for methods and Iterable for iteration
 * Reads from a file and gives info based off that
 *
 * @author Vivek Vemulakonda
 * @version 1.0
 */
public class GlobalWeatherManager implements GlobalWeatherManagerInterface,
        Iterable<WeatherReading> {

    /**
     * ArrayList to store WeatherReadings for use
     */
    private final ArrayList<WeatherReading> weatherArray;


    /**
     * Constructs a GlobalWeatherManager by reading given file
     * @param weatherInfo file Info
     * @throws FileNotFoundException is thrown if file is not valid or found
     */
    public GlobalWeatherManager(File weatherInfo) throws FileNotFoundException {
        weatherArray = new ArrayList<>();
        Scanner scanner = new Scanner(weatherInfo);
        scanner.nextLine();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int starting = 0;
            int ending = line.indexOf(",");

            String[] info = new String[8];
            int index = 0;
            while (ending != -1) {
                info[index++] = line.substring(starting, ending);
                starting = ending + 1;
                ending = line.indexOf(",", starting);
            }
            info[index] = line.substring(starting);

            String region = info[0];
            String country = info[1];
            String state = info[2];
            String city = info[3];
            int month = Integer.parseInt(info[4]);
            int day = Integer.parseInt(info[5]);
            int year = Integer.parseInt(info[6]);
            double avgTemp = Double.parseDouble(info[7]);

            weatherArray.add(new WeatherReading(region, country, state, city, month, day, year,
                    avgTemp));
        }


    }

    /**
     * Retrieves a count of readings
     * @return      count of readings
     */
    @Override
    public int getReadingCount() {
        return weatherArray.size();
    }

    /**
     * Retrieves the weather reading at the specified index.
     *
     * @param index     the index for the desired reading; must be a valid element index.
     * @return the reading at the specified index
     * @throws IndexOutOfBoundsException if Index not in valid range
     */
    @Override
    public WeatherReading getReading(int index) {
        if (index < 0 || index >= weatherArray.size()) {
            throw new IndexOutOfBoundsException("Index not in valid range");
        }
        return weatherArray.get(index);
    }

    /**
     * Retrieves a set of weather readings.
     *
     * @param index     the index of the first reading; must be a valid index.
     * @param count     the count of readings to potentially include.  Must be at least 1.
     *                  Must imply a valid range;
     *                  index + count must be less than the total reading count.
     * @return an array of readings.
     * @throws IndexOutOfBoundsException if Index or count not in valid range
     */
    @Override
    public WeatherReading[] getReadings(int index, int count) {
        if (index < 0 || count < 1 || count + index > weatherArray.size()) {
            throw new IndexOutOfBoundsException("Index or count not in valid range");
        }
        WeatherReading[] finalReadings = new WeatherReading[count];
        for (int i = 0; i < count; i++) {
            finalReadings[i] = weatherArray.get(index + i);
        }
        return finalReadings;
    }

    /**
     *Retrieves a set of WeatherReadings
     *
     * @param index     the index of the first reading.
     * @param count     the count of readings to check for potential inclusion.
     *                  Must be at least 1.
     *                  Must imply a valid range; index +
     *                  count must be less than the total reading count.
     * @param month     the month to filter; must be a valid month (1 to 12).
     * @param day       the day to filter; must be a valid day (1 to 31).
     * @return an array of readings matching the specified criteria.
     * Length will usually be smaller than the count specified as a parameter,
     * as each year will only have one matching day.
     * @throws IndexOutOfBoundsException if Index or count not in valid range
     * @throws IllegalArgumentException if month or day is not valid
     */
    @Override
    public WeatherReading[] getReadings(int index, int count, int month, int day) {
        if (index < 0 || count < 1 || index + count > weatherArray.size()) {
            throw new IndexOutOfBoundsException("Index or count not in valid range");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Month is not valid");
        }
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("Day is not valid");
        }

        int validDataCount = 0;
        for (int i = index; i < index + count; i++) {
            WeatherReading weatherReading = weatherArray.get(i);
            if (weatherReading.month() == month && weatherReading.day() == day) {
                validDataCount++;
            }
        }

        WeatherReading[] finalData = new WeatherReading[validDataCount];
        int currentIndex = 0;
        for (int i = index; i < index + count; i++) {
            WeatherReading weatherReading = weatherArray.get(i);
            if (weatherReading.month() == month && weatherReading.day() == day) {
                finalData[currentIndex++] = weatherReading;
            }
        }

        return finalData;
    }

    /**
     *Retrieves key list statistics for the specified country/state/city.
     *
     * @param country   the country of interest; must not be null or blank.
     * @param state     the state of interest; must not be null.
     * @param city      the city of interest; must not be null or blank.
     * @return the list stats for the specified city, or null if not found.
     * @throws IllegalArgumentException if Country or city is blank
     * and if any of the three are null
     */
    @Override
    public CityListStats getCityListStats(String country, String state, String city) {
        if (country==(null) || country.isEmpty()) {
            throw new IllegalArgumentException("Country can't be null or blank.");
        }
        if (state == null) {
            throw new IllegalArgumentException("State must not be null.");
        }
        if (city == null || city.isEmpty()) {
            throw new IllegalArgumentException("City can't be null or blank.");
        }
        WeatherReading target = new WeatherReading(null, country, state, city,
                0, 0, 0, 0.0);
        int index = Collections.binarySearch(weatherArray, target,WeatherReading::compareCountryStateCity);

        if (index < 0) {
            Collections.sort(weatherArray);
            index = Collections.binarySearch(weatherArray, target, WeatherReading::compareCountryStateCity);
            if (index < 0) {
                return null;
            }
        }

        while (index > 0 && weatherArray.get(index - 1).compareCountryStateCity(target) == 0) {
            index--;
        }

        ArrayList<Integer> yearSet = new ArrayList<>();
        int count = 0;
        int startingIndex = index;

        while (index < weatherArray.size() &&
                weatherArray.get(index).compareCountryStateCity(target) == 0) {
            WeatherReading reading = weatherArray.get(index);
            yearSet.add(reading.year());
            count++;
            index++;
        }

        int[] years = new int[yearSet.size()];
        int i = 0;
        for (Integer year : yearSet) {
            years[i++] = year;
        }
        Arrays.sort(years);

        return new CityListStats(startingIndex, count, years);
    }

    /**
     * Retrieves an iterator over all weather readings.
     * @return      strongly typed iterator for.
     */
    @Override
    public Iterator<WeatherReading> iterator() {
        return weatherArray.iterator();
    }

    /**
     *Does a linear regression analysis on the data, using x = year and y = temperature.
     *Calculates the slope of a best-fit line using the Least Squares method.
     *
     * @param readings      array of readings to analyze.
     *  Should typically be readings for a single day over
     * a number of years; larger data sets will likely yield better results.  Ignores
     * temperature data of -99.0, a default value indicating no temperature data was present.
     * Must not be null and must contain at least two readings.
     * @return  slope of best-fit line; positive slope indicates increasing temperatures.
     * @throws IllegalArgumentException if readings are null or has less than 2 readings
     */
    @Override
    public double getTemperatureLinearRegressionSlope(WeatherReading[] readings) {
        if (readings == null || readings.length < 2) {
            throw new IllegalArgumentException("Readings can't be null and must contain at " +
                    "least two readings.");
        }
        ArrayList<Integer> xValuesList = new ArrayList<>();
        ArrayList<Double> yValuesList = new ArrayList<>();

        for (WeatherReading reading : readings) {
            if (reading.avgTemp() != -99.0) {
                xValuesList.add(reading.year());
                yValuesList.add(reading.avgTemp());
            }
        }

        Integer[] xValues = xValuesList.toArray(new Integer[0]);
        Double[] yValues = yValuesList.toArray(new Double[0]);

        return calcLinearRegressionSlope(xValues, yValues);
    }

    /**
     * Calculates the slope of the best-fit line calculated using the Least Squares method.
     *
     * @param x     an array of x values; must not be null and must contain at least two elements.
     * @param y     an array of y values; must be the same length as the x array
     *              and must not be null.
     * @return the slope of the best-fit line
     * @throws IllegalArgumentException if any values are null,
     * if both arrays are different lengths, or if array length is less than 2
     */
    @Override
    public double calcLinearRegressionSlope(Integer[] x, Double[] y) {
        if (x == null || x.length < 2) {
            throw new IllegalArgumentException("x values can't be null and must contain " +
                    "at least two elements.");
        }
        if (y == null || y.length != x.length) {
            throw new IllegalArgumentException("y values can't be null and must be the " +
                    "same length as the x array.");
        }
        int length = x.length;
        double sumX = 0.0;
        double sumY = 0.0;
        double sumX2 = 0.0;
        double sumXY = 0.0;

        for (int i = 0; i < length; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += (x[i] * y[i]);
            sumX2 += (x[i] * x[i]);
        }

        return (length * sumXY - sumX * sumY) / (length * sumX2 - sumX * sumX);
    }
}
