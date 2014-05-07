package nz.ac.auckland.cer.common.util;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

/**
 * Send e-mail.
 * Includes basic validation of e-mail addresses, subject and body
 */
public class Email {

	@Autowired
	private MailSender mailSender;
	private EmailValidator emailValidator = EmailValidator.getInstance();

    public void send (String from, String to, String cc, String replyto,
    	String subject, String body) throws Exception {
    	SimpleMailMessage mailMessage = new SimpleMailMessage();
    	this.validateEmailAddress(from);
    	this.validateEmailAddress(to);
    	if (replyto != null) {
    		this.validateEmailAddress(replyto);
        	mailMessage.setReplyTo(replyto);
    	}
    	if (cc != null) {
    		this.validateEmailAddress(cc);
        	mailMessage.setCc(cc);
    	}
    	if (subject == null) {
    		throw new Exception("Subject must not be null");
    	}
    	if (body == null) {
    		throw new Exception("Body must not be null");
    	}
    	mailMessage.setFrom(from);
    	mailMessage.setTo(to);
    	mailMessage.setSubject(subject);
    	mailMessage.setText(body);
    	this.mailSender.send(mailMessage);
    }

    protected void validateEmailAddress(String address) throws Exception {
        if (!this.emailValidator.isValid(address)) {
        	throw new Exception("Invalid e-mail address: " + address);
        }
    }

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
    
}
