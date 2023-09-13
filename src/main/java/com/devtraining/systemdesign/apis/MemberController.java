package com.devtraining.systemdesign.apis;

import com.devtraining.systemdesign.member.service.MemberDto;
import com.devtraining.systemdesign.member.service.MemberService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    private final MemberService memberService;

    @PostMapping()
    public ResponseEntity<String> createMember(@RequestBody MemberDto memberDto) {

        Long savedMemberId = memberService.createMember(memberDto);

        URI location = URI.create("/api/member/" + savedMemberId);

        return ResponseEntity.created(location).build();
    }

    @GetMapping()
    public ResponseEntity<List<MemberDto>> retrieveAllMembers() {

        List<MemberDto> memberDtos = memberService.retrieveAllMembers();

        return ResponseEntity.ok(memberDtos);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberDto> retrieveMember(@PathVariable Long memberId) {

        MemberDto memberDto = memberService.retrieveMember(memberId);

        return ResponseEntity.ok(memberDto);
    }
}
