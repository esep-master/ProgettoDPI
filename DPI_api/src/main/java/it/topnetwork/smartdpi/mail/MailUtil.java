package it.topnetwork.smartdpi.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.topnetwork.smartdpi.entity.Operatore;
import it.topnetwork.smartdpi.entity.Utente;
import it.topnetwork.smartdpi.utility.Utility;

@Component
public class MailUtil {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MailOptions mailOptions;

	/**
	 * invia email ad uno specifico indirizzo email
	 * @param email
	 * @param mailBody
	 */
	private void sendEmail(String email, String mailBody) {
		// Recipient's email ID needs to be mentioned.
		String to = email;
		
		log.info("Sending recovery password email to [{}]", to);

		// Sender's email ID needs to be mentioned
		String from = mailOptions.getMailUser();
		final String username = mailOptions.getMailUser();
		final String password = mailOptions.getMailPassword();


		Properties props = new Properties();
		props.put("mail.smtp.auth", mailOptions.getSmtpAuth());
		props.put("mail.smtp.starttls.enable", mailOptions.getSmtpStartTlsEnable());
		props.put("mail.smtp.host", mailOptions.getSmtpHost());
		props.put("mail.smtp.port", mailOptions.getSmtpPort());

		// Get the Session object		
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(username, password);
		    }
		});

		try {
			// Create a default MimeMessage object.
			Message message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));

			// Set Subject: header field
			message.setSubject(mailOptions.getMailSubject());

			// Send the actual HTML message, as big as you like
			message.setContent(mailBody,"text/html");

			// Send message
			Transport.send(message);
			log.info("Recovery password email sent to [{}]", to);
		} catch (MessagingException e) {
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * crea e invia la mail di recupero password ad uno specifico utente
	 * @param utente
	 */
	public void sendRecoveryPasswordMail(Utente utente) {
		String mailBody = "<p>Gentile " + utente.getNome() + " " +  utente.getCognome() + ",</p> " +
				"<p>di seguito la password:</p>" +
				"<p><b>" + Utility.decodeBASE64(utente.getPassword()) + "</b></p>";

		this.sendEmail(utente.getEmail(), mailBody);
	}
	
	/**
	 * crea e invia la mail di recupero password ad uno specifico operatore
	 * @param utente
	 */
	public void sendRecoveryPasswordMail(Operatore operatore) {
		String mailBody = "<p>Gentile " + operatore.getNominativo() + ",</p> " +
				"<p>di seguito la password:</p>" +
				"<p><b>" + Utility.decodeBASE64(operatore.getPassword()) + "</b></p>";

		this.sendEmail(operatore.getEmail(), mailBody);
	}

}
