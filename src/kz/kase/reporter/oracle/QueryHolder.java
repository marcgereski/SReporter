package kz.kase.reporter.oracle;


import kz.kase.reporter.Configuration;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class QueryHolder {
    public static String START_TIME = Configuration.getInstance().getStartTime();
    public static String END_TIME = Configuration.getInstance().getEndTime();
    public static Boolean ISREGULAR = Configuration.getInstance().isRegular();
    public static Integer INTERVAL = Configuration.getInstance().getInterval();

    public static String getAllUsersQuery() {
        return "select distinct s.username from strade.transit_stat s join strade.user_info u on s.username=u.username " +
                "where s.created between to_date('" + START_TIME + "', 'DD/MM/YYYY HH24:MI:SS')" +
                "and to_date('" + END_TIME + "', 'DD/MM/YYYY HH24:MI:SS')"
                ;
    }

    public static String getClientStatQuery(String nickName) {
        return "select * from strade.transit_stat s \n" +
                "join strade.user_info u on s.username=u.username \n" +
                "where u.username='" + nickName + "' and s.created between to_date('" +
                START_TIME + "', 'DD/MM/YYYY HH24:MI:SS') and to_date('" +
                END_TIME + "', 'DD/MM/YYYY HH24:MI:SS') ORDER BY CREATED ASC";
    }

    public static String getLikeClientStatQuery(String nickName) {
        String result ="select * from strade.transit_stat s \n" +
                "join strade.user_info u on s.username=u.username \n" +
                "where u.username like '" + nickName + "%' and s.created between to_date('";
        if(ISREGULAR){
            Date now = new Date(System.currentTimeMillis());
            String currTime =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(now);

            final Calendar beginCal = Calendar.getInstance();
            beginCal.setTime(now);
            beginCal.add(Calendar.MINUTE, -INTERVAL);


            String startTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(beginCal.getTime());
            result += startTime + "', 'DD/MM/YYYY HH24:MI:SS') and to_date('" +
                    currTime + "', 'DD/MM/YYYY HH24:MI:SS') ORDER BY s.username, CREATED ASC";
        }
        else {
            result += START_TIME + "', 'DD/MM/YYYY HH24:MI:SS') and to_date('" +
                    END_TIME + "', 'DD/MM/YYYY HH24:MI:SS') ORDER BY s.username, CREATED ASC";
        }
        return result;
    }

    public static String getFirmByIdQuery(int firmId) {
        return "select * from strade.firm where id='" + firmId + "'";
    }

    public static String emitentsQuery(String instrName) {
        return "select emitent from strade.emitents where instr_code='" + instrName + "'";
    }

    public static String selectInstrDataFromStrTab(String instrId) {
        return "select nin, code, instr_type " +
                "from strade.instrument where id='" + instrId + "'";
    }

    public static String selectAccountCodeStr(String accId) {
        return "select name from strade.account where id =" + accId;
    }

    //todo Queries which currently not used (some of them used by MainFram)
    public static String getAllClientStatQuery() {
        return "select u.full_name,u.id_number,u.id_issue_date,u.id_issued_by," +
                "u.firm_id,s.created,s.username,s.instrid,s.refnum," +
                "s.account,s.quantity,s.price,s.volume,s.direct,s.expired,s.sign from strade.transit_stat s\n" +
                "join strade.user_info u on s.username=u.username";
    }

    @Deprecated
    public static String getQueryForOneClientWithDateInterval(long dateFrom, long dateTo, String clientName) {
        String query = "select\n" +
                "date0,\n" +
                "useritrader.nick ,\n" +
                "currinstrument.extcode,\n" +
                "(select shortname from ts_maxim.sector where ssecsid in (select ownid from ts_maxim.subsector where inst_id =currinstrument.ownid))type,\n" +
                "(select code from ts_maxim.tradeaccount where tradeaccount.id = deltransit.useracc)acc,\n" +
                "deltransit.volume,\n" +
                "deltransit.price,\n" +
                "deltransit.volcontr,\n" +
                "ascii(deltransit.direct)direct,\n" +
                "currinstrument.shortname\n" +
                "from ts_maxim.currinstrument, arcts_maxim.deltransit, ts_maxim.useritrader \n" +
                "where\n" +
                "useritrader.nick in ('" + clientName + "')\n" +
                "and \n" +
                "deltransit.iduser = useritrader.id\n" +
                "and \n" +
                " currinstrument.id = deltransit.idinstr and extcode not like 'R_%'\n" +
                " and\n" +
                "deltransit.sign_hex1 is null\n" +
                " and " + dateFrom + "<=deltransit.date0 and deltransit.date0<= " + dateTo + "\n" +
                "and deltransit.status in (chr(3),chr(13), chr(2),chr(12),chr(1),chr(11), chr(5),chr(6),  chr(7),chr(8), chr(9),chr(10))";
        return query;
    }

    @Deprecated
    public static String getQueryForAllClients(long dateFrom, long dateTo) {
        String query = "select\n" +
                "date0,\n" +
                "useritrader.nick ,\n" +
                "currinstrument.extcode,\n" +
                "(select shortname from ts_maxim.sector where ssecsid in (select ownid from ts_maxim.subsector where inst_id =currinstrument.ownid))type,\n" +
                "(select code from ts_maxim.tradeaccount where tradeaccount.id = deltransit.useracc)acc,\n" +
                "deltransit.volume,\n" +
                "deltransit.price,\n" +
                "deltransit.volcontr,\n" +
                "ascii(deltransit.direct)direct,\n" +
                "currinstrument.shortname\n" +
                "from ts_maxim.currinstrument, arcts_maxim.deltransit, ts_maxim.useritrader \n" +
                "where\n" +
                "deltransit.iduser = useritrader.id\n" +
                "and \n" +
                " currinstrument.id = deltransit.idinstr and extcode not like 'R_%'\n" +
                " and\n" +
                "deltransit.sign_hex1 is null\n" +
                " and " + dateFrom + "<=deltransit.date0 and deltransit.date0<= " + dateTo + "\n" +
                "and deltransit.status in (chr(3),chr(13), chr(2),chr(12),chr(1),chr(11), chr(5),chr(6),  chr(7),chr(8), chr(9),chr(10))";
        return query;
    }

    public static String selectSectorShortName(String ownId) {
        return "select shortname from sector where ssecsid " +
                "in (select ownid from subsector where inst_id =" + ownId + ")";
    }

    public static String selectInstrData(String instrId) {
        return "select extcode, shortname,ownid " +
                "from currinstrument where id='" + instrId + "'";
    }

    public static String selectAccountCode(String accId) {
        return "select code from tradeaccount where id =" + accId;
    }

    public static String getResultInstrType(String type) throws SQLException {
        if (type.contains("Eqs")) type = "Акция";
        if (type.contains("Debts")) type = "Облигация";
        return type;
    }

    public static String getLikeClientsConfirmer(int id) {
        return "select max(name) name from arcts.userbroker " +
                "where id in (select iduser from arcts.dellimitorder where serial in (select ordserial from arcts.deltransit where sign_hex4 =\'" + id + "\'))" +
                "union select max(name) name from ts.userbroker " +
                "where id in (select iduser from arcts.dellimitorder where serial in (select ordserial from arcts.deltransit where sign_hex4 =\'" + id + "\' ))";
    }
//
//    private static final int DATE = 1;
//    private static final int NICK = 2;
//    private static final int NIN = 3;
//    private static final int INSTR_TYPE = 4;
//    private static final int ACCOUNT = 5;
//
//
//    public static Long getResultDate(ResultSet rs) throws SQLException {
//
//        return rs.getLong(1);
//    }
//
//    public static String getRsultNick(ResultSet rs) throws SQLException {
//
//        return rs.getString(2);
//    }
//
//    public static String getResultNin(ResultSet rs) throws SQLException {
//
//        return rs.getString(3);
//    }
//
//    //только для акций и облигациий!!
//    public static String getResultInstrType(ResultSet rs) throws SQLException {
//        String type = rs.getString(4);
//        if (type.contains("Eqs")) type = "Акция";
//        if (type.contains("Debts")) type = "Облигация";
//        return type;
//    }


//    public static String getResultAccount(ResultSet rs) throws SQLException {
//        return rs.getString(5);
//    }
//
//    public static String getResultQty(ResultSet rs) throws SQLException {
//        return rs.getString(6);
//    }
//
//    public static Double getResultPrice(ResultSet rs) throws SQLException {
//        return rs.getDouble(7);
//    }
//
//    public static String getResultVolume(ResultSet rs) throws SQLException {
//        return rs.getString(8);
//    }
//
//    public static Integer getResultDirect(ResultSet rs) throws SQLException {
//        return rs.getInt(9);
//    }
//
//    public static String getResultInstrShortName(ResultSet rs) throws SQLException {
//        return rs.getString(10);
//    }


}
