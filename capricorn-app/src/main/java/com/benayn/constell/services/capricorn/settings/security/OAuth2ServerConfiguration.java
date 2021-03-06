package com.benayn.constell.services.capricorn.settings.security;

import static com.benayn.constell.services.capricorn.settings.constant.CapricornConstant.BASE_API;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
public class OAuth2ServerConfiguration {

    private static final String RESOURCE_ID = "constellation-capricorn-api";

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        private TokenStore tokenStore;
        private LogoutHandler constellationLogoutHandler;

        @Autowired
        public ResourceServerConfiguration(TokenStore tokenStore, LogoutHandler constellationLogoutHandler) {
            this.tokenStore = tokenStore;
            this.constellationLogoutHandler = constellationLogoutHandler;
        }

        @Override
        public void configure(ResourceServerSecurityConfigurer resources) {
            // @formatter:off
            resources
                .authenticationEntryPoint(new DefaultOAuth2AuthenticationEntryPoint())
                .resourceId(RESOURCE_ID).tokenStore(tokenStore);
            // @formatter:on
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            /*
            http
                .authorizeRequests()
                .antMatchers(BASE_API + "/**", "/manage/**")
                .authenticated()
                .and()
                .authorizeRequests()
                .antMatchers("/manage/**")
                .hasRole("MANAGE")
                // login
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error=loginError")
                .defaultSuccessUrl("/")
                // logout
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login").deleteCookies("connect.credentials", "JSESSIONID")
                .and()
                .csrf().disable()
            ;
             */

            // @formatter:off
            http
                .headers().frameOptions().sameOrigin()
                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(BASE_API + "/**", "/manage/**")
                .authenticated()
                .and()
                .logout()
                .logoutUrl("/user/logout")
                .addLogoutHandler(constellationLogoutHandler)
                .logoutSuccessUrl("/").deleteCookies("connect.credentials", "JSESSIONID")
                ;
            // @formatter:on
        }

    }

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        private JwtAccessTokenConverter jwtAccessTokenConverter;
        private TokenStore tokenStore;
        private AuthenticationManager authenticationManager;
        ConstellationUserDetailsService userDetailsService;

        @Autowired
        public void setJwtAccessTokenConverter(JwtAccessTokenConverter jwtAccessTokenConverter) {
            this.jwtAccessTokenConverter = jwtAccessTokenConverter;
        }

        @Autowired
        public void setTokenStore(TokenStore tokenStore) {
            this.tokenStore = tokenStore;
        }

        @Autowired
        @Qualifier("authenticationManagerBean")
        public void setAuthenticationManager(AuthenticationManager authenticationManager) {
            this.authenticationManager = authenticationManager;
        }

        @Autowired
        public void setUserDetailsService(ConstellationUserDetailsService userDetailsService) {
            this.userDetailsService = userDetailsService;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            // @formatter:off
            endpoints
                .tokenStore(tokenStore)
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter)
                .userDetailsService(userDetailsService);
            // @formatter:on
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            // @formatter:off
            clients
                .inMemory()

                .withClient("constellation-capricorn")
                .secret("9ddf1f40ddea06837257a7e2653a69d0")
                .authorities("USER")
                .authorizedGrantTypes("client_credentials", "password", "authorization_code", "refresh_token")
                .scopes("read", "write")
                .resourceIds(RESOURCE_ID)
                .accessTokenValiditySeconds(12 * 60 * 60)
                ;

            // @formatter:on
        }

    }

}
