package kz.kase.reporter.test;


import kz.kase.reporter.oracle.ConnectionFactory;
import kz.kase.reporter.oracle.OracleConnector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestInstrDB {

    OracleConnector conn;

    TestInstrDB() {
//        conn = ConnectionFactory.getProdBaseConnector();
        conn = ConnectionFactory.getTransitBaseConnector();
    }

    public void createInstrQuery() {
        Connection connect = conn.getConnection();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = connect.createStatement();
            rs = st.executeQuery("select * from ts.currinstrument where shortname='KZTK'");
//            rs = st.executeQuery("(select shortname from ts.sector where ssecsid in (select ownid from ts.subsector where inst_id =517580))type");
//          rs = st.executeQuery("select * from sector where shortame='Eqs_List_ST'");
            rs.next();
            System.out.println(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }


    }

    public void createEmitetnQuery() {
        Connection connect = conn.getConnection();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = connect.createStatement();
            rs = st.executeQuery("select * from emitents");
            int i = 1500;
            while (i != 0) {
                i--;
                rs.next();
                System.out.println(i);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }


    }

    public void getEmitByInstrName() {
        Connection connect = conn.getConnection();
        Statement st = null;
        ResultSet rs = null;
        try {
            st = connect.createStatement();
            rs = st.executeQuery("select emitent from emitents where instr_code='REPO_KZT_001'");
            rs.next();
            System.out.println(rs.getString(1));
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }

    public static void main(String[] args) throws SQLException {
        TestInstrDB test = new TestInstrDB();
//        test.getEmitByInstrName();
//        test.createInstrQuery();
        test.createEmitetnQuery();
    }


}
