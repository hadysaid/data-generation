import java.util.Properties;

public class ToolConfig {
    private static final String TOOL_CONFIG_RESOURCE = "tool.config";

    private static final String USE_CYCLIC_CONIG = "UseCyclicConfig";

    private static final String USE_FIXED_CONIG = "UseFixedConfig";

    private static final String CUSTOMERS = "Customers";

    private static final String DAYS = "Days";

    private Properties properties;

    public ToolConfig() {
        properties = Util.loadProperties(TOOL_CONFIG_RESOURCE);
    }

    public boolean isUseCyclicConfig() {
        return Boolean.parseBoolean(properties.getProperty(USE_CYCLIC_CONIG));
    }

    public boolean isUseFixedConfig() {
        return Boolean.parseBoolean(properties.getProperty(USE_FIXED_CONIG));
    }

    public int getCustomers() {
        return Integer.parseInt(properties.getProperty(CUSTOMERS));
    }

    public int getDays() {
        return Integer.parseInt(properties.getProperty(DAYS));
    }
}
