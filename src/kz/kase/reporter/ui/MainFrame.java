package kz.kase.reporter.ui;

import kz.kase.reporter.*;
import kz.kase.reporter.StatusReporter;
import kz.kase.reporter.oracle.ConnectionFactory;
import kz.kase.reporter.oracle.OracleConnector;
import kz.kase.reporter.oracle.QueryHolder;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainFrame extends JFrame implements StatusReporter {
    static private final String SEP = File.separator;
    static private final String FORMS_PATH = System.getProperty("user.dir") + SEP + "forms";
    static private String[] FILE_LIST;

    SReporter sreporter;
    static private String FIRM;

    //Statuses=============================================================================================
    static public final String DATA_BASE_CONNECTING = "Идет Подключение к базе данных Торговой системы";
    static public final String BASE_CONNECTED = "Подключение к базе данных произведено";
    static public final String BASE_CONNECT_FAIL = "Ошибка подключения к БД";
    static public final String QUERY_EXECUTING = "Выполняется запрос";
    static public final String REPOR_EXECUTING = "Формирование отчета";
    static public final String EXPORT_PDF = "Экспорт в PDF";
    static public final String DONE = "Выполнено";
    //=====================================================================================================

    final JComboBox formSelecter;
    //    final JComboBox userSelecter;
    private static JLabel statusLabel;

    private OracleConnector transitStatBase;
    private OracleConnector testBase;
    private OracleConnector emitentsBase;

    public MainFrame() {

        transitStatBase = new OracleConnector(OracleConnector.TS);
        testBase = ConnectionFactory.getProdBaseConnector();

        getContentPane().setLayout(new MigLayout("", "5[grow,fill]5", "10[]10[]10"));
        setSize(new Dimension(400, 250));
        setResizable(false);

        sreporter = new SReporter();

        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        Point myCenter = new Point((int) center.getX() - getWidth() / 2,
                (int) center.getY() - getHeight() / 2);
        setLocation(myCenter);

        JLabel formLabel = new JLabel("Выбирите форму для отчета");
        formSelecter = new JComboBox(FILE_LIST);

//        JLabel userLabel = new JLabel("Выбирите пользователя в торгорой системе");
//        userSelecter = new JComboBox(XmlParser.getClientsNicks(GlobalVars.getFirm()).toArray());

//        JLabel dateLabel = new JLabel("Выбирете интервал дат");
//
//        JTextField dateFrom = new JTextField();
//
//        JTextField

        final JButton buttonAccept = new JButton("Отчёт");

        buttonAccept.addActionListener(new ReportStatFiller());

        JButton buttonExit = new JButton("Выход");
        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //todo make data Bases close
                System.exit(0);
            }
        });

        statusLabel = new JLabel("Статус: ");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        add(formLabel, "span 2,wrap");
        add(formSelecter, "w 170!,wrap");
//        add(userLabel, "span 2,wrap");
//        add(userSelecter, "w 170!,wrap");
//        add(dateLabel, "span 2 ,wrap");
        add(buttonAccept, "w 100!,split 2");
        add(buttonExit, "align left, w 100!,wrap");
        add(statusLabel);

        setVisible(true);
    }

    public void setStatus(final String status) {
        statusLabel.setText("Статус: " + status);
        statusLabel.repaint();
    }

    public String getStatus() {
        return statusLabel.getText();
    }


