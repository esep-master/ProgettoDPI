package it.topnetwork.smartdpi.controller.filter;

import java.io.IOException;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import it.topnetwork.smartdpi.exception.ApplicationException;
import it.topnetwork.smartdpi.utility.TokenUtil;
import it.topnetwork.smartdpi.utility.Utility;
import it.topnetwork.smartdpi.utility.constants.ErrorCode;

@Component
@Order(1)
public class TokenFilter implements Filter {

	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private TokenUtil tokenUtil;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		CustomHttpServletRequest req = new CustomHttpServletRequest((HttpServletRequest)request);
		HttpServletResponse res = (HttpServletResponse) response;

		// CORS
		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("Access-Control-Allow-Methods", "GET, OPTIONS, HEAD, PUT, POST, DELETE");
		res.addHeader("Access-Control-Allow-Headers", "*");

		String requestMethod = req.getMethod();
		String requestURI = req.getRequestURI();

		// build req ID (current timestamp) e imposta header
		long requestID = new Date().getTime();
		req.putHeader("request_id", Long.toString(requestID));
		
		log.info("Incoming Request [{} {}] from [{}]. Request ID [{}]", requestMethod, requestURI, req.getRemoteAddr(), requestID);
		if(this.skipTokenDecode(requestURI, requestMethod)) {
			chain.doFilter(req, response);
		}
		else {
			//validate tokens (Token, TokenAPP)
			String token = req.getHeader("Token");
			String tokenAPP = req.getHeader("TokenAPP");
			try {
				if(Utility.isValid(token)) {
					// token portale
					String idUtente = this.tokenUtil.decodeJWT(token);
					req.putHeader("id_utente", idUtente);
					log.info("Request[{}] - Token decoded for utente [{}]", requestID, idUtente);
				}
				if(Utility.isValid(tokenAPP)) {
					// token APP
					String idOperatore = this.tokenUtil.decodeJWT(tokenAPP);
					req.putHeader("id_operatore", idOperatore);
					log.info("Request[{}] - TokenAPP decoded for operatore [{}]", requestID, idOperatore);
				}
				
				chain.doFilter(req, response);
			} catch(ApplicationException ae) {
				log.error(ae.getMessage());
				res.sendError(ae.getCode(), ae.getMessage());
			} catch(Exception e) {
				log.error(e.getMessage());
				res.sendError(ErrorCode.GENERIC_ERROR, e.getMessage());
			}
		}
	}

	/**
	 * check if this request has to decode token
	 * @param requestURI
	 * @param requestMethod
	 * @return
	 */
	private boolean skipTokenDecode(String requestURI, String requestMethod) {
		boolean skip = false;
		if(requestMethod.equalsIgnoreCase("OPTIONS")) {
			skip = true;
		} else {
			skip = requestURI.contains("auth"); 
		}
		return skip;
	}

}