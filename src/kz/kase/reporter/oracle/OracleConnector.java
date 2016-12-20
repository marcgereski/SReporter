package kz.kase.reporter.oracle;


import kz.kase.reporter.Configuration;
import kz.kase.reporter.StatusSelector;
import kz.kase.reporter.ui.MainFrame;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class OracleConnector {

    private static final Logger log = Logger.getLogger(OracleConnector.class);
    Connection connection;
    public static final String ARCH_TS = "arch_ts";
    public static final String TS = "ts";

    public static String PREFIX;

    public OracleConnector(String baseType) {
        if (baseType.equals(ARCH_TS)) {

            String host = Configuration.getInstance().getArctsHost();
            String port = Configuration.getInstance().getArctsPort();
            String service = Configuration.getInstance().getArctsService();
            String user = Configuration.getInstance().getArctsUser();
            String pass = Configuration.getInstance().getArctsPass();
            //setPrefix((String) properties.get("arcts.prefix"));
            log.info(host + " " + port + " " + service + " " + user + " " + pass);
            connectToBase(host, port, service, user, pass);

        } else if (baseType.equals(TS)) {

            String host = Configuration.getInstance().getTsHost();
            String port = Configuration.getInstance().getTsPort();
            String service = Configuration.getInstance().getTsService();
            String user = Configuration.getInstance().getTsUser();
            String pass = Configuration.getInstance().getTsPass();
            log.info(host + " " + port + " " + service + " " + user + " " + pass);
            connectToBase(host, port, service, user, pass);
        }
    }

    private void setPrefix(String prefix) {
        if (prefix == null || "".equals(prefix)) {
            PREFIX = "";
        } else {
            PREFIX = prefix + ".";
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public OracleConnector(String host, String port, String service, String user, String pass) {
        connectToBase(host, port, service, user, pass);
    }


    private void connectToBase(String host, String port, String service, String user, String pass) {
        String url = null;
        StatusSelector.getInstance().setStatus(MainFrame.DATA_BASE_CONNECTING);
        try {
            if (service.contains(".")) {
                url = "jdbc:oracle:thin:" + user + "/" + pass + "@//" + host + ":" + port + "/" + service;
            } else {
                url = "jdbc:oracle:thin:" + user + "/" + pass + "@" + host + ":" + port + ":" + service;
            }
            Class.forName("oracle.jdbc.driver.OracleDriver");
            connection = DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage());
        } catch (SQLException se) {
            log.error(se.getMessage());
        }
        if (connection == null) {
            log.error("NOT CONNECTED to " + url);
            StatusSelector.getInstance().setStatus(MainFrame.BASE_CONNECT_FAIL);
        } else {
            log.info("CONNECTED to " + url);
            StatusSelector.getInstance().setStatus(MainFrame.BASE_CONNECTED);
        }

    }

    public Connection getConnection() {
        return connection;
    }

//    public static void main(String[] args) {
//        OracleConnector oc = new OracleConnector();
//        Connection connect = oc.getConnection();
//        try {
//            Statement statment = connect.createStatement();
//            statment.executeQuery(QueryHolder.getQueryForAllClients());
//            ResultSet rs = statment.getResultSet();
//            while (rs.next()) {
//                System.out.println(rs.getString(1) + " | " + rs.getString(2)+ " | " +rs.getString(3) + " | " + rs.getString(4)+ " | " +rs.getString(5)+ " | " + rs.getString(6)+ " | " +
//                rs.getString(7) + " | " + rs.getString(8)+ " | " +rs.getString(9));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }

}
