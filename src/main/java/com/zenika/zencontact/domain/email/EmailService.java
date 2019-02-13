package com.zenika.zencontact.domain.email;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import com.zenika.zencontact.domain.Email;
import com.zenika.zencontact.resource.auth.AuthenticationService;

public class EmailService {

    private static final Logger LOG = Logger.getLogger(EmailService.class.getName());
    private static EmailService INSTANCE = new EmailService();

    public static EmailService getInstance() {
        return INSTANCE;
    }

    public void sendEmail(Email email) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(
                AuthenticationService.getInstance().getUser().getEmail(),
                AuthenticationService.getInstance().getUsername()));
            msg.addRecipient(Message.RecipientType.TO,
                new InternetAddress(email.to, email.toName));

            msg.setReplyTo(new Address[]{new InternetAddress("admin@epsi-20181211-sejuapig.appspotmail.com",
                    "Application team")});
            msg.setSubject(email.subject);
            msg.setText(email.body);

            Transport.send(msg);
            LOG.warning("mail envoyé!");
        } catch (AddressException e) {}
        catch (MessagingException e) {}
        catch (UnsupportedEncodingException e) {}
    }

    public void logEmail(HttpServletRequest request) {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);
        try {
            //MimeMessage encapsule toutes les informations
            MimeMessage message = new MimeMessage(session,
                request.getInputStream());

            LOG.warning("Subject:" + message.getSubject()); //Sujet du mail

            Multipart multipart = (Multipart) message.getContent();
            BodyPart part = multipart.getBodyPart(0);
            LOG.warning("Body:" + (String) part.getContent()); //Contenu du mail

            for (Address sender : message.getFrom()) {
                LOG.warning("From:" + sender.toString()); //Origine du mail
            }
        } catch (MessagingException e) {
        } catch (IOException e) {}
    }
}