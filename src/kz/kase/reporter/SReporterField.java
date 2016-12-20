package kz.kase.reporter;

import kz.kase.reporter.ui.MainFrame;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SReporterField {

    public static final String APP_DIR = System.getProperty("user.dir");
    private static final String SEP = File.separator;
    private static final String REPORTS = "reports";
    private static final Logger log = Logger.getLogger(SReporterField.class);
    private static String FORM_ORDER;
    private static String OUT_FILE = "Centras";
    String date;
    String fio;
    String idNumber;
    String idDate;
    String idMadeByWho;
    String firmName;
    String gostRegistration;
    String account;
    String nickTs;
    String orderType;
    String direction2;
    String emitent;
    String instrNin;
    String instrType;
    String qty;
    String price;
    String volume;
    String expired;
    String clientSign;
    private JasperPrint jasperPrint;
    private Map<String, Object> params;
    private String dateToFile;

    public static void setOutFile(String fileName) {
        OUT_FILE = fileName;
        File dir = new File(REPORTS + SEP + OUT_FILE);
        dir.mkdir();
    }

    public void exportToPDF(SReporterField sReporterField) {
        try {
            fillReport(sReporterField);
            JRPdfExporter pdfExporter = new JRPdfExporter();

            pdfExporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            String fileName = REPORTS + SEP + OUT_FILE + SEP + OUT_FILE + " " + dateToFile + ".pdf";
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

    public void fillReport(SReporterField sReporterField) {
        try {
            InputStream inputStream = new FileInputStream(FORM_ORDER);
            ArrayList dataBeanList = new ArrayList();
            dataBeanList.add(sReporterField);

            JRBeanCollectionDataSource beanColDataSource = new
                    JRBeanCollectionDataSource(dataBeanList);

            Map parameters = new HashMap();
            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);

        } catch (JRException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public String getFormOrder() {
        return FORM_ORDER;
    }

    public void setFormOrder(String form) {
        FORM_ORDER = form;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        dateToFile = date.replaceAll(":", "");
        this.date = date;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getIdDate() {
        return idDate;
    }

    public void setIdDate(String idDate) {
        this.idDate = idDate;
    }

    public String getIdMadeByWho() {
        return idMadeByWho;
    }

    public void setIdMadeByWho(String idMadeByWho) {
        this.idMadeByWho = idMadeByWho;
    }

    public String getFirmName() {
        return firmName;
    }

    public void setFirmName(String firmName) {
        this.firmName = firmName;
    }

    public String getGostRegistration() {
        return gostRegistration;
    }

    public void setGostRegistration(String gostRegistration) {
        this.gostRegistration = gostRegistration;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNickTs() {
        return nickTs;
    }

    public void setNickTs(String nickTs) {
        this.nickTs = nickTs;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getDirection2() {
        return direction2;
    }

    public void setDirection2(int dir) {
        if (dir == 0) {
            this.direction2 = "покупка";
        }
        if (dir == 1) {
            this.direction2 = "продажа";
        }
    }

    public String getEmitent() {
        return emitent;
    }

    public void setEmitent(String emitent) {
        this.emitent = emitent;
    }

    public String getInstrNin() {
        return instrNin;
    }

    public void setInstrNin(String instrNin) {
        this.instrNin = instrNin;
    }

    public String getInstrType() {
        return instrType;
    }

    public void setInstrType(String instrType) {
        this.instrType = instrType;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public String getClientSign() {
        return clientSign;
    }

    public void setClientSign(String clientSign) {
        this.clientSign = clientSign;
    }

}
