package com.muling.auth.security.extension.google;

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
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;

import java.util.HashSet;

@Data
@Slf4j
public class GoogleAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private MemberFeignClient memberFeignClient;

    /**
     * google认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("enter google authenprovider.");
        GoogleAuthenticationToken authenticationToken = (GoogleAuthenticationToken) authentication;
        String code = authenticationToken.getIdToken();
        String googleCode = authenticationToken.getGoogleCode();
        String inviteCode = authenticationToken.getInviteCode();
        GoogleIdToken idToken = null;
        try {
            //这里可以限定客户端ID
            String clientID = "859730950696-1ufa24m1lhgcuopithmu0g9fkrp378i5.apps.googleusercontent.com";
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), new GsonFactory())
                    // Specify the CLIENT_ID of the app that accesses the backend:
                    //                .setAudience(Collections.singletonList(CLIENT_ID))
                    // Or, if multiple clients access the backend:
                    //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                    .build();
            idToken = verifier.verify(code);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InvalidTokenException("verify error.");
        }
        log.info("verify google idtoken result:" + idToken);
        if (idToken == null) {
            throw new AuthenticationException("google idToken is error;") {
                @Override
                public String getMessage() {
                    return "google idToken is error;";
                }
            };
        }
        //google 返回的数据
        GoogleIdToken.Payload payload = idToken.getPayload();
        //google 返回用户ID
        String openid = payload.getSubject();
//            System.out.println("User ID: " + openid);
        // Get profile information from payload
        //google 返回邮箱
        String email = payload.getEmail();
//        boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
//        String locale = (String) payload.get("locale");
//        String familyName = (String) payload.get("family_name");
//        String givenName = (String) payload.get("given_name");
        //创建用户，或者 登录成功，openid是三方绑定的Id
        Result<MemberAuthDTO> memberAuthResult = memberFeignClient.loadUserByOpenId(openid);
        // 用户不存在，注册成为新会员
        boolean isNew = false;
        if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
            //用户不存在
            if (StringUtils.isNotBlank(email)) {
                memberAuthResult = memberFeignClient.loadUserByEmail(email);
            }
            //检测邮箱
            if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
                //邮箱绑定用户也不存在，创建新用户
                MemberDTO memberDTO = new MemberDTO();
                memberDTO.setOpenid(openid);
                memberDTO.setNickName(name);
                memberDTO.setEmail(email);
                memberDTO.setAvatarUrl(pictureUrl);
                memberFeignClient.addMember(memberDTO);
                isNew = true;
            }
        }
        //
        MemberAuthDTO memberAuthDTO = memberAuthResult.getData();
        if (memberAuthDTO != null) {
            //被封用户，返回错误
            if (memberAuthDTO.getStatus() == MemberStatusEnum.FORBIDDEN.getValue()) {
                throw new BizException(ResultCode.USER_FORBIDDEN);
            }
            if (memberAuthDTO.getIsBindGoogle() == GlobalConstants.STATUS_YES) {
                //如果绑定了google验证码，则开启验证
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
        //获取用户详细信息
        MemberUserDetails userDetails = (MemberUserDetails) ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByOpenId(openid);
        userDetails.setIsNew(isNew);
        //构建Token
        GoogleAuthenticationToken result = new GoogleAuthenticationToken(userDetails, new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return GoogleAuthenticationToken.class.isAssignableFrom(authentication);
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
