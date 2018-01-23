import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {

    public static Properties loadProperties(String fileName) {
        Properties p = new Properties();
        try (InputStream inputStream = Util.class.getResourceAsStream(fileName)) {
            if (inputStream != null) {
                p.load(inputStream);
            } else {
                System.out.println("Unable to find properties file " + fileName);
            }
        } catch (IOException e) {
            System.err.println("Unable to load properties from " + fileName + ", Exception:" + e.getMessage());
        }
        return p;
    }

}
