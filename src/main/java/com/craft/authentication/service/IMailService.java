package com.craft.authentication.service;

import com.craft.authentication.model.NotificationEmail;

public interface IMailService {
    void sendMail(NotificationEmail notificationEmail) throws Exception;
}
