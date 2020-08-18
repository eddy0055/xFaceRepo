package com.xpand.xface.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
	@Autowired
    private CustomAuthenticationProvider authProvider;
//	@Autowired
//	private CustomUserDetailsService userDetailsService;

	@Override
	public void configure(WebSecurity web) throws Exception {
	    web
	       .ignoring()
	       .antMatchers("/static/**", "/bootstrap/**", "/dist/**", "/plugins/**");
	}
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//            .authorizeRequests()
//			.antMatchers("/**").permitAll()
//            .and()
//            .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    	http.cors().configurationSource(new CorsConfigurationSource() {			
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                return new CorsConfiguration().applyPermitDefaultValues();
            }
        })
    	.and().httpBasic().and().authorizeRequests().antMatchers("/rest").hasAnyRole("USER","ADMIN")
    	.antMatchers("/rest/pushData*").permitAll()    	
    	.antMatchers("/error/invalidsession").permitAll()    	
        .antMatchers("/**").hasAnyRole("USER","ADMIN")                        
        .anyRequest().authenticated()
        .and()        
        .formLogin().loginPage("/auth/login").permitAll()        
        .and()
        .logout().logoutSuccessUrl("/auth/login")        
        .and().logout().invalidateHttpSession(true).clearAuthentication(true)
        	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        	.logoutSuccessUrl("/")
        .and()
        .sessionManagement().maximumSessions(1).expiredUrl("/error/invalidsession");   
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth.authenticationProvider(this.authProvider);
        
    }    
        
	
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(this.userDetailsService) //.passwordEncoder(new BCryptPasswordEncoder());
//        	.passwordEncoder(this.getPasswordEncoder());        
//    }
//    private PasswordEncoder getPasswordEncoder() {
//    	return new PasswordEncoder() {    		
//            @Override
//            public String encode(CharSequence charSequence) {
//            	try {
//            		MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            		byte[] hash = digest.digest(charSequence.toString().getBytes(StandardCharsets.UTF_8));
//            		String sha256hex = new String(Hex.encode(hash));
//                    return sha256hex;
//            	}catch (Exception ex) {
//            		return charSequence.toString();
//            	}            	
//            }
//
//            @Override
//            public boolean matches(CharSequence charSequence, String s) {
//            	String encoderPwd = this.encode(charSequence);
//            	Logger.debug(this, "encoder: "+encoderPwd+", repository:"+s);
//            	if (encoderPwd.equals(s)) {
//            		//this.sysAuditService.createAudit(this.transactionId, SystemAudit.MOD_SECURITY, SystemAudit.MOD_SUB_ALL, "login for user "+super.getUserName(), SystemAudit.RES_SUCCESS);
//                	Logger.info(this, LogUtil.getLogInfo(this.transactionId, "login with username "+super.getUserName()+" pwd "+super.getUserName()+" is pass with role "+gson.toJson(listOfRole)));
//            		return true;
//            	}else {
//            		//this.sysAuditService.createAudit(this.transactionId, SystemAudit.MOD_SECURITY, SystemAudit.MOD_SUB_ALL, "login for user "+super.getUserName(), SystemAudit.RES_SUCCESS);
//                	Logger.info(this, LogUtil.getLogInfo(this.transactionId, "login with username "+super.getUserName()+" pwd "+super.getUserName()+" is pass with role "+gson.toJson(listOfRole)));
//            		return false;
//            	}            	
//            }
//        };
//    }
}
