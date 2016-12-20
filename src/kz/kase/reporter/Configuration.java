package kz.kase.reporter;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    private static final Logger log = Logger.getLogger(Configuration.class);
    private Properties config;
    private static final String SEP = File.separator;
    public static final String APP_DIR = System.getProperty("user.dir");
    public static final String CONFIG_FILE = APP_DIR + SEP + "config.properties";
    private static Configuration instance;

    public static final String ARCTS_HOST = "arcts.host";
    public static final String ARCTS_PORT = "arcts.port";
    public static final String ARCTS_USER = "arcts.user";
    public static final String ARCTS_PASS = "arcts.pass";
    public static final String ARCTS_SERVICE = "arcts.service";
    public static final String TS_HOST = "ts.host";
    public static final String TS_PORT = "ts.port";
    public static final String TS_USER = "ts.user";
    public static final String TS_PASS = "ts.pass";
    public static final String TS_SERVICE = "ts.service";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String USERS = "users";
    public static final String REGULAR = "launch.regular";
    public static final String INTERVAL = "launch.interval";
    public static final String JASPER_FORM = "jasper.form";
    public static final String JASPER_FORM_OBL = "jasper.form.obl";
    public static final String BROKER_NAME = "broker.name";



    public Configuration() {
        config = new Properties();
        loadConfig(config, CONFIG_FILE);
    }

    public String getConfig(String name, String defaultValue) {
        return config.getProperty(name, defaultValue);
    }

    public String getConfig(String name) {
        return getConfig(name, null);
    }

    private static void loadConfig(Properties config, String configFile) {
        try {
            config.load(new BufferedReader(new FileReader(configFile)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }
    // Getters
    public String getArctsHost() {
        return getConfig(ARCTS_HOST);
    }

    public String getArctsPort() {
        return getConfig(ARCTS_PORT);
    }

    public String getArctsUser() {
        return getConfig(ARCTS_USER);
    }

    public String getArctsPass() {
        return getConfig(ARCTS_PASS);
    }

    public String getArctsService() {
        return getConfig(ARCTS_SERVICE);
    }

    public String getTsHost() {
        return getConfig(TS_HOST);
    }

    public String getTsPort() {
        return getConfig(TS_PORT);
    }

    public String getTsUser() {
        return getConfig(TS_USER);
    }

    public String getTsPass() {
        return getConfig(TS_PASS);
    }

    public String getTsService() {
        return getConfig(TS_SERVICE);
    }

    public String getStartTime() {
        return getConfig(START_TIME);
    }
    public String getEndTime() {
        return getConfig(END_TIME);
    }
    public String getUsers() {
        return getConfig(USERS);
    }

    public String getObligationForm() {
        return getConfig(JASPER_FORM_OBL);
    }
    public String getRegularForm() {
        return getConfig(JASPER_FORM);
    }

    public String getBrokerName() {
        return getConfig(BROKER_NAME);
    }

    public boolean isRegular() {
        String regular = getConfig(REGULAR);
        if (regular.equalsIgnoreCase("Y"))
           return true;
        else return false;
    }

    public Integer getInterval(){
        String interval = getConfig(INTERVAL);
        return Integer.parseInt(interval);
    }

    public static void main(String[] args) {
         //Configuration.getInstance().getHost();
//        System.out.println(Configuration.getInstance().getStartTime());
//        System.out.println(Configuration.getInstance().getUsers());

    }
}
