package com.craft.authentication.service.implementation;

import com.craft.authentication.constants.Constants;
import com.craft.authentication.exception.CustomException;
import com.craft.authentication.model.NotificationEmail;
import com.craft.authentication.service.IMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class MailService implements IMailService {
    private final JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailId;

    @Async
    @Override
    public void sendMail(NotificationEmail notificationEmail) {

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(mailId);
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(notificationEmail.getBody());
        };

        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            throw new CustomException(MessageFormat.format(Constants.ErrorMessages.EMAIL_SEND_FAILED,
                    notificationEmail.getRecipient()),
                    Constants.ErrorCodes.EMAIL_SEND_FAILED_CODE);
        }
    }
}
