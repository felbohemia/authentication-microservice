package microservices.book.authentication.services;

import microservices.book.authentication.user.Mail;

public interface MailService {
    public void sendEmail(Mail mail);
}
