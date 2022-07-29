package it.topnetwork.smartdpi.utility.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SignOptions {
	
	public static String issuer;
	public static String subject;
	public static long ttlMillis;
	
	@Value("${smartdpi.security.signOptions.issuer}")
	public void seTokentIssuer(String issuer) {
		SignOptions.issuer = issuer;
	}
	
	@Value("${smartdpi.security.signOptions.subject}")
	public void setTokenSubject(String subject) {
		SignOptions.subject = subject;
	}
	
	@Value("${smartdpi.security.signOptions.ttlMillis}")
	public void setTokenTtlMillis(long ttlMillis) {
		SignOptions.ttlMillis = ttlMillis;
	}
	
}
