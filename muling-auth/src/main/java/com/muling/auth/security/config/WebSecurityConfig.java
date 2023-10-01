package com.muling.auth.security.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import com.muling.auth.security.extension.alipay.AlipayAuthenticationProvider;
import com.muling.auth.security.extension.email.MailCodeAuthenticationProvider;
import com.muling.auth.security.extension.facebook.FacebookAuthenticationProvider;
import com.muling.auth.security.extension.google.GoogleAuthenticationProvider;
import com.muling.auth.security.extension.metemask.MetaMaskAuthenticationProvider;
import com.muling.auth.security.extension.mobile.SmsCodeAuthenticationProvider;
import com.muling.auth.security.extension.username.UsernameAuthenticationProvider;
import com.muling.auth.security.extension.wechat.WechatAuthenticationProvider;
import com.muling.auth.security.extension.wxopen.WxopenAuthenticationProvider;
import com.muling.common.cert.service.HttpApiClientWechat;
import com.muling.mall.ums.api.MemberFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService sysUserDetailsService;
    private final UserDetailsService memberUserDetailsService;
    private final WxMaService wxMaService;
    private final MemberFeignClient memberFeignClient;
    private final StringRedisTemplate redisTemplate;
    private final RestTemplate restTemplate;

    private final HttpApiClientWechat httpApiClientWechat;

    private final Environment env;


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests().antMatchers("/oauth/**", "/sms/code", "/sms/notify","/actuator/health").permitAll()
                // @link https://gitee.com/xiaoym/knife4j/issues/I1Q5X6 (接口文档knife4j需要放行的规则)
                .antMatchers("/webjars/**", "/doc.html", "/swagger-resources/**", "/v2/api-docs").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf().disable();
    }

    /**
     * 认证管理对象
     *
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(wechatAuthenticationProvider()).
                authenticationProvider(googleAuthenticationProvider()).
                authenticationProvider(facebookAuthenticationProvider()).
                authenticationProvider(metaMaskAuthenticationProvider()).
                authenticationProvider(passwordAuthenticationProvider()).
                authenticationProvider(daoAuthenticationProvider()).
                authenticationProvider(emailAuthenticationProvider()).
                authenticationProvider(smsCodeAuthenticationProvider()).
                authenticationProvider(wxopenAuthenticationProvider()).
                authenticationProvider(alipayAuthenticationProvider());
    }

    private AuthenticationProvider wxopenAuthenticationProvider() {
        WxopenAuthenticationProvider provider = new WxopenAuthenticationProvider();
        provider.setHttpApiClientWechat(httpApiClientWechat);
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setMemberFeignClient(memberFeignClient);
        provider.setRedisTemplate(redisTemplate);
        provider.setEnv(env);
        return provider;
    }

    private AuthenticationProvider alipayAuthenticationProvider() {
        AlipayAuthenticationProvider provider = new AlipayAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setMemberFeignClient(memberFeignClient);
        provider.setRedisTemplate(redisTemplate);
        provider.setEnv(env);
        return provider;
    }

    private AuthenticationProvider emailAuthenticationProvider() {
        MailCodeAuthenticationProvider provider = new MailCodeAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setMemberFeignClient(memberFeignClient);
        provider.setRedisTemplate(redisTemplate);
        return provider;
    }

    private AuthenticationProvider passwordAuthenticationProvider() {
        UsernameAuthenticationProvider provider = new UsernameAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setMemberFeignClient(memberFeignClient);
        return provider;
    }

    /**
     * 手机验证码认证授权提供者
     *
     * @return
     */
    @Bean
    public SmsCodeAuthenticationProvider smsCodeAuthenticationProvider() {
        SmsCodeAuthenticationProvider provider = new SmsCodeAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setMemberFeignClient(memberFeignClient);
        provider.setRedisTemplate(redisTemplate);
        provider.setEnv(env);
        return provider;
    }

    /**
     * 微信认证授权提供者
     *
     * @return
     */
    @Bean
    public WechatAuthenticationProvider wechatAuthenticationProvider() {
        WechatAuthenticationProvider provider = new WechatAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setWxMaService(wxMaService);
        provider.setMemberFeignClient(memberFeignClient);
        return provider;
    }

    /**
     * Google授权提供者
     *
     * @return
     */
    @Bean
    public GoogleAuthenticationProvider googleAuthenticationProvider() {
        GoogleAuthenticationProvider provider = new GoogleAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setMemberFeignClient(memberFeignClient);
        return provider;
    }

    /**
     * facebook授权提供者
     *
     * @return
     */
    @Bean
    public FacebookAuthenticationProvider facebookAuthenticationProvider() {
        FacebookAuthenticationProvider provider = new FacebookAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setMemberFeignClient(memberFeignClient);
        provider.setRestTemplate(restTemplate);
        return provider;
    }

    @Bean
    public MetaMaskAuthenticationProvider metaMaskAuthenticationProvider() {
        MetaMaskAuthenticationProvider provider = new MetaMaskAuthenticationProvider();
        provider.setUserDetailsService(memberUserDetailsService);
        provider.setMemberFeignClient(memberFeignClient);
        return provider;
    }


    /**
     * 用户名密码认证授权提供者
     *
     * @return
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(sysUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false); // 是否隐藏用户不存在异常，默认:true-隐藏；false-抛出异常；
        return provider;
    }


    /**
     * 密码编码器
     * <p>
     * 委托方式，根据密码的前缀选择对应的encoder，例如：{bcypt}前缀->标识BCYPT算法加密；{noop}->标识不使用任何加密即明文的方式
     * 密码判读 DaoAuthenticationProvider#additionalAuthenticationChecks
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


}
