import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DataGenerator {

    private static final String CUSTOMER_CONFIG_TEMPLATE =
            "customer-{customerNumber}-config.txt";

    private static final String CUSTOMER_ATTENDANCE_TEMPLATE =
            "customer-{customerNumber}-attendance.csv";

    private static final String CUSTOMER_LOGGED_USERS_FILENAME_TEMPLATE =
            "customer-{customerNumber}-logged-users.csv";

    private static final String CUSTOMER_LOGIN_LOGOUT_TIMES_FILENAME_TEMPLATE =
            "customer-{customerNumber}-login-logout-times.csv";

    private static Calendar CALENDAR;

    private static Date START_DATE;

    private static SimpleDateFormat OUTPUT_DATE_FORMAT;

    private static SimpleDateFormat OUTPUT_FORMAT;
    static {
        try {
            CALENDAR = Calendar.getInstance();
            START_DATE = new SimpleDateFormat("mm-dd-yyyy", Locale.ENGLISH).parse("01-02-2017");
            OUTPUT_DATE_FORMAT = new SimpleDateFormat("E yyyy-MM-dd", Locale.ENGLISH);
            OUTPUT_FORMAT = new SimpleDateFormat("E yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH);
        } catch (Exception e) {}
    }

    private static String getCustomerConfigFileName(int customerNumber) {
        return CUSTOMER_CONFIG_TEMPLATE
                .replace("{customerNumber}", Integer.toString(customerNumber));
    }

    private static String getCustomerAttendanceFileName(int customerNumber) {
        return CUSTOMER_ATTENDANCE_TEMPLATE
                .replace("{customerNumber}", Integer.toString(customerNumber));
    }

    private static String getCustomerLoggedUsersFileName(int customerNumber) {
        return CUSTOMER_LOGGED_USERS_FILENAME_TEMPLATE
                .replace("{customerNumber}", Integer.toString(customerNumber));
    }

    private static String getCustomerLoginLogoutTimesFileName(int customerNumber) {
        return CUSTOMER_LOGIN_LOGOUT_TIMES_FILENAME_TEMPLATE
                .replace("{customerNumber}", Integer.toString(customerNumber));
    }

    private AbstractConfig config;

    private int day;

    private int attendanceMovingMean;

    private NormalDistribution[] loginGenerators;

    private NormalDistribution[] loginTimeGenerators;

    public DataGenerator(AbstractConfig config) {
        this.config = config;
        int[] cycleValues = config.cycleValues;
        this.loginGenerators = new NormalDistribution[cycleValues.length];
        this.loginTimeGenerators = new NormalDistribution[cycleValues.length];

        int day = 1;
        for (int i=0; i< cycleValues.length; i++) {
            int loginMean = (int) TimeUnit.HOURS.toMinutes(config.getLoginHour(day));
            this.loginGenerators[i] = new NormalDistribution(loginMean, config.getLoginVarianceInMinutes(day));

            int loginTimeMean = (int) TimeUnit.HOURS.toMinutes(config.getLoginTime(day));
            this.loginTimeGenerators[i] = new NormalDistribution(loginTimeMean, config.getLoginTimeVarianceInMinutes(day));
            day += cycleValues[i];
        }
        this.day = 0;
        this.attendanceMovingMean = config.getAttendanceMean(1);
    }

    public int[] generateNextDay(int customerNumber) {
        this.day++;
        int periods = (config.getChartEndHour(this.day) - config.getChartStartHour(this.day)) * 60 /
                config.getChartPeriodInMinutes(this.day);

        double factor = 1.0 + (config.getAttendanceChangePercentPerPeriod(this.day) * this.day / config.getDaysInPeriod(this.day));
        this.attendanceMovingMean = (int) (config.getAttendanceMean(this.day) * factor);
        NormalDistribution attendanceGenerator = new NormalDistribution(
                this.attendanceMovingMean, config.getAttendanceVariance(this.day));
        int attendance = Math.max(0, attendanceGenerator.generate());
        int[] chart = new int[periods];
        String customerAttendanceFileName = getCustomerAttendanceFileName(customerNumber);
        String loginLogoutTimesFileName = getCustomerLoginLogoutTimesFileName(customerNumber);
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(customerAttendanceFileName, true);
            fileWriter.write(String.format("%s, %5d\n", getDate(this.day,0, true), attendance));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.format("Failed to record day %d attendance for customer number %d to file %s, Exception:%s",
                    day, customerNumber, customerAttendanceFileName, e.getMessage());
        }
        try {
            int dayIndex = config.getCycleIndex(this.day);
            fileWriter = new FileWriter(loginLogoutTimesFileName, true);
            for (int i = 0; i < attendance; i++) {
                int loginMinute = loginGenerators[dayIndex].generate();
                int loginTimeInMinutes = loginTimeGenerators[dayIndex].generate();
                int logoutMinute = loginMinute + loginTimeInMinutes;
                fileWriter.write(String.format(String.format("%s , %s\n",
                        getDate(this.day, loginMinute, false), getDate(this.day, logoutMinute,false))));

                int periodStart = config.getChartStartHour(this.day) * 60;
                int periodEnd = periodStart + config.getChartPeriodInMinutes(this.day);
                for (int j = 0; j < periods; j++) {
                    if (((periodStart <= loginMinute)  && (loginMinute  < periodEnd)) ||
                        ((periodStart <= logoutMinute) && (logoutMinute < periodEnd)) ||
                        ((loginMinute <= periodStart ) && (periodEnd    < logoutMinute))) {
                        chart[j] = chart[j] + 1;
                    }
                    periodStart = periodEnd;
                    periodEnd = periodStart + config.getChartPeriodInMinutes(this.day);
                }
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.format("Failed to record day %d login/logout times for customer number %d to file %s, Exception:%s",
                    day, customerNumber, loginLogoutTimesFileName, e.getMessage());
        }
        return chart;
    }

    public String toTime(int minutes) {
        int hour = minutes / 60;
        minutes %= 60;
        String AMPM = (hour < 12) ? "AM" : "PM";
        hour = (0 == hour) ? 12 : ((hour > 12) ? hour-12 : hour);
        return String.format("%02d:%02d %s", hour, minutes, AMPM);
    }

    public void printChartHeader(String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            int minutes = 60 * config.getChartStartHour(this.day);
            int toMinutes = 60 * config.getChartEndHour(this.day);
            int period = config.getChartPeriodInMinutes(this.day);
            fileWriter.write("Date/Time ");
            for (; minutes < toMinutes;) {
                fileWriter.write(String.format(", %s", toTime(minutes)));
                minutes +=  period;
            }
            fileWriter.write("\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.format("Failed to write login/logout chart header to file %s, Exception:%s",
                    fileName, e.getMessage());
        }
    }

    public void printChart(String fileName, int[] chart) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true);
            fileWriter.write(String.format("%s, %5d", getDate(day,0, true), chart[0]));
            for (int i = 1; i < chart.length; i++) {
                fileWriter.write(String.format(", %5d", chart[i]));
            }
            fileWriter.write("\n");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            System.err.format("Failed to write day %d login/logout chart to file %s, Exception:%s",
                    day, fileName, e.getMessage());
        }
    }

    public String getDate(int day, int minutes, boolean useShortDate) {
        CALENDAR.setTime(START_DATE);
        CALENDAR.add(Calendar.DATE, day-1);
        CALENDAR.add(Calendar.MINUTE, minutes);
        return useShortDate ?
                OUTPUT_DATE_FORMAT.format(CALENDAR.getTime()) :
                OUTPUT_FORMAT.format(CALENDAR.getTime());
    }

    public static void main(String[] args) {
        ToolConfig toolConfig = new ToolConfig();
        boolean useCyclicConfig = toolConfig.isUseCyclicConfig();
        boolean useFixedConfig = toolConfig.isUseFixedConfig();
        int customers = toolConfig.getCustomers();
        int days = toolConfig.getDays();

        for (int customerNumber = 1; customerNumber <= customers; customerNumber++) {
            String customerConfigFileName = getCustomerConfigFileName(customerNumber);
            AbstractConfig config = useCyclicConfig ? new CyclicConfig(useFixedConfig) : new Config(useFixedConfig);
            config.print(customerConfigFileName);
            DataGenerator generator = new DataGenerator(config);

            System.out.format("Start data generation for customer %4d\n", customerNumber);
            String customerLoggedUsersFileName = getCustomerLoggedUsersFileName(customerNumber);
            generator.printChartHeader(customerLoggedUsersFileName);
            for (int day = 1; day <= days; day++) {
                System.out.format("Generating data for customer %4d day: %4d\n", customerNumber, day);
                int[] chart = generator.generateNextDay(customerNumber);
                generator.printChart(customerLoggedUsersFileName, chart);
            }
        }
        System.out.println("Done");
    }
}
