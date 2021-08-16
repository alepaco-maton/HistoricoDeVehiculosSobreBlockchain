/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inv.stamina.modulobase.commons;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author alepaco.com
 */
public class EmailNotifier {

    private static Properties makeProperties(String host, int port) {
        Properties properties = new Properties();

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");

        return properties;
    }

    /**
     *
     * @param host "smtp.gmail.com"
     * @param port 587
     * @param from
     * @param password
     * @param to
     * @param subject
     * @param data
     * @throws javax.mail.MessagingException
     */
    public static void sendEmail(String host, int port, String from, String password,
            String to, String subject, String data) throws MessagingException {

        Properties properties = makeProperties(host, port);
        // Get the Session object.// and pass username and password
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() { 
                return new PasswordAuthentication(from, password);
            }
        });

        // Used to debug SMTP issues
        session.setDebug(true);

        // Create a default MimeMessage object.
        MimeMessage message = new MimeMessage(session);

        // Set From: header field of the header.
        message.setFrom(new InternetAddress(from));

        // Set To: header field of the header.
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

        // Set Subject: header field
        message.setSubject(subject);

        // Now set the actual message
        message.setText(data);

        // Send message
        Transport.send(message);
    }

}
