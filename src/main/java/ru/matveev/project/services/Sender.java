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
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.matveev.project.models.Film;
import ru.matveev.project.models.Pages;

import java.io.File;
import java.util.List;

@Service
public class Sender {

    private final JavaMailSender mailSender;

    @Autowired
    public Sender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendMail(String to, List<Film> films) {
        try {
            File file = saveXML(films);
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
    public File saveXML(List<Film> films) {
        File file = new File("films.xml");
        try {
            JAXBContext context = JAXBContext.newInstance(Pages.class);
            Pages page = new Pages();
            page.setItems(films);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(page, file);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return file;
    }
}
