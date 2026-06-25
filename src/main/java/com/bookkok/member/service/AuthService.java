package com.bookkok.member.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bookkok.member.repository.MemberRepository;
import com.bookkok.member.dto.MemberDto.LoginRequest;
import com.bookkok.member.entity.Member;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService implements UserDetailsService {
	
	private final MemberRepository memberRepository;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public void authenticateLogin(LoginRequest requestDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = requestDTO.toAuthentication();
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }
	
	@Override
	public UserDetails loadUserByUsername(String memberId) throws UsernameNotFoundException { // member -> UserDetails 매핑
		
		Optional<Member> _member = memberRepository.findById(memberId);
		
		if(_member.isEmpty()) {
			throw new UsernameNotFoundException("memberId: " + memberId + "를 데이터베이스에서 찾을 수 없습니다.");
		}
		
		Member member = _member.get();
		
		GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + member.getRoleName());
		
		return new User(
				member.getMemberId(),
				member.getPassword(),
				Collections.singleton(grantedAuthority));
	}
}
