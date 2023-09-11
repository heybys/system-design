package com.devtraining.systemdesign.apis;

import com.devtraining.systemdesign.member.service.MemberInfo;
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
    public ResponseEntity<String> createMemberInfo(@RequestBody MemberInfo memberInfo) {

        Long savedMemberId = memberService.createMemberInfo(memberInfo);

        URI location = URI.create("/api/member/" + savedMemberId);

        return ResponseEntity.created(location).build();
    }

    @GetMapping()
    public ResponseEntity<List<MemberInfo>> retrieveAllMemberInfos() {

        List<MemberInfo> memberInfos = memberService.retrieveAllMemberInfos();

        return ResponseEntity.ok(memberInfos);
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<MemberInfo> retrieveMemberInfo(@PathVariable Long memberId) {

        MemberInfo memberInfo = memberService.retrieveMemberInfo(memberId);

        return ResponseEntity.ok(memberInfo);
    }
}
