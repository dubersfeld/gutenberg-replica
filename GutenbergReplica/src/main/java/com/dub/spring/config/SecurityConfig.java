package com.dub.spring.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.dub.spring.service.UserService;


@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true, order = 0, mode = AdviceMode.PROXY,
        proxyTargetClass = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserService userService;

	@Bean
	public SimpleUrlAuthenticationSuccessHandler myAuthenticationSuccessHandler() {
		
		SimpleUrlAuthenticationSuccessHandler handler 
				= new SimpleUrlAuthenticationSuccessHandler();
			
		handler.setDefaultTargetUrl("/index");
		
		return handler;
	}
	
	@Bean
    protected SessionRegistry sessionRegistryImpl() {
        return new SessionRegistryImpl();
    }
	
	
	@Override
    protected void configure(HttpSecurity security) 
    		throws Exception {
        security
                .authorizeRequests()
                	.antMatchers("/login/**")
                		.permitAll()
                    .antMatchers("/login")
                      	.permitAll()
                    .antMatchers("/register")
                      	.permitAll()       
                    .antMatchers("/**")
                    	.authenticated()  
                    .and().formLogin()
                    .loginPage("/login")//
                    //.failureHandler(enclume)
                    .failureUrl("/login?loginFailed")
                    .successHandler(myAuthenticationSuccessHandler()) 
                    //.defaultSuccessUrl("/index")
                
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .permitAll()
                .and().logout()
                    .logoutUrl("/logout").logoutSuccessUrl("/login?loggedOut")
                    .invalidateHttpSession(true).deleteCookies("JSESSIONID")
                    .permitAll()
                .and().sessionManagement()
                    .sessionFixation().changeSessionId()
                    .maximumSessions(1).maxSessionsPreventsLogin(true)
                   
                .and().and().csrf()
                    .requireCsrfProtectionMatcher((r) -> {
                        String m = r.getMethod();
                        return !r.getServletPath().startsWith("/services/") &&
                                ("POST".equals(m) || "PUT".equals(m) ||
                                        "DELETE".equals(m) || "PATCH".equals(m));
                    });
                   
    }
	
	

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder builder) throws Exception {
	
    	 builder
         	.userDetailsService(this.userService)     
         	.passwordEncoder(new BCryptPasswordEncoder())
         	.and()
         	.eraseCredentials(true);        
    }
	
}