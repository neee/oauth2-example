package ru.cerberus.server

import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.web.client.RestTemplate;

/**
 * @author   Sergey Serdyuk
 * @version  31/10/2016
 */
@Slf4j
@EnableOAuth2Sso
@Configuration
class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    @Autowired
    RemoteTokenServices remoteTokenServices(ResourceServerProperties resource, RestTemplate restTemplate) {
        RemoteTokenServices services = new RemoteTokenServices()
        services.setRestTemplate(restTemplate)
        services.setCheckTokenEndpointUrl(resource.tokenInfoUri)
        services.setClientId(resource.clientId)
        services.setClientSecret(resource.clientSecret)
        services.setAccessTokenConverter(accessTokenConverter())
        return services
    }

    @Override
    void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(
                '/oauth2/register',
                '/oauth2/authorize',
                '/oauth2/token',
                '/oauth2/refresh_token')
    }

    //I want to apply this settings
    @Override
    void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        http.sessionManagement().sessionFixation().none()
    }

    @Bean
    HttpSessionStrategy httpSessionStrategy() {
        return new CookieHttpSessionStrategy()
    }
}
