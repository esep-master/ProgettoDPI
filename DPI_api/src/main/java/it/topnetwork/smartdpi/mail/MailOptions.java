package it.topnetwork.smartdpi.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailOptions {
	
	@Value("${smartdpi.mail.smtp.auth}")
	private String smtpAuth;
	@Value("${smartdpi.mail.smtp.starttls.enable}")
	private String smtpStartTlsEnable;
	@Value("${smartdpi.mail.smtp.host}")
	private String smtpHost;
	@Value("${smartdpi.mail.smtp.port}")
	private String smtpPort;
	@Value("${smartdpi.mail.transport.auth.user}")
	private String mailUser;
	@Value("${smartdpi.mail.transport.auth.pass}")
	private String mailPassword;
	@Value("${smartdpi.mail.subject}")
	private String mailSubject;
	
	public String getSmtpAuth() {
		return smtpAuth;
	}
	public void setSmtpAuth(String smtpAuth) {
		this.smtpAuth = smtpAuth;
	}
	public String getSmtpStartTlsEnable() {
		return smtpStartTlsEnable;
	}
	public void setSmtpStartTlsEnable(String smtpStartTlsEnable) {
		this.smtpStartTlsEnable = smtpStartTlsEnable;
	}
	public String getSmtpHost() {
		return smtpHost;
	}
	public void setSmtpHost(String smtpHost) {
		this.smtpHost = smtpHost;
	}
	public String getSmtpPort() {
		return smtpPort;
	}
	public void setSmtpPort(String smtpPort) {
		this.smtpPort = smtpPort;
	}
	public String getMailUser() {
		return mailUser;
	}
	public void setMailUser(String mailUser) {
		this.mailUser = mailUser;
	}
	public String getMailPassword() {
		return mailPassword;
	}
	public void setMailPassword(String mailPassword) {
		this.mailPassword = mailPassword;
	}
	public String getMailSubject() {
		return mailSubject;
	}
	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}
	
}
