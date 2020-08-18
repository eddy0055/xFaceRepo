package com.xpand.xface;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.xpand.xface.service.PersonInfoService;

@SpringBootApplication
@EnableTransactionManagement
public class XFaceApplication {
	@Autowired
	static PersonInfoService personInfoService;
	public static void main(String[] args) {
//		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));				
		SpringApplication.run(XFaceApplication.class, args);
	}
	
//	@NotNull
//    @Bean
//    ServletListenerRegistrationBean<ServletContextListener> myServletListener() {
//        ServletListenerRegistrationBean<ServletContextListener> srb = new ServletListenerRegistrationBean<>();
//        srb.setListener(new XFaceAppListener());
//        return srb;
//    }
}
