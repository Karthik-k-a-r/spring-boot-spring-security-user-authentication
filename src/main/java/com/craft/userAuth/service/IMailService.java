package com.craft.userAuth.service;

import com.craft.userAuth.model.NotificationEmail;

public interface IMailService {
    void sendMail(NotificationEmail notificationEmail) throws Exception;
}
