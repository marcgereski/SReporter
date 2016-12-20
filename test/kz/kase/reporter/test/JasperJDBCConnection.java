package kz.kase.reporter.test;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanArrayDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JasperJDBCConnection {
    ArrayList dataBeanList = new ArrayList();
    public static void main(String[] args) throws FileNotFoundException, JRException {
        JasperJDBCConnection jas = new JasperJDBCConnection();
        jas.fill2();

    }
    public CustomBean setMethod() {
        CustomBean cb = new CustomBean();

        cb.setName("islam");
        cb.setCity("almaty");
        cb.setId(2);
        cb.setStreet("begaliev");
        return cb;
    }

    public void fill2() {
        try {
            InputStream inputStream = new FileInputStream("D:/testReport.jrxml");


            dataBeanList.add(setMethod());

            JRBeanCollectionDataSource beanColDataSource = new
                    JRBeanCollectionDataSource(dataBeanList);

            Map parameters = new HashMap();
            JasperDesign jasperDesign = JRXmlLoader.load(inputStream);
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
            JasperExportManager.exportReportToPdfFile(jasperPrint, "D:/test_jasper.pdf");
        } catch (JRException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}