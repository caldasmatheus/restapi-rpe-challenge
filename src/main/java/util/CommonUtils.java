package util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CommonUtils {

    private CommonUtils() {
    }

    public static String retornarValorArquivoConfiguracao(String propriedade) {
        Properties properties;

        try {
            properties = new Properties();

            String env = null == System.getProperty("env") ? "local" : System.getProperty("env");
            String separadorArquivo = System.getProperty("file.separator");

            try (InputStream propFileInpStream = CommonUtils.class.getClassLoader()
                    .getResourceAsStream("conf" + separadorArquivo + env + separadorArquivo + "config.properties")) {
                properties.load(propFileInpStream);
            }

            return properties.getProperty(propriedade);

        } catch (IOException | NullPointerException e) {
        }
        return null;
    }
}