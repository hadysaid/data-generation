import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

public class CyclicConfig extends AbstractConfig {
    private static final String FIXED_CONFIG_RESOURCE = "cyclic-fixed-data-generation.config";

    private static final String RANDOM_CONFIG_RESOURCE = "cyclic-random-data-generation.config";

    private static final String CYCLE_PATTERN = "CyclePattern";

    private int[] loginHours;

    private int[] loginVarianceInMinutes;

    private int[] loginTimes;

    private int[] loginTimeVarianceInMinutes;

    private int[] attendanceMeans;

    private int[] attendanceVariances;

    private double[] attendanceChangePercentPerPeriods;

    private int daysInPeriod;

    private int chartStartHour;

    private int chartEndHour;

    private int chartPeriodInMinutes;

    private int[] minLoginHours;

    private int[] maxLoginHours;

    private int[] minLoginVarianceInMinutes;

    private int[] maxLoginVarianceInMinutes;

    private int[] minLoginTimes;

    private int[] maxLoginTimes;

    private int[] minLoginTimeVarianceInMinutes;

    private int[] maxLoginTimeVarianceInMinutes;

    private int[] minAttendanceMeans;

    private int[] maxAttendanceMeans;

    private int[] minAttendanceVariances;

    private int[] maxAttendanceVariances;

    private double[] minAttendanceChangePercentPerPeriods;

    private double[] maxAttendanceChangePercentPerPeriods;

    public CyclicConfig(boolean useFixedConfig) {
        if (useFixedConfig) {
            Properties p = Util.loadProperties(FIXED_CONFIG_RESOURCE);
            readFixedConfig(p);
        } else {
            Properties rp = Util.loadProperties(RANDOM_CONFIG_RESOURCE);
            readRandomConfig(rp);
            generateRandomProperties(rp);
        }
    }

    private int[] parseIntCycles(Properties p, String propertyName) {
        String[] cycles = p.getProperty(propertyName).split(",");
        int[] cycleValues = new int[cycles.length];
        for (int i=0 ; i< cycles.length; i++) {
            cycleValues[i] = Integer.parseInt(cycles[i].trim());
        }
        return cycleValues;
    }

    private double[] parseDoubleCycles(Properties p, String propertyName) {
        String[] cycles = p.getProperty(propertyName).split(",");
        double[] cycleValues = new double[cycles.length];
        for (int i=0 ; i< cycles.length; i++) {
            cycleValues[i] = Double.parseDouble(cycles[i]);
        }
        return cycleValues;
    }

    private void readFixedConfig(Properties p) {
        cycleValues = parseIntCycles(p, CYCLE_PATTERN);
        cycleSizeInDays = Arrays.stream(cycleValues).sum();

        loginHours = parseIntCycles(p, LOGIN_HOUR);
        loginVarianceInMinutes = parseIntCycles(p, LOGIN_VARIANCE_IN_MINUTES);
        loginTimes = parseIntCycles(p, LOGIN_TIME);
        loginTimeVarianceInMinutes = parseIntCycles(p, LOGIN_TIME_VARIANCE_IN_MINUTES);
        attendanceMeans = parseIntCycles(p, ATTENDANCE_MEAN);
        attendanceVariances = parseIntCycles(p, ATTENDANCE_VARIANCE);
        attendanceChangePercentPerPeriods = parseDoubleCycles(p, ATTENDANCE_CHANGE_PERCENT_PER_PERIOD);
        daysInPeriod = Integer.parseInt(p.getProperty(DAYS_IN_PERIOD));
        chartStartHour = Integer.parseInt(p.getProperty(CHART_START_HOUR));
        chartEndHour = Integer.parseInt(p.getProperty(CHART_END_HOUR));
        chartPeriodInMinutes = Integer.parseInt(p.getProperty(CHART_PERIOD_IN_MINUTES));
    }

    private void readRandomConfig(Properties rp) {
        cycleValues = parseIntCycles(rp, CYCLE_PATTERN);
        cycleSizeInDays = Arrays.stream(cycleValues).sum();

        minLoginHours = parseIntCycles(rp, MIN_LOGIN_HOUR);
        maxLoginHours = parseIntCycles(rp, MAX_LOGIN_HOUR);
        minLoginVarianceInMinutes = parseIntCycles(rp, MIN_LOGIN_VARIANCE_IN_MINUTES);
        maxLoginVarianceInMinutes = parseIntCycles(rp, MAX_LOGIN_VARIANCE_IN_MINUTES);

        minLoginTimes = parseIntCycles(rp, MIN_LOGIN_TIME);
        maxLoginTimes = parseIntCycles(rp, MAX_LOGIN_TIME);
        minLoginTimeVarianceInMinutes = parseIntCycles(rp, MIN_LOGIN_TIME_VARIANCE_IN_MINUTES);
        maxLoginTimeVarianceInMinutes = parseIntCycles(rp, MAX_LOGIN_TIME_VARIANCE_IN_MINUTES);

        minAttendanceMeans = parseIntCycles(rp, MIN_ATTENDANCE_MEAN);
        maxAttendanceMeans = parseIntCycles(rp, MAX_ATTENDANCE_MEAN);
        minAttendanceVariances = parseIntCycles(rp, MIN_ATTENDANCE_VARIANCE);
        maxAttendanceVariances = parseIntCycles(rp, MAX_ATTENDANCE_VARIANCE);
        minAttendanceChangePercentPerPeriods = parseDoubleCycles(rp, MIN_ATTENDANCE_CHANGE_PERCENT_PER_PERIOD);
        maxAttendanceChangePercentPerPeriods = parseDoubleCycles(rp, MAX_ATTENDANCE_CHANGE_PERCENT_PER_PERIOD);

        daysInPeriod = Integer.parseInt(rp.getProperty(DAYS_IN_PERIOD));
        chartStartHour = Integer.parseInt(rp.getProperty(CHART_START_HOUR));
        chartEndHour = Integer.parseInt(rp.getProperty(CHART_END_HOUR));
        chartPeriodInMinutes = Integer.parseInt(rp.getProperty(CHART_PERIOD_IN_MINUTES));
    }

