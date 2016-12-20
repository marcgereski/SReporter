package kz.kase.reporter.xml;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class XmlParser {

    static private final String SEP = File.separator;
    static private final String BROKER_BASE_PATH = System.getProperty("user.dir") + SEP + "base" + SEP + "brokerbase.xml";
    static private final String EMITENTS_BASE_PATH = System.getProperty("user.dir") + SEP + "base" + SEP + "emitents.xml";

    static final String NAME = "name";
    static final String NICK_TS = "nick_ts";
    static final String FIO = "fio";
    static final String ID = "id_number";
    static final String ID_DATE = "id_date";
    static final String ID_MADE = "id_made_by_who";
    static final String DATA = "data";
    static final String SN = "sn";
    static final String EMITENT = "emitent";
    static final String ORGANIZATION = "organization";

    private static boolean initiated = false;

    private static String tempBroker;
    private static String tempClient;

    private static Element brokersRootElement;
    private static Element emitentsRootElement;

    static {
        SAXBuilder sax = new SAXBuilder();
        try {
            Document document = sax.build(new File(BROKER_BASE_PATH));
            brokersRootElement = document.getRootElement();
            Document document2 = sax.build(new File(EMITENTS_BASE_PATH));
            emitentsRootElement = document2.getRootElement();
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //todo enter by Cert DN or else
    public XmlParser() {


    }

    @SuppressWarnings("unchecked")
    public static Element getBroker(String brokerName) {
        List<Element> children = brokersRootElement.getChildren();
        if (children == null || children.size() == 0) return null;
        else {
            for (Element element : children) {
                if (brokerName.equalsIgnoreCase(element.getAttributeValue(NAME))) {
                    return element;
                }

            }
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Element> getEmitents() {
        return emitentsRootElement.getChildren();
    }

    @SuppressWarnings("unchecked")
    public static String getEmitent(String instrumetnCode) {
        List<Element> codes = emitentsRootElement.getChildren();
        for (Element cod : codes) {
            if (instrumetnCode.contains(cod.getAttributeValue(NAME))) {
                return cod.getAttributeValue(EMITENT);
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static List<Element> getClients(String brokerName) {
        Element broker = getBroker(brokerName);
        List<Element> children = broker.getChildren();
        if (children == null || children.size() == 0) return null;
        else {
            return children;
        }

    }

    @SuppressWarnings("unchecked")
    public static List<String> getClientsNicks(String brokerName) {
        List<String> names = null;
        try {

            Element broker = getBroker(brokerName);
            List<Element> children = broker.getChildren();
            if (children == null || children.size() == 0) return null;
            names = new ArrayList<String>();
            for (Element element : children) {
                names.add(element.getAttributeValue(NICK_TS));
            }
            Collections.sort(names);
        } catch (NullPointerException ne) {
            ne.printStackTrace();
            JDialog jd = new JOptionPane().createDialog("No Firm like " + brokerName);
            jd.setVisible(true);
        }
        return names;
    }

    public static Element getClient(String brokerName, String clientName) {
        List<Element> clients = getClients(brokerName);
        if (clients == null || clients.size() == 0) return null;
        else {
            for (Element element : clients) {
                if (clientName.equalsIgnoreCase(element.getAttributeValue(NICK_TS))) {
                    return element;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static String getAttrDataValue(String nameBroker, String clientName, String attr) {
        Element client = getClient(nameBroker, clientName);
        List<Element> children = client.getChildren();
        if (children == null || children.size() == 0) return null;
        for (Element element : children) {
            if (element.getName().equalsIgnoreCase(attr)) {
                return element.getAttributeValue(DATA);
            }
        }
        return null;

    }

    public static void initData(String brok, String client) {
        tempBroker = brok;
        tempClient = client;
        initiated = true;
    }

    public static String getOrganization() throws Exception {
        if (initiated)
            return getAttrDataValue(tempBroker, tempClient, ORGANIZATION);
        else throw new Exception("Data: broker name and client name Not initiated");
    }

    public static String getSN() throws Exception {
        if (initiated)
            return getAttrDataValue(tempBroker, tempClient, SN);
        else throw new Exception("Data: broker name and client name Not initiated");
    }

    public static String getFio() throws Exception {
        if (initiated)
            return getAttrDataValue(tempBroker, tempClient, FIO);
        else throw new Exception("Data: broker name and client name Not initiated");
    }

    public static String getId() throws Exception {
        if (initiated)
            return getAttrDataValue(tempBroker, tempClient, ID);
        else throw new Exception("Data: broker name and client name Not initiated");
    }

    public static String getIdDate() throws Exception {
        if (initiated)
            return getAttrDataValue(tempBroker, tempClient, ID_DATE);
        else throw new Exception("Data: broker name and client name Not initiated");
    }

    public static String getIdMade() throws Exception {
        if (initiated)
            return getAttrDataValue(tempBroker, tempClient, ID_MADE);
        else throw new Exception("Data: broker name and client name Not initiated");
    }

    public static String getUserNick() throws Exception {
        if (initiated) {
            Element client = getClient(tempBroker, tempClient);
            return client.getAttributeValue(NICK_TS);
        } else throw new Exception("Data: broker name and client name Not initiated");
    }

//    public static void main(String[] args) {
//        try {
//            XmlParser xmlp = new XmlParser();
//            xmlp.initData("Centras", "CE090");
//            System.out.println(xmlp.getFio());
//            System.out.println(xmlp.getId());
//            System.out.println(xmlp.getIdDate());
//            System.out.println(xmlp.getIdMade());
//
//            System.out.println(xmlp.getClientsNicks("Centras"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }

}
