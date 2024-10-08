/**
 * Represents weather readings for a specific data point
 * Implements comparable interface to naturally order all variables
 *
 * @author Vivek Vemulakonda
 * @version 1.0
 * @param region
 * @param country
 * @param state
 * @param city
 * @param month
 * @param day
 * @param year
 * @param avgTemp
 */
public record WeatherReading(String region, String country, String state, String city,
                             int month, int day, int year,double avgTemp) implements
        Comparable<WeatherReading>{

    /**
     *Compares 2 Weather Readings for natural order.
     *
     * @param other the object to be compared.
     * @return integer based of if this WeatherReading is greater,
     * less or equal to other Weather Reading
     */
    @Override
    public int compareTo(WeatherReading other){
        int result = this.country.compareTo(other.country);
        if(result==0) {
            result = this.state.compareTo(other.state);
            if (result == 0) {
                result = this.city.compareTo(other.city);
                if (result == 0) {
                    result = Integer.compare(this.year, other.year);
                    if (result == 0) {
                        result = Integer.compare(this.month, other.month);
                        if (result == 0) {
                            result = Integer.compare(this.day, other.day);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * Shows whether an object is, "equal" to the other
     *
     * @param other   the reference object with which to compare.
     * @return true if they are equal false if not
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }if (other == null) {
            return false;
        }if (!(other instanceof WeatherReading that)) {
            return false;
        }
        return this.country.equals(that.country) &&
                this.state.equals(that.state) &&
                this.city.equals(that.city) &&
                this.year == that.year &&
                this.month == that.month &&
                this.day == that.day;
    }

    /**
     * Compares only country, state and city
     *
     * @param other Weather reading compared to
     * @return integer based of if this WeatherReading is greater,
     *       less or equal to other Weather Reading
     */
    public int compareCountryStateCity (WeatherReading other){
        int result=this.country.compareTo(other.country);
        if(result==0) {
            result = this.state.compareTo(other.state);
            if (result == 0) {
                result = this.city.compareTo(other.city);
            }
        }
        return result;
    }
}
