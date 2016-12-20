package kz.kase.reporter.test;

import java.sql.*;

public class TestDb {

    public static void main(String[] args) throws SQLException, ClassNotFoundException {

        String service = "csdb.kase.kz";
        String user = "CSVIEW";
        String pass = "CSVIEW";
        String host = "192.168.111.2";
        String port = "1521";
        String url = "jdbc:oracle:thin:" + user + "/" + pass + "@//" + host + ":" + port + "/" + service;
        System.out.println("url = " + url);

        Connection con = null;
        Class.forName("oracle.jdbc.driver.OracleDriver");
        try {
            con = DriverManager.getConnection(url);

            Statement statement = con.createStatement();

            statement.executeQuery("select CS.REPORTS_PKG.GETORGBYNIN('KZPC2Y10B426','namerus') from dual");
            ResultSet rs = statement.getResultSet();

            rs.beforeFirst();
            rs.last();
            int size = rs.getRow();

            System.out.println("size = " + size);

        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
}
