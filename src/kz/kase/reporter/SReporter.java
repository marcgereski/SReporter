package kz.kase.reporter;


import kz.kase.reporter.oracle.QueryHolder;
import kz.kase.reporter.ui.MainFrame;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SReporter {

    private static final String SEP = File.separator;
    private static String FORM_ORDER;
    private static final String REPORTS = "reports";
    private static String OUT_FILE = "Centras";
    public static final String APP_DIR = System.getProperty("user.dir");
    private JasperPrint jasperPrint;


    static final String FIO = "fio";
    static final String ID = "id_number";
    static final String ID_DATE = "id_date";
    static final String ID_MADE = "id_made_by_who";
    static final String FIRM_NAME = "firm_name";
    static final String GOST_REGSTRATION = "gost_registration";
    static final String ACC = "account";
    static final String NICK = "nick_ts";
    static final String ORDER_TYPE = "order_type";
    static final String DIRECT = "direction";
    static final String EMITENT = "emitent";
    static final String NIN = "instr_nin";
    static final String INSTR_TYPE = "instr_type";
    static final String QTY = "qty";
    static final String VOLUME = "volume";
    static final String PRICE = "price";
    static final String CLI_SIGN = "client_sign";
    static final String BROK_SIGN = "broker_sign";
    static final String DATE = "date";
    static final String ACCEPT_DATE = "accept_date";
    static final String ACCEPT_TIME = "accept_time";
    static final String BROKER_NAME = "broker_name";
    static final String TRADER_NAME = "trader_name";
    static final String INTERNAL_NUMBER = "internal_number";
    static final String DIRECT_2 = "direction_2";
    static final String EXPIRED = "expir_date";
    static final String SERIAL = "serial";
    static final String ORDER_NUMBER = "order_number";
    static final String REF = "ref";
    static final String INSTR_CODE = "instr_code";


    public Map<String, Object> params;

    private String dateToFile;
    private static final Logger log = Logger.getLogger(SReporter.class);

    public SReporter() {
        params = new HashMap<String, Object>();
    }


    public void exportToPDF() {
        try {
            StatusSelector.getInstance().setStatus(MainFrame.REPOR_EXECUTING);
            fillReport();
            StatusSelector.getInstance().setStatus(MainFrame.EXPORT_PDF);
            JRPdfExporter pdfExporter = new JRPdfExporter();

            pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            String fileName = APP_DIR + SEP + REPORTS + SEP + OUT_FILE + SEP + params.get(INTERNAL_NUMBER) + "_" + OUT_FILE + "_" + dateToFile + ".pdf";
            File reportFile = new File(fileName);

            File reportsDir = new File(APP_DIR + SEP + REPORTS);

            if (!reportsDir.exists()) {
                boolean b = reportsDir.mkdir();
                if (!b) {
                    log.error("Could not create folder reports");
                    return;
                }
            }

            if (reportFile.exists()) {
                log.info("report alredy exist" + reportFile.getAbsolutePath());
                return;
            }
            pdfExporter.setParameter(JRExporterParameter.OUTPUT_FILE, reportFile);

            log.info("Exporting report " + OUT_FILE + " " + dateToFile + "...");
            pdfExporter.exportReport();
            log.info("Done!");
            StatusSelector.getInstance().setStatus(MainFrame.DONE);
        } catch (JRException e) {
            e.printStackTrace();
        }
    }

    public static void setOutFile(String fileName) {
        OUT_FILE = fileName;
        File dir = new File(REPORTS + SEP + OUT_FILE);
        dir.mkdir();
    }

    private void addParametr(String parametrName, String value) {
        params.put(parametrName, value);
    }


    public void addDate(String date) {
//        DateTS dateTS = new DateTS(date);
        dateToFile = date.replaceAll(":", "");
        params.put(DATE, date);
    }

    public void addFio(String fio) {
        if (fio == null) fio = " ";
        params.put(FIO, fio);
    }

    public void addId(String id) {
        if (id == null) id = " ";
        params.put(ID, id);
    }

    public void addIdDate(String iddate) {
        if (iddate == null) iddate = " ";
        params.put(ID_DATE, iddate);
    }

    public void addIdMade(String idMade) {
        if (idMade == null) idMade = " ";
        params.put(ID_MADE, idMade);
    }

    public void addFirmName(String firm) {
        if (firm == null || firm.equals("null")) firm = " ";
        params.put(FIRM_NAME, firm);
    }

    public void addNick(String nick) {
        if (nick == null) nick = " ";
        params.put(NICK, nick);
    }

    public void addEmitenet(String emitent) {
        if (emitent == null) {
            emitent = " ";
        }
        params.put(EMITENT, emitent);
    }

    public void addQty(String qty) {
        if (qty == null) qty = " ";
        params.put(QTY, qty);
    }

    public void addPrice(String price) {
        if (price == null) price = " ";
        params.put(PRICE, price);
    }

    public void addVolune(String vol) {
        if (vol == null) vol = " ";
        params.put(VOLUME, vol);
    }

    public void addOrderType(String type) {
        if (type == null) type = " ";
        params.put(ORDER_TYPE, type);
    }

    public void addInstrType(String iType) {
        if (iType == null) iType = " ";
        params.put(INSTR_TYPE, iType);
    }

    public void addAccount(String acc) {
        if (acc == null) acc = " ";
        params.put(ACC, acc);
    }

    public void addClientSign(String sign) {
        if (sign == null) sign = " ";
        params.put(CLI_SIGN, sign);
    }

    public void addNin(String nin) {
        if (nin == null) nin = " ";
        params.put(NIN, nin);
    }

    public void addGostReg(String gReg) {
        if (gReg == null) gReg = " ";
        params.put(GOST_REGSTRATION, gReg);
    }

    public void addDirection(int dir) {
        if (dir == 0) {
            params.put(DIRECT, "покупка");
            params.put(DIRECT_2, "Покупка");
        }
        if (dir == 1) {
            params.put(DIRECT, "продажа");
            params.put(DIRECT_2, "Продажа");
        }
    }

    public void addExpiredDate(String date) {
        if (date == null) date = " ";
        params.put(EXPIRED, date);
    }

    public void addSerial(String serial) {
        if (serial == null) serial = " ";
        params.put(SERIAL, serial);
    }

    public void addOrderNumber(String order) {
        if (order == null) order = " ";
        params.put(ORDER_NUMBER, order);
    }

    public void addRef(String ref) {
        if (ref == null) ref = " ";
        params.put(REF, ref);
    }

    public void addInstrCode(String instrCode) {
        if (instrCode == null) instrCode = " ";
        params.put(INSTR_CODE, instrCode);
    }
    @SuppressWarnings("unchecked")
    public void fillReport() throws JRException {
        JasperReport jaspReport = JasperCompileManager.compileReport(FORM_ORDER);
        jasperPrint = JasperFillManager.fillReport(jaspReport,
                params, new JREmptyDataSource());
    }

    public void setFormOrder(String form) {
        FORM_ORDER = form;
    }

    public String getFormOrder() {
        return FORM_ORDER;
    }


//    public static void main(String[] args) {
//        SReporter srep = new SReporter();
//        srep.fillReport();
//        srep.exportToPDF();
//    }
    private HSSFWorkbook hwb = new HSSFWorkbook();

    public HSSFSheet createExcelDoc() {
        HSSFSheet sheet = hwb.createSheet("new sheet");
        HSSFRow rowhead = sheet.createRow((short) 0);
        rowhead.createCell((short) 0).setCellValue("№");
        rowhead.createCell((short) 1).setCellValue("№ транзитного приказа");
        rowhead.createCell((short) 2).setCellValue("№ заявки");
        rowhead.createCell((short) 3).setCellValue("Направление");
        rowhead.createCell((short) 4).setCellValue("Наименование инструмента");
        rowhead.createCell((short) 5).setCellValue("НИН инструмента");
        rowhead.createCell((short) 6).setCellValue("Количество");
        rowhead.createCell((short) 7).setCellValue("Цена");
        rowhead.createCell((short) 8).setCellValue("Объем");
        rowhead.createCell((short) 9).setCellValue("Наименование залогового инструмента (если применимо)");
        rowhead.createCell((short) 10).setCellValue("НИН залогового инструмента (если применимо)");
        rowhead.createCell((short) 11).setCellValue("Количество залогового инструмента (если применимо)");
        rowhead.createCell((short) 12).setCellValue("Пользователь, подписавший приказ");
        rowhead.createCell((short) 13).setCellValue("Дата подачи");
        rowhead.createCell((short) 14).setCellValue("Время подачи");
        rowhead.createCell((short) 15).setCellValue("Дата истечения");
        rowhead.createCell((short) 16).setCellValue("Время истечения");
        rowhead.createCell((short) 17).setCellValue("Статус приказа");
        rowhead.createCell((short) 18).setCellValue("Ref Number");
        return sheet;
    }
    public void saveToExcel(HSSFSheet sheet, String userName, int number) {

        try {
            log.info(QueryHolder.getClientStatQuery(userName));
            HSSFRow row = sheet.createRow((short) number);
            row.createCell((short) 0).setCellValue(number);
            row.createCell((short) 1).setCellValue((String) params.get(SERIAL));
            row.createCell((short) 2).setCellValue((String) params.get(ORDER_NUMBER));
            row.createCell((short) 3).setCellValue((String) params.get(DIRECT_2));
            row.createCell((short) 4).setCellValue((String) params.get(INSTR_CODE));
            row.createCell((short) 5).setCellValue((String) params.get(NIN));
            row.createCell((short) 6).setCellValue((String) params.get(QTY));
            row.createCell((short) 7).setCellValue((String) params.get(PRICE));
            row.createCell((short) 8).setCellValue((String) params.get(VOLUME));
            row.createCell((short) 9).setCellValue("");
            row.createCell((short) 10).setCellValue("");
            row.createCell((short) 11).setCellValue("");
            row.createCell((short) 12).setCellValue((String) params.get(NICK));

            Date createDate = new SimpleDateFormat("dd.MM.yyyy").parse((String) params.get(DATE));
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            String date = dateFormat.format(createDate);
            //Date createTime = new SimpleDateFormat("HH:mm:SS").parse();//createDate.getHours();
            /*if (hour < 7) {
                hour = hour + 12;
            }
            createDate.setHours(hour);*/
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:SS");
            String time = (String) params.get(ACCEPT_TIME);

            row.createCell((short) 13).setCellValue(date);
            row.createCell((short) 14).setCellValue(time);

            String expireDateStr = (String) params.get(EXPIRED);
            String exDate = "";
            String exTime = "";
            if (!expireDateStr.equals(" ")) {
                Date expireDate = new SimpleDateFormat("dd.MM.yyyy").parse(expireDateStr);
                exDate = dateFormat.format(expireDate);
                int hour2 = expireDate.getHours();
                if (hour2 < 7) {
                    hour2 = hour2 + 12;
                }
                if (hour2 == 12) {
                    hour2 = 0;
                }
                expireDate.setHours(hour2);
                exTime = timeFormat.format(expireDate);
            }
            row.createCell((short) 15).setCellValue(exDate);
            row.createCell((short) 16).setCellValue(exTime);
            row.createCell((short) 17).setCellValue("");
            row.createCell((short) 18).setCellValue((String) params.get(REF));
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
     public void closeFile(String userName) {
         try {
             String USER_DIR = System.getProperty("user.dir");
             String filename = USER_DIR + File.separator + "reports" + File.separator + "report_" + userName + ".xls";
             FileOutputStream fileOut = new FileOutputStream(filename);
             hwb.write(fileOut);
             fileOut.close();
             System.out.println("Your excel file has been generated!");
         } catch (IOException e) {
             e.printStackTrace();
         }

     }

    public void addAcceptDate(String date) {
        params.put(ACCEPT_DATE, date);
    }

    public void addAcceptTime(String time) {
        params.put(ACCEPT_TIME, time);
    }

    public void addBrokerName(String name) {
        params.put(BROKER_NAME, name);
    }

    public void addTraderName(String name) {
        params.put(TRADER_NAME, name);
    }

    public void addInternalNum(String number) {
        params.put(INTERNAL_NUMBER, number);

    }
}
