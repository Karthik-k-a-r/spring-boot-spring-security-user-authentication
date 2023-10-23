package com.craft.authentication.service.implementation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import com.craft.authentication.constants.Constants;
import com.craft.authentication.exception.CustomException;
import com.craft.authentication.model.NotificationEmail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {MailService.class})
@ExtendWith(SpringExtension.class)
class MailServiceTest {
    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    private MailService mailService;

    @Test
    void testSendMail() throws MailException {
        doNothing().when(javaMailSender).send(Mockito.<MimeMessagePreparator>any());
        NotificationEmail notificationEmail = new NotificationEmail(Constants.Email.VERIFY_ACCOUNT_SUBJECT, Constants.Email.FROM_MAIL_ID,
                Constants.Email.VERIFY_ACCOUNT_BODY);

        mailService.sendMail(notificationEmail);
        verify(javaMailSender).send(Mockito.<MimeMessagePreparator>any());
        assertEquals(Constants.Email.VERIFY_ACCOUNT_BODY, notificationEmail.getBody());
        assertEquals(Constants.Email.VERIFY_ACCOUNT_SUBJECT, notificationEmail.getSubject());
        assertEquals(Constants.Email.FROM_MAIL_ID, notificationEmail.getRecipient());
    }

    @Test
    void testSendMailThrowsException() throws MailException {
        doThrow(new MailAuthenticationException("Activation email sent!!")).when(javaMailSender)
                .send(Mockito.<MimeMessagePreparator>any());
        assertThrows(CustomException.class, () -> mailService.sendMail(
                new NotificationEmail(Constants.Email.VERIFY_ACCOUNT_SUBJECT, Constants.Email.FROM_MAIL_ID, Constants.Email.VERIFY_ACCOUNT_BODY)));
        verify(javaMailSender).send(Mockito.<MimeMessagePreparator>any());
    }

}

