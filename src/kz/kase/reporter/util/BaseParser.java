package kz.kase.reporter.util;

import kz.kase.reporter.xml.XmlParser;
import org.jdom.Element;

import java.util.List;

public class BaseParser {


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
    //columns
    static final String USERNAME = "username";
    static final String FULL_NAME = "full_name";
    static final String ID_NUMBER = "id_number";
    static final String ID_ISSUE_DATE = "id_issue_date";
    static final String ID_ISSUE_BY = "id_issued_by";
    static final String CERT_SERIAL = "cert_serial";
    static final String INSERT_ACT = "insert into";
    private String tableName;
    private String organization;

    public BaseParser() {
        this("user_info", "Centras");
    }


    public BaseParser(String tableName, String organization) {
        this.tableName = tableName;
        this.organization = organization;

    }

    public static void main(String[] args) {
        BaseParser parser = new BaseParser();
        System.out.println(parser.parseUsers());
//        System.out.println(parser.parseEmitents());
    }

    public String parseUsers() {
        StringBuilder result = new StringBuilder();
        List<Element> clients = XmlParser.getClients(organization);
        if (clients != null) {
            for (Element c : clients) {
                String nick = c.getAttributeValue(NICK_TS);
                String fullNAme = c.getChild(FIO).getAttributeValue(DATA);
                String id = c.getChild(ID).getAttributeValue(DATA);
                String idDate = c.getChild(ID_DATE).getAttributeValue(DATA);
                String idIssue = c.getChild(ID_MADE).getAttributeValue(DATA);
                String serial = c.getChild(SN).getAttributeValue(DATA);

                result.append(INSERT_ACT + " ").
                        append(tableName).
                        append(" (" + USERNAME + "," + FULL_NAME + "," +
                                ID_NUMBER + "," + ID_ISSUE_DATE + "," +
                                ID_ISSUE_BY + "," + CERT_SERIAL + ") ").
                        append(" values ").
                        append("(").
                        append("'").append(nick).append("'").
                        append(",").append("'").append(fullNAme).append("'").
                        append(",").append("'").append(id).append("'").
                        append(",").
                        append("to_date(").
                        append("'").append(idDate).append("'").
                        append(",").
                        append("'dd.mm.yyyy')").
                        append(",").append("'").append(idIssue).append("'").
                        append(",").append("'").append(serial).append("'").
                        append(")").
                        append(";").
                        append("\n");


            }
        }
        return result.toString();
    }

    public String parseEmitents() {
        StringBuilder result = new StringBuilder();
        List<Element> emitents = XmlParser.getEmitents();
        if (emitents != null) {
            result.append("INSERT ALL");
            for (Element em : emitents) {
                String instrName = em.getAttributeValue("name");
                String emitent = em.getAttributeValue("emitent");
                emitent = emitent.replaceAll("'", "");
                result.append(" INTO ").
                        append("emitents").
                        append(" (instr_code,emitent)").
                        append(" values ").
                        append("(").
                        append("'").append(instrName).append("'").append(",").
                        append("'").append(emitent).append("'").
                        append(")").
                        append("\n");
            }
            result.append(";");
        }
        return result.toString();
    }

}
