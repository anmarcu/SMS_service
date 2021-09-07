package com;

import gnu.io.CommPortIdentifier;
import org.smslib.OutboundMessage;
import org.smslib.Service;
import org.smslib.modem.SerialModemGateway;

import javax.swing.*;
import java.awt.event.*;

public class SMS extends JFrame {
    static SMS smsApp;

    private JPanel contentPanel;
    private JButton detect;
    private JTextArea jTextAreaLog;
    private JTextField jTextFieldCellPhoneNo;
    private JTextArea jTextAreaMessage;
    private JComboBox jComboBoxPorts;
    private JButton jButtonListPorts;
    private JButton buttonSendSMS;
    private JButton getModemInfoButton;

    public static void main(String[] args) {
        smsApp = new SMS();
        smsApp.pack();
        smsApp.setVisible(true);
//        System.exit(0);
    }

    public SMS() {
        setContentPane(contentPanel);
//        setModal(true);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        detect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                new PortsTest(smsApp).getAndTestCOMPorts();
            }
        });
        jButtonListPorts.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //////////jButtonListPorts///////////////////////////////
                jComboBoxPorts.removeAllItems();

                java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
                while ( portEnum.hasMoreElements() )
                {
                    CommPortIdentifier portIdentifier = portEnum.nextElement();
                    //System.out.println(portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) );
                    jComboBoxPorts.addItem(portIdentifier.getName());
                    jTextAreaLog.append("\n" + portIdentifier.getName()  +  " - " +  getPortTypeName(portIdentifier.getPortType()) + "\n");
                }

            }
        });
        buttonSendSMS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try{
                    sendSMS();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        getModemInfoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                try{
                    getModemInfo();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    static String getPortTypeName ( int portType )
    {
        switch ( portType )
        {
            case CommPortIdentifier.PORT_I2C:
                return "I2C";
            case CommPortIdentifier.PORT_PARALLEL:
                return "Parallel";
            case CommPortIdentifier.PORT_RAW:
                return "Raw";
            case CommPortIdentifier.PORT_RS485:
                return "RS485";
            case CommPortIdentifier.PORT_SERIAL:
                return "Serial";
            default:
                return "unknown type";
        }
    }

    void appendToJTextAreaLog(String txt)
    {
        jTextAreaLog.append(txt);
    }

    public void sendSMS() throws Exception
    {
        Service service = new Service();

        OutboundNotification outboundNotification = new OutboundNotification();

        SerialModemGateway gateway = new SerialModemGateway("", jComboBoxPorts.getSelectedItem().toString(), 9600, "", "");
        gateway.setInbound(true);
        gateway.setOutbound(true);
        //gateway.setSimPin("0000");
        service.setOutboundNotification(outboundNotification);
        service.addGateway(gateway);
        //String status = Service.getInstance().getServiceStatus().toString();
        //if(status=="STARTED"){}
        service.startService();

        OutboundMessage msg = new OutboundMessage(jTextFieldCellPhoneNo.getText(), jTextAreaMessage.getText());

        service.sendMessage(msg);

        jTextAreaLog.append(msg.toString());

        service.stopService();
//        service.removeGateway(gateway);
    }

    public void getModemInfo() throws Exception
    {
        Service service = null;
        try {
            service = new Service();

            SerialModemGateway gateway = new SerialModemGateway("", jComboBoxPorts.getSelectedItem().toString(), 9600, "", "");

            service.addGateway(gateway);
            service.startService();

            jTextAreaLog.append("Modem Information:");
            jTextAreaLog.append("\n  Manufacturer: " + gateway.getManufacturer());
            jTextAreaLog.append("\n  Model: " + gateway.getModel());
            jTextAreaLog.append("\n  Serial No: " + gateway.getSerialNo());
            jTextAreaLog.append("\n  SIM IMSI: " + gateway.getImsi());
            jTextAreaLog.append("\n  Signal Level: " + gateway.getSignalLevel() + " dBm");
            jTextAreaLog.append("\n  Battery Level: " + gateway.getBatteryLevel() + "%");

        }
        finally {
            service.stopService();
        }

//        service.removeGateway(gateway);

    }
}
