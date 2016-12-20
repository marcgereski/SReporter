package kz.kase.reporter;

import kz.kase.reporter.oracle.OracleConnector;
import kz.kase.reporter.oracle.QueryHolder;
import kz.kase.reporter.util.FormatUtil;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;

public class ReportGenerator {

    static private final String SEP = File.separator;
    static private final String APP = System.getProperty("user.dir");
    static private final String LOG_SETTINGS_FILE = APP + SEP + "log4j.xml";
    static private final String FORMS_PATH = APP + SEP + "forms";
    private static final Logger log = Logger.getLogger(ReportGenerator.class);
    SReporter sreporter;
    private OracleConnector transitStatBase;

    private String jasperForm = "Evraz.jrxml";
    private String jasperFormObl = "Resmi_oblig.jrxml";

    public ReportGenerator(String jasperForm, String jasperFormObl) {
        this.jasperForm = jasperForm;
        this.jasperFormObl = jasperFormObl;
        sreporter = new SReporter();
        transitStatBase = new OracleConnector(OracleConnector.TS);
        log.info("ReportGenerator started");
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        DOMConfigurator.configure(LOG_SETTINGS_FILE);

        ReportGenerator rp = new ReportGenerator(Configuration.getInstance().getRegularForm(), Configuration.getInstance().getObligationForm());
        String users = Configuration.getInstance().getUsers();
        if (users.equals("all")) {
            rp.getAllUserName();
        } else {
            rp.createReports(users);
        }
        System.err.println("Filling time : " + (System.currentTimeMillis() - start));
    }

