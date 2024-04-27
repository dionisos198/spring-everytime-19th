package com.ceos19.everyTime.member.controller;

import com.ceos19.everyTime.member.dto.MemberSignUpDto;
import com.ceos19.everyTime.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;



    @PostMapping
    public ResponseEntity<Void> saveMember(@RequestBody MemberSignUpDto memberSignUpDto){
        memberService.saveMember(memberSignUpDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}