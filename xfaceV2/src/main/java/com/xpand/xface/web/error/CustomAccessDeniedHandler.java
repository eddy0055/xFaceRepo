package com.xpand.xface.web.error;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.jcabi.log.Logger;
import com.xpand.xface.util.ConstUtil;

// handle 403 page
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,AccessDeniedException e) throws IOException, ServletException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Logger.info(this, httpServletRequest.getSession().getId()+ "|User " + auth.getName() + " attempted to access the protected URL: "+ httpServletRequest.getRequestURI());
            ArrayList<GrantedAuthority> authorities = new ArrayList<>(SecurityContextHolder.getContext().getAuthentication().getAuthorities());
            if (authorities.size()==1 && authorities.get(0).getAuthority().equals(ConstUtil.USER_ROLE_FORCE_CHANGE_PWD_USAGE)) {            	            	
//            	ArrayList<GrantedAuthority> authorities = new ArrayList<>();
//            	authorities.add(new SimpleGrantedAuthority("ROLE_NEWUSERROLE")); 
//            	    SecurityContextHolder.getContext().setAuthentication(
//            	        new UsernamePasswordAuthenticationToken(
//            	            SecurityContextHolder.getContext().getAuthentication().getPrincipal(),
//            	            SecurityContextHolder.getContext().getAuthentication().getCredentials(),
//            	            authorities)
//            	        );
            	httpServletResponse.sendRedirect("/xFace/auth/forceChangePwd");
            	Logger.info(this, httpServletRequest.getSession().getId()+ "|error0 redirect to /xFace/auth/forceChangePwd");
            }else {
            	httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/error/403");
            	Logger.info(this, httpServletRequest.getSession().getId()+ "|error1 redirect to 403");
            }
        	
        }else {
        	httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/error/403");
        	Logger.info(this, httpServletRequest.getSession().getId()+ "|error2 redirect to 403");
        }        
    }
}
