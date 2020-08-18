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

import com.xpand.xface.util.ConstUtil;
import com.xpand.xface.web.error.CustomAccessDeniedHandler;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
public class CustomWebSecurityConfigurer extends WebSecurityConfigurerAdapter {
	@Autowired
    CustomAuthenticationProvider authProvider;
	@Autowired
	CustomAuthenticationSuccessHandler authenSuccessHanler; 
	@Autowired
	CustomAccessDeniedHandler accessDeniedHandler;
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    web
	       .ignoring()
	       .antMatchers("/static/**", "/bootstrap/**", "/dist/**", "/plugins/**");
	}
	@Override
	protected void configure(HttpSecurity http) throws Exception {
//	        http.csrf().disable()
//	            .authorizeRequests()
//				.antMatchers("/**").permitAll()
//	            .and()
//	            .exceptionHandling().accessDeniedHandler(accessDeniedHandler);
    	http.csrf().disable().cors().configurationSource(new CorsConfigurationSource() {			
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                return new CorsConfiguration().applyPermitDefaultValues();
            }
        })    			
    	.and().httpBasic().and().authorizeRequests()    	        	
    	.antMatchers("/rest/userManage/user/forceChangePwd").hasAnyRole(ConstUtil.USER_ROLE_FORCE_CHANGE_PWD_CONFIG)
    	.antMatchers("/auth/forceChangePwd").hasAnyRole(ConstUtil.USER_ROLE_FORCE_CHANGE_PWD_CONFIG)
    	.antMatchers("/rest/userManage/user/forgetPwd").permitAll()
    	.antMatchers("/rest/person/registerCustomerV2").permitAll()
    	.antMatchers("/rest").hasAnyRole("USER","ADMIN")    	
    	.antMatchers("/rest/alarm/*").permitAll()
    	.antMatchers("/error/invalidsession").permitAll()
        .antMatchers("/**").hasAnyRole("USER","ADMIN")  
        .antMatchers(ConstUtil.WEBSOCKET_ENDPOINT).hasAnyRole("USER","ADMIN")
        .anyRequest().authenticated()
        .and().formLogin().loginPage("/auth/login").permitAll()   
        .and().formLogin().successHandler(this.authenSuccessHanler)
        .and().logout().logoutSuccessUrl("/auth/login")        
        .and().logout().invalidateHttpSession(true).clearAuthentication(true)
        	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        	.logoutSuccessUrl("/")        
        .and().exceptionHandling().accessDeniedHandler(this.accessDeniedHandler)
        .and().sessionManagement().maximumSessions(1).expiredUrl("/error/invalidsession");   
        http.csrf().disable();
	}	    

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
    	auth.authenticationProvider(this.authProvider);
        
    }           
}
