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
    public Long createMemberInfo(MemberInfo memberInfo) {
        Member member = memberInfo.toMember();
        member.encodePassword(passwordEncoder);

        Member savedMember = memberRepository.save(member);

        memberAuthorityService.grantAuthorities(savedMember, memberInfo.authorityTypes());

        return savedMember.getId();
    }

    @Transactional(readOnly = true)
    public List<MemberInfo> retrieveAllMemberInfos() {
        return memberRepository.findAllWithAuthority().stream()
                .map(MemberInfo::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public MemberInfo retrieveMemberInfo(Long memberId) {
        Member member = memberRepository.findWithAuthorityById(memberId).orElseThrow();

        return MemberInfo.of(member);
    }

    @Transactional
    public void modifyMemberActivation(Long memberId, boolean isToActivate) {
        Member member = memberRepository.findById(memberId).orElseThrow();

        member.activate(isToActivate);
    }
}