    public void getAllUserName() {
        try {
            Connection allUserConn = transitStatBase.getConnection();
            Statement allUserStat = allUserConn.createStatement();
            allUserStat.executeQuery(QueryHolder.getAllUsersQuery());
            ResultSet resultSet = allUserStat.getResultSet();
            while (resultSet.next()) {
                createReports(resultSet.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createReports(String userName) {

        Connection stradeBaseConn;
        Statement transStatment;
        Statement firmStatment;
        Statement emitentStatment;
        Statement instrStatement;
        Statement accountStatment;
        Statement brokerStatement;

        try {
            stradeBaseConn = transitStatBase.getConnection();
            transStatment = stradeBaseConn.createStatement();
            firmStatment = stradeBaseConn.createStatement();
            emitentStatment = stradeBaseConn.createStatement();
            instrStatement = stradeBaseConn.createStatement();
            accountStatment = stradeBaseConn.createStatement();
            brokerStatement = stradeBaseConn.createStatement();

            log.info(QueryHolder.getLikeClientStatQuery(userName));
            transStatment.executeQuery(QueryHolder.getLikeClientStatQuery(userName));
            ResultSet rs = transStatment.getResultSet();
            int number = 1;
            int xls_number = 1;
            String old_nick = "";
            //HSSFSheet sheet = sreporter.createExcelDoc();
            while (rs.next()) {
                String nick = rs.getString("username");
                int firmId = rs.getInt("firm_id");
                if (!nick.equals(old_nick)) {
                    number = 1;
                    old_nick = nick;
                }

                log.info(QueryHolder.getFirmByIdQuery(firmId));
                firmStatment.executeQuery(QueryHolder.getFirmByIdQuery(firmId));
                ResultSet firmRes = firmStatment.getResultSet();
                if (firmRes.next()) {
                    sreporter.addFirmName(firmRes.getString("name"));
                    sreporter.addGostReg(firmRes.getString("reg_num") +
                            ", выдано от " +
                            correctDate(firmRes.getString("id_issue_date")) + " " +
                            firmRes.getString("id_issue_by") +
                            " г. " + firmRes.getString("id_issue_town"));
                } else {
                    sreporter.addFirmName(null);
                    sreporter.addGostReg(null);
                }
                sreporter.addFio(rs.getString("full_name"));
                sreporter.addId(rs.getString("id_number"));
                sreporter.addIdDate(correctDate(delLastZeroFromDate(rs.getString("id_issue_date"))));
                sreporter.addIdMade(rs.getString("id_issued_by"));

                accountStatment.executeQuery(QueryHolder.selectAccountCodeStr(rs.getString("account")));
                ResultSet accRes = accountStatment.getResultSet();

                if (accRes.next()) {
                    sreporter.addAccount(accRes.getString("name"));
                } else {
                    sreporter.addAccount(null);
                }
                String sign = rs.getString("sign");
                if (sign != null) sign.replace("\n", "");
                sreporter.addClientSign(sign);
                sreporter.addDate(extractDate(rs.getString("created")));
                sreporter.addAcceptDate(extractDate(rs.getString("created")));
                sreporter.addAcceptTime(extractTime(rs.getString("created")));
                sreporter.addDirection(rs.getInt("direct"));
                String expired = extractDate(rs.getString("expired"));
                if ((expired == null) || expired.equalsIgnoreCase("")){
                    expired = "1 день";
                }
                sreporter.addExpiredDate(expired);
//                sreporter.addInternalNum(/*number*/rs.getInt("id"));
                sreporter.addInternalNum(String.valueOf(""));

                log.info(QueryHolder.getLikeClientsConfirmer(rs.getInt("id")));
                brokerStatement.executeQuery(QueryHolder.getLikeClientsConfirmer(rs.getInt("id")));
                ResultSet brokerRes = brokerStatement.getResultSet();
                if (brokerRes.next()) {
                    String trader_name = brokerRes.getString("name");
                    if (trader_name == null) trader_name = Configuration.getInstance().getBrokerName();
                    sreporter.addBrokerName(trader_name);
                    sreporter.addTraderName(trader_name);
                }



                instrStatement.executeQuery(QueryHolder.selectInstrDataFromStrTab(rs.getString("instrid")));
                ResultSet instrRes = instrStatement.getResultSet();
                String instrType = "";

                if (instrRes.next()) {
                    sreporter.addNin(instrRes.getString("nin"));
                    instrType = instrRes.getString("instr_type");
                    sreporter.addInstrType(instrType);

                    String instrName = instrRes.getString("code");
                    sreporter.addInstrCode(instrName);
                    log.info(QueryHolder.emitentsQuery(instrName));
                    emitentStatment.executeQuery(QueryHolder.emitentsQuery(instrName));
                    ResultSet emitentRes = emitentStatment.getResultSet();
                    if (emitentRes.next()) {
                        sreporter.addEmitenet(emitentRes.getString("emitent"));
                    } else {
                        sreporter.addEmitenet(null);

                    }
                } else {
                    sreporter.addNin(null);
                    sreporter.addEmitenet(null);
                    sreporter.addInstrType(null);
                }

                sreporter.addNick(nick);

                double price = rs.getDouble("price");
                int qty = rs.getInt("quantity");
                double vol = rs.getDouble("volume");
                String strPrice = FormatUtil.format(price, 2);
                String strQty = FormatUtil.format(qty, 2);
                String strVol = FormatUtil.format(vol, 2);

                sreporter.addPrice(strPrice);
                sreporter.addQty(strQty);
                if ("Облигация".equals(instrType.trim())/* && strVol.equals(null)*/) {
                    sreporter.addVolune(null);
                    sreporter.addOrderType("Условная заявка");
                    sreporter.setFormOrder(FORMS_PATH + SEP + jasperFormObl);

                } else {
                    sreporter.addVolune(strVol);
                    sreporter.addOrderType("Лимитированная заявка");
                    sreporter.setFormOrder(FORMS_PATH + SEP + jasperForm);
                }

                sreporter.addSerial(rs.getString("serial"));
                sreporter.addOrderNumber(rs.getString("orderserial"));
                sreporter.addRef(rs.getString("refnum"));
                SReporter.setOutFile(nick);
                sreporter.exportToPDF();
                //sreporter.saveToExcel(sheet, nick, xls_number);
                number++;
                xls_number++;
            }
            //sreporter.closeFile(userName);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private String extractTime(String date) {
        if (date == null) return null;
        return  date.substring(10,16);
    }

    private String correctDate(String date) {
        if (date == null) return null;
        return date.replace("00:00:00", "");
    }

    private String extractDate(String date) {
        if (date == null) return null;
        String result = date.substring(8,10) + "." + date.substring(5,7) + "." + date.substring(0,4);
        return  result;
    }

    private String delLastZeroFromDate(String date) {
        if (date == null) return null;
        return date.replace(".0", "");
    }

    public void saveToExcel(String userName) {
        String USER_DIR = System.getProperty("user.dir");
        String filename = USER_DIR + File.separator + "transit_stat.xls";
        HSSFWorkbook hwb = new HSSFWorkbook();
        HSSFSheet sheet = hwb.createSheet("new sheet");

        HSSFRow rowhead = sheet.createRow((short) 0);
        rowhead.createCell((short) 0).setCellValue("ID");
        rowhead.createCell((short) 1).setCellValue("USERNAME");
        rowhead.createCell((short) 2).setCellValue("ACCOUNT");
        rowhead.createCell((short) 3).setCellValue("INSTRID");
        rowhead.createCell((short) 4).setCellValue("SERIAL");
        rowhead.createCell((short) 5).setCellValue("ORDERSERIAL");
        rowhead.createCell((short) 6).setCellValue("DIRECT");
        rowhead.createCell((short) 7).setCellValue("PRICE");
        rowhead.createCell((short) 8).setCellValue("QUANTITY");
        rowhead.createCell((short) 9).setCellValue("REMAINDER");
        rowhead.createCell((short) 10).setCellValue("VOLUME");
        rowhead.createCell((short) 11).setCellValue("STATUS");
        rowhead.createCell((short) 12).setCellValue("CREATED");
        rowhead.createCell((short) 13).setCellValue("EXPIRED");
        rowhead.createCell((short) 14).setCellValue("DATA");
        rowhead.createCell((short) 15).setCellValue("SIGN");
        rowhead.createCell((short) 16).setCellValue("REFNUM");

        Connection stradeBaseConn;
        Statement transStatment;
        try {
            stradeBaseConn = transitStatBase.getConnection();
            transStatment = stradeBaseConn.createStatement();

            log.info(QueryHolder.getClientStatQuery(userName));
            transStatment.executeQuery(QueryHolder.getClientStatQuery(userName));
            ResultSet rs = null;
            rs = transStatment.getResultSet();
            int i = 1;
            while (rs.next()) {
                HSSFRow row = sheet.createRow((short) i);
                row.createCell((short) 0).setCellValue(rs.getString("id"));
                row.createCell((short) 1).setCellValue(rs.getString("username"));
                row.createCell((short) 2).setCellValue(rs.getString("ACCOUNT"));
                row.createCell((short) 3).setCellValue(rs.getString("INSTRID"));
                row.createCell((short) 4).setCellValue(rs.getString("SERIAL"));
                row.createCell((short) 5).setCellValue(rs.getString("ORDERSERIAL"));
                row.createCell((short) 6).setCellValue(rs.getString("DIRECT"));
                row.createCell((short) 7).setCellValue(rs.getString("PRICE"));
                row.createCell((short) 8).setCellValue(rs.getString("QUANTITY"));
                row.createCell((short) 9).setCellValue(rs.getString("REMAINDER"));
                row.createCell((short) 10).setCellValue(rs.getString("VOLUME"));
                row.createCell((short) 11).setCellValue(rs.getString("STATUS"));
                row.createCell((short) 12).setCellValue(rs.getString("CREATED"));
                row.createCell((short) 13).setCellValue(rs.getString("EXPIRED"));
                row.createCell((short) 14).setCellValue(rs.getString("DATA"));
                row.createCell((short) 15).setCellValue(rs.getString("SIGN"));
                row.createCell((short) 16).setCellValue(rs.getString("REFNUM"));

                i++;
            }

            FileOutputStream fileOut = new FileOutputStream(filename);
            hwb.write(fileOut);
            fileOut.close();
            System.out.println("Your excel file has been generated!");

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

}
