package com.devtraining.systemdesign.member.service;

import com.devtraining.systemdesign.member.domain.Authority;
import com.devtraining.systemdesign.member.domain.AuthorityRepository;
import com.devtraining.systemdesign.member.domain.Member;
import com.devtraining.systemdesign.member.domain.MemberAuthority;
import com.devtraining.systemdesign.member.domain.MemberAuthorityRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberAuthorityService {

    private final AuthorityRepository authorityRepository;
    private final MemberAuthorityRepository memberAuthorityRepository;

    @Transactional
    public void grantAuthorities(Member member, List<String> authorityNames) {
        authorityNames.forEach(authorityName -> {
            Authority authority = authorityRepository
                    .findByName(authorityName)
                    .orElse(Authority.builder().name(authorityName).build());

            authorityRepository.save(authority);

            MemberAuthority memberAuthority = MemberAuthority.builder()
                    .member(member)
                    .authority(authority)
                    .build();

            memberAuthorityRepository.save(memberAuthority);
        });
    }
}
