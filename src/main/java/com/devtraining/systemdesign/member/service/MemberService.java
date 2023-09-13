package com.devtraining.systemdesign.member.service;

import com.devtraining.systemdesign.member.domain.Member;
import com.devtraining.systemdesign.member.domain.MemberRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final MemberAuthorityService memberAuthorityService;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long createMember(MemberDto memberDto) {
        Member member = memberDto.toEntity();
        member.encodePassword(passwordEncoder);

        Member savedMember = memberRepository.save(member);

        memberAuthorityService.grantAuthorities(savedMember, memberDto.authorityTypes());

        return savedMember.getId();
    }

    @Transactional(readOnly = true)
    public List<MemberDto> retrieveAllMembers() {
        return memberRepository.findAllWithAuthority().stream()
                .map(MemberDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberDto retrieveMember(Long memberId) {
        Member member = memberRepository.findWithAuthorityById(memberId).orElseThrow();

        return MemberDto.of(member);
    }

    @Transactional
    public void modifyMemberActivation(Long memberId, boolean isToActivate) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        member.activate(isToActivate);
    }
}
