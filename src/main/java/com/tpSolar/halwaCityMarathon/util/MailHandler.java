package com.tpSolar.halwaCityMarathon.util;

import com.tpSolar.halwaCityMarathon.model.RegistrationDetails;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.thymeleaf.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Component
public class MailHandler {

        @Autowired
        private JavaMailSender mailSender;

        @Autowired
        private SpringTemplateEngine templateEngine;

        @Value("${app.mail.sender}")
        private String mailId;

        public void sendRegistrationConfirmationEmail(RegistrationDetails details) throws MessagingException {
            Context context = new Context();
            context.setVariable("PARTICIPANT_NAME", details.getParticipantName());
            context.setVariable("CHEST_NUMBER", details.getId());
            context.setVariable("EVENT_NAME", details.getEventName());

            String htmlContent = templateEngine.process("confirmation_mail", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mailId);
            helper.setTo(details.getEmail());
            helper.setSubject("Halwa City Marathon 2025 - Registration Confirmed");
            helper.setText(htmlContent, true);

            mailSender.send(message);
        }
    }

  /*private static JavaMailSender mailSender;

    @Autowired
    public MailHandler(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    private static String mailId = "vijaysankher04@gmail.com";

    private static final Logger logger = LoggerFactory.getLogger(MailHandler.class);

    public static void sendEmailWithAttachment(String recipientEmail, byte[] pdfBytes) throws Exception {

        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new RuntimeException("Error: PDF byte array is empty! Cannot send email.");
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        logger.info("The Input values  : ---- : {}", mailId);
        // Set email details
        helper.setFrom(mailId); // Ensure sender email is set
        helper.setTo(recipientEmail);
        helper.setSubject("Certificate of Appreciation");
        helper.setText("Dear Participant, \n\nThanks for making this event a great success. "
                + "Please find your certificate attached. Happy Learning ✨.");

        // Convert byte[] to a PDF attachment
        ByteArrayDataSource pdfDataSource = new ByteArrayDataSource(pdfBytes, "application/pdf");

        // Attach the PDF to the email
        helper.addAttachment("Certificate.pdf", pdfDataSource);

        // Send email
        mailSender.send(message);
        System.out.println("Email sent successfully to " + recipientEmail);
    }*/