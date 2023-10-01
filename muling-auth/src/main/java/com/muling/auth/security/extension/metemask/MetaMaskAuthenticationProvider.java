package com.muling.auth.security.extension.metemask;

import com.muling.auth.security.core.userdetails.member.MemberUserDetails;
import com.muling.auth.security.core.userdetails.member.MemberUserDetailsServiceImpl;
import com.muling.auth.security.extension.google.GoogleAuthenticationToken;
import com.muling.mall.ums.api.MemberFeignClient;
import com.muling.mall.ums.pojo.dto.MemberAuthDTO;
import com.muling.mall.ums.pojo.dto.MemberDTO;
import com.muling.common.auth.CryptoUtils;
import com.muling.common.result.Result;
import com.muling.common.result.ResultCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.web3j.crypto.WalletUtils;

import java.util.HashSet;

@Data
@Slf4j
public class MetaMaskAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;
    private MemberFeignClient memberFeignClient;

    /**
     * metamask认证
     *
     * @param authentication
     * @return
     * @throws AuthenticationException
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("enter metamask authenprovider.");
        MetaMaskAuthenticationToken authenticationToken = (MetaMaskAuthenticationToken) authentication;
        String address = authenticationToken.getAddress();
        String message = authenticationToken.getMessage();
        String signature = authenticationToken.getSignature();

        boolean validAddress = WalletUtils.isValidAddress(address);
        if (!validAddress) {
            throw new InvalidTokenException("verify error.");
        }
        boolean verify = CryptoUtils.verifySignature(address, message, signature);
        if (!verify) {
            throw new InvalidTokenException("verify error.");
        }

        log.info("verify metamask idtoken result:" + address);
        if (address == null) {
            throw new AuthenticationException("metamask address is error.") {
                @Override
                public String getMessage() {
                    return "metamask address is error.";
                }
            };
        }

        Result<MemberAuthDTO> memberAuthResult = memberFeignClient.loadUserByOpenId(address);
        // 用户不存在，注册成为新会员
        boolean isNew = false;
        if (memberAuthResult != null && ResultCode.USER_NOT_EXIST.getCode().equals(memberAuthResult.getCode())) {
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setOpenid(address);
            memberDTO.setNickName(address);
            memberFeignClient.addMember(memberDTO);
            isNew = true;
        }
        MemberUserDetails userDetails = (MemberUserDetails) ((MemberUserDetailsServiceImpl) userDetailsService).loadUserByOpenId(address);
        userDetails.setIsNew(isNew);
        MetaMaskAuthenticationToken result = new MetaMaskAuthenticationToken(userDetails, new HashSet<>());
        result.setDetails(authentication.getDetails());
        return result;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return GoogleAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
