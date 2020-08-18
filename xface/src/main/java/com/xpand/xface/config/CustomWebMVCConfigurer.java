package com.xpand.xface.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.xpand.xface.entity.ApplicationCfg;
import com.xpand.xface.service.ApplicationCfgService;
import com.xpand.xface.service.GlobalVarService;
import com.xpand.xface.service.impl.GlobalVarServiceImpl;
import com.xpand.xface.service.impl.HWGateServiceImpl;
import com.xpand.xface.util.StringUtil;

@Configuration
public class CustomWebMVCConfigurer extends WebMvcConfigurerAdapter{

	@Autowired
	ApplicationCfgService appCfgService;
	@Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setOneIndexedParameters(true);
        ApplicationCfg appCfg = this.appCfgService.findByAppKey(ApplicationCfg.GUI_GRID_ROW_PER_PAGE);
        resolver.setFallbackPageable(new PageRequest(1, StringUtil.stringToInteger(appCfg.getAppValue1(),10)));
        argumentResolvers.add(resolver);
        super.addArgumentResolvers(argumentResolvers);
    }
	@Bean
	@Qualifier("HWGateServiceImpl")
    public HWGateServiceImpl hwGateServiceImpl() {
        return new HWGateServiceImpl();
    }	
}
