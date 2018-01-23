import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

public abstract class AbstractConfig {
    protected static final String LOGIN_HOUR = "LoginHour";

    protected static final String LOGIN_VARIANCE_IN_MINUTES = "LoginVarianceInMinutes";

    protected static final String LOGIN_TIME = "LoginTime";

    protected static final String LOGIN_TIME_VARIANCE_IN_MINUTES = "LoginTimeVarianceInMinutes";

    protected static final String ATTENDANCE_MEAN = "AttendanceMean";

    protected static final String ATTENDANCE_VARIANCE = "AttendanceVariance";

    protected static final String ATTENDANCE_CHANGE_PERCENT_PER_PERIOD = "AttendanceChangePercentPerPeriod";

    protected static final String MIN_LOGIN_HOUR = "MinLoginHour";

    protected static final String MAX_LOGIN_HOUR = "MaxLoginHour";

    protected static final String MIN_LOGIN_VARIANCE_IN_MINUTES = "MinLoginVarianceInMinutes";

    protected static final String MAX_LOGIN_VARIANCE_IN_MINUTES = "MaxLoginVarianceInMinutes";

    protected static final String MIN_LOGIN_TIME = "MinLoginTime";

    protected static final String MAX_LOGIN_TIME = "MaxLoginTime";

    protected static final String MIN_LOGIN_TIME_VARIANCE_IN_MINUTES = "MinLoginTimeVarianceInMinutes";

    protected static final String MAX_LOGIN_TIME_VARIANCE_IN_MINUTES = "MaxLoginTimeVarianceInMinutes";

    protected static final String MIN_ATTENDANCE_MEAN = "MinAttendanceMean";

    protected static final String MAX_ATTENDANCE_MEAN = "MaxAttendanceMean";

    protected static final String MIN_ATTENDANCE_VARIANCE = "MinAttendanceVariance";

    protected static final String MAX_ATTENDANCE_VARIANCE = "MaxAttendanceVariance";

    protected static final String MIN_ATTENDANCE_CHANGE_PERCENT_PER_PERIOD = "MinAttendanceChangePercentPerPeriod";

    protected static final String MAX_ATTENDANCE_CHANGE_PERCENT_PER_PERIOD = "MaxAttendanceChangePercentPerPeriod";

    protected static final String DAYS_IN_PERIOD = "DaysInPeriod";

    protected static final String CHART_START_HOUR = "ChartStartHour";

    protected static final String CHART_END_HOUR = "ChartEndHour";

    protected static final String CHART_PERIOD_IN_MINUTES = "ChartPeriodInMinutes";

    protected int cycleSizeInDays;

    protected int[] cycleValues;

    public AbstractConfig() {
        cycleValues = new int[1];
        cycleValues[0] = 1;
        cycleSizeInDays = 1;
    }

    protected static int getRandomProperty(Random random, int min, int max) {
        return (min + ((max > min) ? random.nextInt(max - min) : 0));
    }

    protected static double getRandomDoubleProperty(Random random, double min, double max) {
        return (min + random.nextDouble() * (max - min));
    }

    public int[] getCycleValues() {
        return this.cycleValues;
    }

    public int getCycleSizeInDays() {
        return this.cycleSizeInDays;
    }

    public int getCycleIndex(int day) {
        day = (day % cycleSizeInDays);
        day = (0 == day) ? cycleSizeInDays : day;
        for(int i=0; i<cycleValues.length; i++) {
            if (day <= cycleValues[i]) {
                return i;
            }
            day -= cycleValues[i];
        }
        return -1;
    }

    public abstract int getLoginHour(int day);

    public abstract int getLoginVarianceInMinutes(int day);

    public abstract int getLoginTime(int day);

    public abstract int getLoginTimeVarianceInMinutes(int day);


    public abstract int getAttendanceMean(int day);

    public abstract int getAttendanceVariance(int day);

    public abstract double getAttendanceChangePercentPerPeriod(int day);


    public abstract int getDaysInPeriod(int day);

    public abstract int getChartStartHour(int day);

    public abstract int getChartEndHour(int day);

    public abstract int getChartPeriodInMinutes(int day);


    public abstract void print(String fileName);
}
