package it.topnetwork.smartdpi;

import java.util.Date;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class SmartDpiApiApplication {
	
	@PostConstruct
    public void init(){
        TimeZone.setDefault(TimeZone.getTimeZone("CET"));   // It will set CEST timezone
        System.out.println("Spring boot application running in timezone :"+new Date());
    }

	public static void main(String[] args) {
		SpringApplication.run(SmartDpiApiApplication.class, args);
	}
	
//	/**
//	 * configura CORS
//	 * !!!!!!!!! NON FUNZIONA CON FILTER !!!!!!!!!!
//	 * @return
//	 */
//	@Bean
//	public WebMvcConfigurer corsConfigurer() {
//		return new WebMvcConfigurer() {
//			@Override
//			public void addCorsMappings(CorsRegistry registry) {
//				registry
//				.addMapping("/**")
//				.allowedOrigins("*")
//				.allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH")
//				.allowedHeaders("*");
//			}
//		};
//	}

}
