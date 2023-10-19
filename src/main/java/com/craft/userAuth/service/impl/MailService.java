package com.craft.userAuth.service.impl;

import com.craft.userAuth.constants.Constants;
import com.craft.userAuth.exception.CustomException;
import com.craft.userAuth.model.NotificationEmail;
import com.craft.userAuth.service.IMailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@Slf4j
public class MailService implements IMailService {

    @Autowired
    private JavaMailSender mailSender;
    @Async
    @Override
    public void sendMail(NotificationEmail notificationEmail) throws Exception {

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(Constants.Email.FROM_MAIL_ID);
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(notificationEmail.getBody());
        };
        try {
            mailSender.send(messagePreparator);
            log.info("Activation email sent!!");
        } catch (MailException e) {
            log.error("Exception occurred when sending mail", e);
            throw new CustomException(MessageFormat.format(Constants.ErrorMessages.EMAIL_SEND_FAILED,
                    notificationEmail.getRecipient()),
                    Constants.ErrorCodes.EMAIL_SEND_FAILED_CODE);
        }
    }
}
