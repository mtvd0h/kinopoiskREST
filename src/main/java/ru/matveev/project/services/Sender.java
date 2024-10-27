package ru.matveev.project.services;

import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class Sender {

    private final JavaMailSender mailSender;

    @Autowired
    public Sender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, File file) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            mimeMessage.setFrom("lolmen.228@bk.ru");
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject("Ваши сохраненные фильмы!");

            Multipart multipart = new MimeMultipart();
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            FileDataSource source = new FileDataSource(file);
            mimeBodyPart.setDataHandler(new DataHandler(source));
            mimeBodyPart.setFileName(file.getName());
            multipart.addBodyPart(mimeBodyPart);

            mimeMessage.setContent(multipart);
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
