package com.sprta.deliveryproject.controller;

import com.sprta.deliveryproject.dto.*;
import com.sprta.deliveryproject.exception.ProfileValidationException;
import com.sprta.deliveryproject.exception.SignupValidationException;
import com.sprta.deliveryproject.security.UserDetailsImpl;
import com.sprta.deliveryproject.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/login-page")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup-page")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/profile-page")
    public String profilePage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("memberId", userDetails.getMember().getId());
        return "profile";
    }

    @GetMapping("/profile-update-page")
    public String profleUpdatePage(Model model, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        model.addAttribute("memberId", userDetails.getMember().getId());
        return "profile-update";
    }

    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> signupMember(@Valid @RequestBody SignupRequestDto requestDto, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            Map<String,String> errorMap = new HashMap<>();  //error 담기위한 객체
            for (FieldError error : fieldErrors) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new SignupValidationException("회원가입 유효성 검사 실패",errorMap);
            //return "redirect:/api/member/signup";
        }
        return memberService.signupMember(requestDto);
    }

    @GetMapping("/{member_id}")
    @ResponseBody
    public MemberResponseDto getMember(@PathVariable Long member_id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.getMember(member_id, userDetails.getMember());
    }

    @PutMapping("/{member_id}")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> updateMember(@PathVariable Long member_id, @Valid @RequestBody ProfileRequestDto requestDto, BindingResult bindingResult, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        if(bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            Map<String,String> errorMap = new HashMap<>();  //error 담기위한 객체
            for (FieldError error : fieldErrors) {
                errorMap.put(error.getField(), error.getDefaultMessage());
            }
            throw new ProfileValidationException("프로필 수정 실패",errorMap);
        }
        return memberService.updateMember(member_id, requestDto, userDetails.getMember());
    }

    @DeleteMapping("/{member_id}")
    @ResponseBody
    public ResponseEntity<ApiResponseDto> deleteMember(@PathVariable Long member_id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.deleteMember(member_id, userDetails.getMember());
    }

    @ExceptionHandler({SignupValidationException.class})
    public ResponseEntity<ApiResponseDto> handleSignupException(SignupValidationException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),ex.getMessage(),ex.getErrorMap());
        return new ResponseEntity<>(
                apiResponseDto,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler({ProfileValidationException.class})
    public ResponseEntity<ApiResponseDto> handleProfileUpdateException(ProfileValidationException ex) {
        ApiResponseDto apiResponseDto = new ApiResponseDto(HttpStatus.BAD_REQUEST.value(),ex.getMessage(),ex.getErrorMap());
        return new ResponseEntity<>(
                apiResponseDto,
                HttpStatus.BAD_REQUEST
        );
    }

}
