package com.muling.auth.security.extension.facebook;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.muling.auth.security.core.userdetails.member.MemberUserDetails;
import com.muling.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.muling.common.auth.GoogleAuthenticator;
import com.muling.common.constant.GlobalConstants;
import com.muling.common.exception.BizException;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.enums.MemberStatusEnum;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;

/**
 * facebook认证提供者
 *
 * @author <a href="mailto:xianrui0365@163.com">xianrui</a>
 * @date 2021/9/25
 */
@Data
@Slf4j
public class FacebookAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private MemberFeignClient memberFeignClient;
    private RestTemplate restTemplate;


    /**
     * facebook认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("enter facebook authenprovider.");
        FacebookAuthenticationToken authenticationToken = (FacebookAuthenticationToken) authentication;
        String accessToken = authenticationToken.getAccessToken();
        String userID = authenticationToken.getUserID();
        String googleCode = authenticationToken.getGoogleCode();
        String code = authenticationToken.getCode();

        String url = "https://graph.facebook.com/" + userID;
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("fields", "id,name,email,picture");
        params.add("access_token", accessToken);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);
        URI uri = builder.queryParams(params).build().encode().toUri();
        ResponseEntity<String> forEntity = restTemplate.getForEntity(uri, String.class);
        String resultStr = forEntity.getBody();

        boolean isNew = false;//new or not

        JSONObject json = (JSONObject) JSONUtil.parse(resultStr);
        String openid = json.getStr("id");
        String name = json.getStr("name");
        String email = json.getStr("email");
        String pictureUrl = json.getJSONObject("picture").getJSONObject("data").getStr("url");
        Result<MemberAuthDTO> memberAuthResult = memberFeignClient.loadUserByOpenId(openid);
        // 用户不存在，注册成为新会员
        if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
            if (StringUtils.isNotBlank(email)) {
                memberAuthResult = memberFeignClient.loadUserByEmail(email);
            }
            if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {

                MemberDTO memberDTO = new MemberDTO();
                memberDTO.setOpenid(openid);
                memberDTO.setNickName(name);
                memberDTO.setEmail(email);
                memberDTO.setAvatarUrl(pictureUrl);
                memberFeignClient.addMember(memberDTO);

                isNew = true;
            }
        }

        MemberAuthDTO memberAuthDTO = memberAuthResult.getData();
        if (memberAuthDTO != null) {
            if (memberAuthDTO.getStatus() == MemberStatusEnum.FORBIDDEN.getValue()) {//GlobalConstants.USER_STATUS_FORBIDDEN
                throw new BizException(ResultCode.USER_FORBIDDEN);
            }
            if (memberAuthDTO.getIsBindGoogle() == GlobalConstants.STATUS_YES) {
                /***检验谷歌验证码*/
                long t = System.currentTimeMillis();
                GoogleAuthenticator ga = new GoogleAuthenticator();
                //should give 5 * 30 seconds of grace...
                ga.setWindowSize(5);
                long codeNum = 0;
                try {
                    codeNum = Long.parseLong(googleCode);
                } catch (NumberFormatException e) {
                    log.error("google login error:{},code:{}", "谷歌验证码错误(不是数字)", googleCode);
                    throw new BizException(ResultCode.GOOGLECODE_ERROR);
                }
                boolean r = ga.checkCode(memberAuthDTO.getSecret(), codeNum, t);

                if (!r) {    //如果校验没通过,进行错误处理
                    log.error("google login error:{},username:{}", "谷歌验证码错误", memberAuthDTO.getUsername());
                    throw new BizException(ResultCode.GOOGLECODE_ERROR);
                }
            }
        }

        // 用户存在，登录成功
        MemberUserDetails userDetails = (MemberUserDetails) ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByOpenId(openid);
        userDetails.setIsNew(isNew);
        FacebookAuthenticationToken result = new FacebookAuthenticationToken(userDetails, new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return FacebookAuthenticationToken.class.isAssignableFrom(authentication);
    }

    public static void main(String[] args) {
        String code = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImQ0ZTA2Y2ViMjJiMDFiZTU2YzIxM2M5ODU0MGFiNTYzYmZmNWE1OGMiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJhY2NvdW50cy5nb29nbGUuY29tIiwiYXpwIjoiNTgwMzk0NjA4MjUwLWxoa3U0b2NvNWc0MG04bHU2Mm9wNDdqOHBsMWk2OWVzLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwiYXVkIjoiNTgwMzk0NjA4MjUwLWxoa3U0b2NvNWc0MG04bHU2Mm9wNDdqOHBsMWk2OWVzLmFwcHMuZ29vZ2xldXNlcmNvbnRlbnQuY29tIiwic3ViIjoiMTAwMTUyMjkyNjUyNzQxMjQzNzc4IiwiZW1haWwiOiJ4dXppeXUuZ2xhc3NAZ21haWwuY29tIiwiZW1haWxfdmVyaWZpZWQiOnRydWUsImF0X2hhc2giOiJ0ZnhxSXM5WUlTczVyaFY4cGpnbjR3IiwibmFtZSI6IlppeXUgWHUiLCJwaWN0dXJlIjoiaHR0cHM6Ly9saDMuZ29vZ2xldXNlcmNvbnRlbnQuY29tL2EvQUFUWEFKekNyRU5EaV9ya1dwcHQ3aVNObVB5ZE1Vd1lRQlVqQkk1U1lzSnk9czk2LWMiLCJnaXZlbl9uYW1lIjoiWml5dSIsImZhbWlseV9uYW1lIjoiWHUiLCJsb2NhbGUiOiJlbiIsImlhdCI6MTYzODE3NjQyMCwiZXhwIjoxNjM4MTgwMDIwLCJqdGkiOiIyNGRmNWMzZDFmNmMzZDU3Yzc0ODNkZWMzNjdkMTAzZDA0M2IxMGM1In0.C7J_dN4x75eD_k4KylxszSg3wsueJOMHy4SeTCkweoQCvStzZ5X_ka3hlXbSjGETvzDCemHpMXiQHTVc0k6VmbNXpUiDrAWgEgeEHpIjv9BbJAwYS6nR1yAE3F-jdbqDbqk0UwwSRsGgL50Up4-SOHv-MYxGIpcnwknGj076YbxzVpko5l3RiV1cqnvxsjTzGYoTYlGK6ZkYqNo3BM9cq3sbmFd_CFY_BDJzN08RiN7z14Nxdqo7rfO_fZwSfwsLrYpHIvTgneeGEVa1m0avIs2UD9IoOgue05K3Hsn8z1ah4Jy8HKIRyd1YkZ2QZFy4kiFdwwlwPUIl3yJV9z3eqw";
        //        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
//                // Specify the CLIENT_ID of the app that accesses the backend:
//                .setAudience(Collections.singletonList(CLIENT_ID))
//                // Or, if multiple clients access the backend:
//                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
//                .build();

        try {
            final NetHttpTransport transport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleIdTokenVerifier verifier =
                    new GoogleIdTokenVerifier.Builder(transport, new GsonFactory()).build();

            GoogleIdToken idToken = null;
            idToken = verifier.verify(code);
            System.out.println(idToken);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
