package lk.ijse.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Properties;

public class MailFormController {

    public Text txtStatus;
    @FXML
    private JFXButton btnsend;

    @FXML
    private JFXTextArea txtemail;

    @FXML
    private JFXTextField txtfrom;

    @FXML
    private JFXTextField txtsubject;

    @FXML
    private JFXTextField txtto;

    @FXML
    private AnchorPane root;


    public void btnbackOnActin(ActionEvent actionEvent) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.centerOnScreen();
    }

    public void btnsendOnAction(ActionEvent actionEvent) {
        System.out.println("Start");
        txtStatus.setText("sending...");
        Mail mail = new Mail(); //creating an instance of Mail class
        mail.setMsg(txtemail.getText());//email message
        mail.setTo(txtto.getText()); //receiver's mail
        mail.setSubject(txtsubject.getText()); //email subject

        Thread thread = new Thread(mail);
        thread.start();

        System.out.println("end");
        txtStatus.setText("sended");
    }

    public static class Mail implements Runnable {
        private String msg;
        private String to;
        private String subject;

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public boolean outMail() throws MessagingException {
            String from = "dlconstruction396@gmail.com"; //sender's email address
            String host = "localhost";

            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", 587);
            Session session = Session.getInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("dlconstruction396@gmail.com", "lysz sxzs eltm eqhw");  // email and password
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(from));
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(this.subject);
            mimeMessage.setText(this.msg);
            Transport.send(mimeMessage);
            return true;
        }

        public void run() {
            if (msg != null) {
                try {
                    outMail();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("not sent. empty msg!");
            }
        }
    }
}
