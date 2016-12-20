package kz.kase.reporter.oracle;


public class ConnectionFactory {

    public ConnectionFactory() {

    }

    public static OracleConnector getProdBaseConnector() {
        return new OracleConnector("192.168.111.2", "1521", "world.kase.kz", "arcts_strade", "strade");
    }

    public static OracleConnector getTransitBaseConnector() {
        return new OracleConnector("192.168.111.19", "1521", "ts19.kase.kz", "strade", "strade");
    }

//    public static OracleConnector getOracleCsConnector(){
//        return new OracleConnector("192.168.111.21","1521","csdb.kase.kz","CSVIEW","csview");
//    }

}