    private void generateRandomProperties(Properties rp) {
        Random random = new Random();
        int length = this.cycleValues.length;
        loginHours = new int[length];
        loginVarianceInMinutes = new int[length];
        loginTimes = new int[length];
        loginTimeVarianceInMinutes = new int[length];

        attendanceMeans = new int[length];
        attendanceVariances = new int[length];
        attendanceChangePercentPerPeriods = new double[length];

        for(int i=0; i<this.cycleValues.length; i++) {
            loginHours[i] = getRandomProperty(
                    random, minLoginHours[i], maxLoginHours[i]);
            loginVarianceInMinutes[i] = getRandomProperty(
                    random, minLoginVarianceInMinutes[i], maxLoginVarianceInMinutes[i]);
            loginTimes[i] = getRandomProperty(
                    random, minLoginTimes[i], maxLoginTimes[i]);
            loginTimeVarianceInMinutes[i] = getRandomProperty(
                    random, minLoginTimeVarianceInMinutes[i], maxLoginTimeVarianceInMinutes[i]);

            attendanceMeans[i] = getRandomProperty(
                    random, minAttendanceMeans[i], maxAttendanceMeans[i]);
            attendanceVariances[i] = getRandomProperty(
                    random, minAttendanceVariances[i], maxAttendanceVariances[i]);
            attendanceChangePercentPerPeriods[i] = getRandomDoubleProperty(
                    random, minAttendanceChangePercentPerPeriods[i], maxAttendanceChangePercentPerPeriods[i]);

            daysInPeriod = Integer.parseInt(rp.getProperty(DAYS_IN_PERIOD));
            chartStartHour = Integer.parseInt(rp.getProperty(CHART_START_HOUR));
            chartEndHour = Integer.parseInt(rp.getProperty(CHART_END_HOUR));
            chartPeriodInMinutes = Integer.parseInt(rp.getProperty(CHART_PERIOD_IN_MINUTES));
        }
    }

    @Override
    public int getLoginHour(int day) { return loginHours[getCycleIndex(day)]; }

    @Override
    public int getLoginVarianceInMinutes(int day) { return loginVarianceInMinutes[getCycleIndex(day)]; }

    @Override
    public int getLoginTime(int day) {
        return loginTimes[getCycleIndex(day)];
    }

    @Override
    public int getLoginTimeVarianceInMinutes(int day) { return loginTimeVarianceInMinutes[getCycleIndex(day)]; }


    @Override
    public int getAttendanceMean(int day) { return attendanceMeans[getCycleIndex(day)]; }

    @Override
    public int getAttendanceVariance(int day) { return attendanceVariances[getCycleIndex(day)]; }

    @Override
    public double getAttendanceChangePercentPerPeriod(int day) { return attendanceChangePercentPerPeriods[getCycleIndex(day)]; }


    @Override
    public int getDaysInPeriod(int day) { return daysInPeriod; }

    @Override
    public int getChartStartHour(int day) { return chartStartHour; }

    @Override
    public int getChartEndHour(int day) { return chartEndHour; }

    @Override
    public int getChartPeriodInMinutes(int day) { return chartPeriodInMinutes; }

    @Override
    public void print(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            fileWriter.write("Configuration settings used to generate customer data\n");
            int day = 1;
            for (int i=0; i< this.cycleValues.length; i++) {
                fileWriter.write(String.format("Settings for day %d in cycle forward\n", day));
                fileWriter.write(String.format("%s = %s \n", LOGIN_HOUR, getLoginHour(day)));
                fileWriter.write(String.format("%s = %s \n", LOGIN_VARIANCE_IN_MINUTES, getLoginVarianceInMinutes(day)));
                fileWriter.write(String.format("%s = %s \n", LOGIN_TIME, getLoginTime(day)));
                fileWriter.write(String.format("%s = %s \n", LOGIN_TIME_VARIANCE_IN_MINUTES, getLoginTimeVarianceInMinutes(day)));

                fileWriter.write(String.format("%s = %s \n",
                        ATTENDANCE_MEAN, getAttendanceMean(day)));
                fileWriter.write(String.format("%s = %s \n",
                        ATTENDANCE_VARIANCE, getAttendanceVariance(day)));
                fileWriter.write(String.format("%s = %4f \n",
                        ATTENDANCE_CHANGE_PERCENT_PER_PERIOD, getAttendanceChangePercentPerPeriod(day)));
                fileWriter.write(String.format("%s = %s \n",
                        DAYS_IN_PERIOD, getDaysInPeriod(day)));

                fileWriter.write(String.format("%s = %s \n", CHART_START_HOUR, getChartStartHour(day)));
                fileWriter.write(String.format("%s = %s \n", CHART_END_HOUR, getChartEndHour(day)));
                fileWriter.write(String.format("%s = %s \n\n", CHART_PERIOD_IN_MINUTES, getChartPeriodInMinutes(day)));
                day +=  this.cycleValues[i];
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.format("Failed to print config file %s, Exception: %s \n", fileName, e.getMessage());
        }
    }
}
