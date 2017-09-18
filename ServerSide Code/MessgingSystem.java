package com.SEM.InvestmentHoustSystem;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Created by roie on 16/06/2017.
 */
public class MessgingSystem {
    private static final String FROM = "100plus.investments@gmail.com",PASSWORD = "100plus100plus";
        public static void send(String to,String title,String msg){
            //Get properties object
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");
            //get Session
            Session session = Session.getDefaultInstance(props,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(FROM,PASSWORD);
                        }
                    });
            //compose message
            try {
                MimeMessage message = new MimeMessage(session);
                message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));
                message.setSubject(title);
                message.setText(msg);
                //send message
                Transport.send(message);
                System.out.println("message sent successfully");
            } catch (MessagingException e) {
                System.out.println("Invalid Email");
            }

        }
}
