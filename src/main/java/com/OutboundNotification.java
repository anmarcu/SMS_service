package com;

import org.smslib.AGateway;
import org.smslib.IOutboundMessageNotification;
import org.smslib.OutboundMessage;

public class OutboundNotification implements IOutboundMessageNotification
{
    public void process(AGateway gateway, OutboundMessage msg)
    {
        System.out.println("Outbound handler called from Gateway: " + gateway.getGatewayId());
        System.out.println(msg);
    }

    public void process(String s, OutboundMessage outboundMessage) {
        System.out.println("Outbound handler called from Gateway: " + s);
        System.out.println(outboundMessage);
    }
}