//    public static void main(String[] args) {
//        MainFrame mf = new MainFrame();
//
//    }

    static {
        File f = new File(FORMS_PATH);
        FILE_LIST = f.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                if (name.endsWith(".jrxml")) {
                    return true;
                }
                return false;
            }
        });
    }

    public MainFrame getInstance() {
        return this;
    }

    //Отчеты для всех клиентов сразу
    private class ReportStatFiller implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            Connection statBaseConn = null;
            Connection prodBaseConn = null;
            Connection emitetntsCon = null;

            Statement transStatment = null;
            Statement emitentStatment = null;
            Statement instrStatment = null;
            Statement instrTypeStatement = null;
            try {

                statBaseConn = transitStatBase.getConnection();
                transStatment = statBaseConn.createStatement();
                emitentStatment = statBaseConn.createStatement();

                transStatment.executeQuery(QueryHolder.getAllClientStatQuery());  //отчеты для всех клиентов сразу
                ResultSet rs = transStatment.getResultSet();

                prodBaseConn = transitStatBase.getConnection();
                instrStatment = prodBaseConn.createStatement();

                instrTypeStatement = prodBaseConn.createStatement();


//                XmlParser.initData(GlobalVars.getFirm(), (String) userSelecter.getSelectedItem());

                while (rs.next()) {
                    String nick = rs.getString("username");

                    sreporter.addFio(rs.getString("full_name"));
                    sreporter.addId(rs.getString("id_number"));
                    sreporter.addIdDate(rs.getString("id_issue_date").replace("00:00:00", ""));
                    sreporter.addIdMade(rs.getString("id_issued_by"));
                    sreporter.addAccount(rs.getString("account"));
                    sreporter.addClientSign(rs.getString("cert_serial"));
                    sreporter.addDate(rs.getString("created"));
                    sreporter.addDirection(rs.getInt("direct"));
                    sreporter.addExpiredDate(rs.getString("expired"));
                    sreporter.addFirmName(null);
                    sreporter.addGostReg(null);

                    String instrId = rs.getString("instrid");
                    instrStatment.executeQuery("select extcode, shortname,ownid " +
                            "from ts.currinstrument where id='" + instrId + "'");
                    ResultSet instrRes = instrStatment.getResultSet();
                    instrRes.next();

                    String ownId = instrRes.getString("ownid");
                    instrTypeStatement.executeQuery("select shortname from ts.sector where ssecsid " +
                            "in (select ownid from ts.subsector where inst_id =" + ownId + ")");
                    ResultSet typeResult = instrTypeStatement.getResultSet();
                    typeResult.next();

                    sreporter.addInstrType(QueryHolder.getResultInstrType(typeResult.getString("shortname")));
                    sreporter.addNick(nick);
                    sreporter.addNin(instrRes.getString("extcode"));
                    sreporter.addOrderType("Лимитированная заявка");
                    sreporter.addPrice(rs.getString("price"));
                    sreporter.addQty(rs.getString("quantity"));
                    sreporter.addVolune(rs.getString("volume"));

                    String instrName = instrRes.getString("shortname");

                    emitentStatment.executeQuery("select emitent from emitents where instr_code='" + instrName + "'");
                    ResultSet emitentRes = emitentStatment.getResultSet();
                    emitentRes.next();

                    sreporter.addEmitenet(emitentRes.getString(1));

                    SReporter.setOutFile(nick);
                    String path = (String) formSelecter.getSelectedItem();

                    sreporter.setFormOrder(FORMS_PATH + SEP + path);
                    sreporter.exportToPDF();
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    transStatment.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }


        }
    }


//    private class ReportFillListener implements ActionListener {
//
//        public void actionPerformed(ActionEvent e) {
//            Connection connect = null;
//            Statement statment = null;
//            try {
//                sreporter.resetID();
//                OracleConnector oc = new OracleConnector();
//                connect = oc.getConnection();
//
//                statment = connect.createStatement();
//                statment.executeQuery(QueryHolder.getQueryForOneClientWithDateInterval(131794689, 131859201, (String) userSelecter.getSelectedItem()));
//                ResultSet rs = statment.getResultSet();
//                XmlParser.initData(GlobalVars.getFirm(), (String) userSelecter.getSelectedItem());
//                String nick = XmlParser.getUserNick();
//                SReporter.setOutFile(nick);
//
//                while (rs.next()) {
//
//                    sreporter.addFio(XmlParser.getFio());
//                    sreporter.addId(XmlParser.getId());
//                    sreporter.addIdDate(XmlParser.getIdDate());
//                    sreporter.addIdMade(XmlParser.getIdMade());
//                    sreporter.addAccount(QueryHolder.getResultAccount(rs));
//                    sreporter.addClientSign(XmlParser.getSN());
//                    sreporter.addDate(QueryHolder.getResultDate(rs));
//                    sreporter.addDirection(QueryHolder.getResultDirect(rs));
//                    sreporter.addFirmName(XmlParser.getOrganization());
//                    sreporter.addGostReg(null);
//                    sreporter.addInstrType(QueryHolder.getResultInstrType(rs));
//                    sreporter.addNick(nick);
//                    sreporter.addNin(QueryHolder.getResultNin(rs));
//                    sreporter.addOrderType("Лимитированная заявка");
//                    sreporter.addPrice(QueryHolder.getResultPrice(rs).toString());
//                    sreporter.addQty(QueryHolder.getResultQty(rs));
//                    sreporter.addVolune(QueryHolder.getResultVolume(rs));
//                    sreporter.addEmitenet(XmlParser.getEmitent(QueryHolder.getResultInstrShortName(rs).trim()));
//                    String path = (String) formSelecter.getSelectedItem();
//                    sreporter.setFormOrder(FORMS_PATH + SEP + path);
//                    sreporter.exportToPDF();
//                }
//
//
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            } finally {
//                try {
//                    statment.close();
//                    connect.close();
//
//                } catch (SQLException e1) {
//                    e1.printStackTrace();
//                }
//            }
//
//
//        }
//    }


}
