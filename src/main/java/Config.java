import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class Config extends AbstractConfig {
    private static final String FIXED_CONFIG_RESOURCE = "fixed-data-generation.config";

    private static final String RANDOM_CONFIG_RESOURCE = "random-data-generation.config";

    private Properties properties;

    public Config(boolean useFixedConfig) {
        if (useFixedConfig) {
            this.properties = Util.loadProperties(FIXED_CONFIG_RESOURCE);
        } else {
            Properties rp = Util.loadProperties(RANDOM_CONFIG_RESOURCE);
            this.properties = generateRandomProperties(rp);
        }
    }

    private static String generateProperty(Random random, Properties rp, String minPropertyName, String maxPropertyName) {
        int min = Integer.parseInt(rp.getProperty(minPropertyName));
        int max = Integer.parseInt(rp.getProperty(maxPropertyName));
        return Integer.toString(min + ((max > min) ? random.nextInt(max - min) : 0));
    }

    private static String generateDoubleProperty(Random random, Properties rp, String minPropertyName, String maxPropertyName) {
        double min = Double.parseDouble(rp.getProperty(minPropertyName));
        double max = Double.parseDouble(rp.getProperty(maxPropertyName));
        return String.format("%.3f", min + random.nextDouble() * (max - min));
    }

    private static Properties generateRandomProperties(Properties rp) {
        Properties p = new Properties();
        Random random = new Random();
        p.setProperty(LOGIN_HOUR,
                generateProperty(random, rp, MIN_LOGIN_HOUR, MAX_LOGIN_HOUR));
        p.setProperty(LOGIN_VARIANCE_IN_MINUTES,
                generateProperty(random, rp, MIN_LOGIN_VARIANCE_IN_MINUTES, MAX_LOGIN_VARIANCE_IN_MINUTES));
        p.setProperty(LOGIN_TIME,
                generateProperty(random, rp, MIN_LOGIN_TIME, MAX_LOGIN_TIME));
        p.setProperty(LOGIN_TIME_VARIANCE_IN_MINUTES,
                generateProperty(random, rp, MIN_LOGIN_TIME_VARIANCE_IN_MINUTES, MAX_LOGIN_TIME_VARIANCE_IN_MINUTES));

        p.setProperty(ATTENDANCE_MEAN,
                generateProperty(random, rp, MIN_ATTENDANCE_MEAN, MAX_ATTENDANCE_MEAN));
        p.setProperty(ATTENDANCE_VARIANCE,
                generateProperty(random, rp, MIN_ATTENDANCE_VARIANCE, MAX_ATTENDANCE_VARIANCE));
        p.setProperty(ATTENDANCE_CHANGE_PERCENT_PER_PERIOD,
                generateDoubleProperty(random, rp, MIN_ATTENDANCE_CHANGE_PERCENT_PER_PERIOD, MAX_ATTENDANCE_CHANGE_PERCENT_PER_PERIOD));

        p.setProperty(DAYS_IN_PERIOD, rp.getProperty(DAYS_IN_PERIOD));
        p.setProperty(CHART_START_HOUR, rp.getProperty(CHART_START_HOUR));
        p.setProperty(CHART_END_HOUR, rp.getProperty(CHART_END_HOUR));
        p.setProperty(CHART_PERIOD_IN_MINUTES, rp.getProperty(CHART_PERIOD_IN_MINUTES));

        return p;
    }

    @Override
    public int getLoginHour(int day) {
        return Integer.parseInt(properties.getProperty(LOGIN_HOUR));
    }

    @Override
    public int getLoginVarianceInMinutes(int day) {
        return Integer.parseInt(properties.getProperty(LOGIN_VARIANCE_IN_MINUTES));
    }

    @Override
    public int getLoginTime(int day) {
        return Integer.parseInt(properties.getProperty(LOGIN_TIME));
    }

    @Override
    public int getLoginTimeVarianceInMinutes(int day) {
        return Integer.parseInt(properties.getProperty(LOGIN_TIME_VARIANCE_IN_MINUTES));
    }


    @Override
    public int getAttendanceMean(int day) {
        return Integer.parseInt(properties.getProperty(ATTENDANCE_MEAN));
    }

    @Override
    public int getAttendanceVariance(int day) {
        return Integer.parseInt(properties.getProperty(ATTENDANCE_VARIANCE));
    }

    @Override
    public double getAttendanceChangePercentPerPeriod(int day) {
        return Double.parseDouble(properties.getProperty(ATTENDANCE_CHANGE_PERCENT_PER_PERIOD));
    }

    @Override
    public int getDaysInPeriod(int day) {
        return Integer.parseInt(properties.getProperty(DAYS_IN_PERIOD));
    }



    @Override
    public int getChartStartHour(int day) {
        return Integer.parseInt(properties.getProperty(CHART_START_HOUR));
    }

    @Override
    public int getChartEndHour(int day) {
        return Integer.parseInt(properties.getProperty(CHART_END_HOUR));
    }

    @Override
    public int getChartPeriodInMinutes(int day) {
        return Integer.parseInt(properties.getProperty(CHART_PERIOD_IN_MINUTES));
    }

    @Override
    public void print(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            int day = 1;
            fileWriter.write("Configuration settings used to generate customer data\n");
            fileWriter.write(String.format("%s = %s \n",
                    LOGIN_HOUR, getLoginHour(day)));
            fileWriter.write(String.format("%s = %s \n",
                    LOGIN_VARIANCE_IN_MINUTES, getLoginVarianceInMinutes(day)));
            fileWriter.write(String.format("%s = %s \n",
                    LOGIN_TIME, getLoginTime(day)));
            fileWriter.write(String.format("%s = %s \n",
                    LOGIN_TIME_VARIANCE_IN_MINUTES, getLoginTimeVarianceInMinutes(day)));

            fileWriter.write(String.format("%s = %s \n",
                    ATTENDANCE_MEAN, getAttendanceMean(day)));
            fileWriter.write(String.format("%s = %s \n",
                    ATTENDANCE_VARIANCE, getAttendanceVariance(day)));
            fileWriter.write(String.format("%s = %4f \n",
                    ATTENDANCE_CHANGE_PERCENT_PER_PERIOD, getAttendanceChangePercentPerPeriod(day)));

            fileWriter.write(String.format("%s = %s \n", DAYS_IN_PERIOD, getDaysInPeriod(day)));
            fileWriter.write(String.format("%s = %s \n", CHART_START_HOUR, getChartStartHour(day)));
            fileWriter.write(String.format("%s = %s \n", CHART_END_HOUR, getChartEndHour(day)));
            fileWriter.write(String.format("%s = %s \n\n", CHART_PERIOD_IN_MINUTES, getChartPeriodInMinutes(day)));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.format("Failed to print config file %s, Exception: %s \n", fileName, e.getMessage());
        }
    }
}
