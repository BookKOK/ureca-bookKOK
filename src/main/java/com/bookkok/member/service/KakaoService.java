package com.bookkok.member.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.bookkok.member.dto.KakaoTokenDto.KakaoTokenResponse;
import com.bookkok.member.dto.KakaoTokenDto.KakaoUserInfoResponse;
import com.bookkok.member.dto.TokenDto.TokenResponse;
import com.bookkok.member.entity.Member;
import com.bookkok.member.entity.Provider;
import com.bookkok.member.entity.RoleType;
import com.bookkok.member.repository.MemberRepository;
import com.bookkok.util.PhoneNumberUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoService {

    @Value("${kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;
    
    @Value("${kakao.redirect-uri}")
    private String REDIRECT_URI;
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    
    public KakaoTokenResponse getAccessToken(String authCode) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        // body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("redirect_uri", REDIRECT_URI);
        body.add("code", authCode);
        body.add("client_secret", KAKAO_CLIENT_SECRET);
        // Http요청 객체
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(body, headers);
        // Kakao API 호출
        ResponseEntity<KakaoTokenResponse> response =
            new RestTemplate().exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                httpEntity,
                KakaoTokenResponse.class);

        return response.getBody();
    }
    
    public KakaoUserInfoResponse getUserInfo(String accessToken){
        final String BEARER_TOKEN_PREFIX = "Bearer ";
        // 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        headers.add("Authorization", BEARER_TOKEN_PREFIX + accessToken);
        
        // Http요청 객체
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);
        
        // Kakao API 호출
        ResponseEntity<KakaoUserInfoResponse> response =
            new RestTemplate().exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                KakaoUserInfoResponse.class);

        return response.getBody();
      }
    
    public TokenResponse login(KakaoUserInfoResponse userInfo) {
    	
        String providerId = userInfo.getId().toString();

        Optional<Member> optionalMember =
        		memberRepository.findByProviderAndProviderId(
                        Provider.KAKAO,
                        providerId);

        boolean isNewMember = false;
        Member member;

        // 3. 회원가입
        if(optionalMember.isEmpty()){

            isNewMember = true;

            String randomPassword = UUID.randomUUID().toString();
            member = Member.builder()
            		.memberId(userInfo.getKakaoAccount()
                            .getProfile()
                            .getNickname())
                    .provider(Provider.KAKAO)
                    .providerId(providerId)
                    .email(userInfo.getKakaoAccount().getEmail())
                    .name(userInfo.getKakaoAccount().getName())
                    .roleName(RoleType.USER)
                    .password(passwordEncoder.encode(randomPassword))
                    .phoneNumber(PhoneNumberUtil.normalize(userInfo.getKakaoAccount().getPhoneNumber()))
                    .build();

            memberRepository.save(member);

        }else{
            member = optionalMember.get();
        }


        // 5. 응답
        return tokenService.createToken(member);
    }
    
    
}