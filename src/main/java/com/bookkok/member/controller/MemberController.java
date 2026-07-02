package com.bookkok.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bookkok.member.dto.MemberDto.ProfileResponse;
import com.bookkok.member.dto.MemberDto.SignupRequest;
import com.bookkok.member.dto.MemberDto.UpdatePasswordRequest;
import com.bookkok.member.dto.MemberDto.UpdateProfileRequest;
import com.bookkok.member.dto.MemberDto.UpdateProfileResponse;
import com.bookkok.member.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/api/members")
public class MemberController {

	private final MemberService memberService;
	
	@PostMapping("/signup")
	public ResponseEntity<String> signup(@RequestBody SignupRequest request){
		memberService.signup(request);
		
		return ResponseEntity.status(HttpStatus.CREATED)
		        .body("회원가입 성공");
	}
	
	@PostMapping("/password/reset")
	public ResponseEntity<Void> resetPassword(@RequestBody UpdatePasswordRequest request){
		memberService.resetPassword(request);
		
		return ResponseEntity.ok().build();
	}
	
	@PutMapping("/update")
    public ResponseEntity<UpdateProfileResponse> updateMember(
    		Authentication authentication,
            @RequestBody UpdateProfileRequest request
    ) {
		String memberId = authentication.getName();

        UpdateProfileResponse response = memberService.updateProfile(memberId, request);

        return ResponseEntity.ok(response);
    }
	
	@GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(Authentication authentication) {
		
		String memberId = authentication.getName();

	    ProfileResponse res = memberService.getProfile(memberId);
	    
        return ResponseEntity.ok(res);
    }
	
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteMember(Authentication authentication){
		memberService.deleteMember(authentication.getName());
		
		return ResponseEntity.ok().build();
	}

	
}
