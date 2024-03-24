package com.ceos19.everyTime.jjokji.service;

import com.ceos19.everyTime.error.ErrorCode;
import com.ceos19.everyTime.error.exception.NotFoundException;
import com.ceos19.everyTime.jjokji.domain.Jjokji;
import com.ceos19.everyTime.jjokji.domain.JjokjiRoom;
import com.ceos19.everyTime.jjokji.dto.response.JjokjiLatestResponse;
import com.ceos19.everyTime.jjokji.dto.response.JjokjiResponse;
import com.ceos19.everyTime.jjokji.repository.JjokjiRepository;
import com.ceos19.everyTime.jjokji.repository.JjokjiRoomRepository;
import com.ceos19.everyTime.member.domain.Member;
import com.ceos19.everyTime.member.repository.MemberRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class JjokjiService {
    private final JjokjiRepository jjokjiRepository;
    private final JjokjiRoomRepository jjokjiRoomRepository;
    private final MemberRepository memberRepository;

    //쪽지방 리스트의 모습을 최근 쪽지 메시지로 확인
    public List<JjokjiLatestResponse>  showJjokjiRoomByLatestJjokji(Member currentMember){

        List<JjokjiLatestResponse> latestJjokjiResponseList = jjokjiRoomRepository.findJjokjiRoomByMemberId(
            currentMember.getId()).stream().map(jjokjiRoom -> {
            Jjokji latestJjokji = jjokjiRepository.findLatestJjokjiByJjokjiRoomId(jjokjiRoom.getId()).orElseThrow(()->new NotFoundException(ErrorCode.MESSAGE_NOT_FOUND));
              return JjokjiLatestResponse
                  .builder()
                  .chatRoomId(latestJjokji.getJjokjiRoom().getId())
                  .message(latestJjokji.getMessage())
                  .createdAt(latestJjokji.getCreatedAt())
                  .build();
        }).collect(Collectors.toList());

        return latestJjokjiResponseList;
    }


    //쪽지를 전달하는 메서드
    @Transactional
    public Jjokji sendMessage(String message, Member currentMember,Long receiverId){
        Member receiver = memberRepository.findById(receiverId).orElseThrow(()-> new NotFoundException(ErrorCode.MESSAGE_NOT_FOUND));

        //쪽지방이 존재하지 않을때 쪽지방을 생성하고 그렇지 않다면 쪽지방을 조회
        JjokjiRoom jjokjiRoom = jjokjiRoomRepository.findByJjokjiRoomByParticipant(currentMember.getId(),receiverId).orElseGet(()->createJjokjiRoom(currentMember,receiver));

        Jjokji jjokji = Jjokji.builder().message(message).jjokjiRoom(jjokjiRoom).member(currentMember).build();
        return jjokjiRepository.save(jjokji);
    }

    //쪽지방 생성 메서드

    private JjokjiRoom createJjokjiRoom(Member currentMember,Member receiver){
        JjokjiRoom jjokjiRoom=JjokjiRoom.builder().firstReceiver(receiver).firstSender(currentMember).build();
        jjokjiRoomRepository.save(jjokjiRoom);
        return jjokjiRoom;
    }



    //하나의 채팅방에서의 메시지 기록 조회

    public List<JjokjiResponse> ChatListInOneRoom(Long roomId){
         return jjokjiRepository.findJjokjisByJjokjiRoom_Id(roomId).stream().map(jjokji -> {
             return JjokjiResponse
                 .builder()
                 .message(jjokji.getMessage())
                 .senderId(jjokji.getMember().getId())
                 .createdAt(jjokji.getCreatedAt())
                 .build();
         }).collect(Collectors.toList());
    }

    








}