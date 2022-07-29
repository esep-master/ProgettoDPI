package it.topnetwork.smartdpi.utility;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;
import it.topnetwork.smartdpi.utility.constants.SignOptions;


@Component
public class TokenUtil {

	@Autowired
	private ResourceLoader resourceLoader;

	private String PRIVATE_KEY;
	
	/**
	 * create token jwt
	 * @param id
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 */
	public String createJWT(String id) throws IOException, URISyntaxException {

		//The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		//We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(this.readPrivateKey());
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		//Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id)
				.setIssuedAt(now)
				.setSubject(SignOptions.subject)
				.setIssuer(SignOptions.issuer)
				.signWith(signatureAlgorithm, signingKey);

		//if it has been specified, let's add the expiration
		if (SignOptions.ttlMillis > 0) {
			long expMillis = nowMillis + SignOptions.ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}  

		//Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	/**
	 * verify token jwt
	 * @param jwt
	 * @return
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 * @throws SignatureException 
	 * @throws MalformedJwtException 
	 * @throws UnsupportedJwtException 
	 * @throws ExpiredJwtException 
	 * @throws ApplicationException 
	 */
	public String decodeJWT(String jwt) throws ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException, IOException, URISyntaxException, ApplicationException {
		//This line will throw an exception if it is not a signed JWS (as expected)
		if(jwt != null && !jwt.equals("")) {
			try {
				Claims claims = Jwts.parser()
						.setSigningKey(DatatypeConverter.parseBase64Binary(this.readPrivateKey()))
						.parseClaimsJws(jwt).getBody();
				return claims.getId();
			}
			catch(Exception e) {
				throw new ApplicationException(ErrorCode.AUTH_TOKEN, e.getMessage());
			}
		}
		else {
			throw new ApplicationException(ErrorCode.AUTH_TOKEN, "Accesso negato. Non è stato fornito un token.");
		}
	}

	/**
	 * read private key
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private String readPrivateKey() throws IOException, URISyntaxException {
		if(this.PRIVATE_KEY == null) {
			try {  
				final Resource resource = resourceLoader.getResource("classpath:static/secret.pem");
				Reader reader = new InputStreamReader(resource.getInputStream());
				this.PRIVATE_KEY =  FileCopyUtils.copyToString(reader);
			} catch (Exception e) {     
				e.printStackTrace();
			}
		}

		return this.PRIVATE_KEY;
	}

}
