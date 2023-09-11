package com.devtraining.systemdesign.member.domain;

import com.devtraining.systemdesign.generic.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Entity(name = "member")
@ToString(exclude = {"memberAuthorities"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 512, unique = true)
    private String username;

    @Column(nullable = false, length = 256)
    private String password;

    @Column(nullable = false)
    private boolean activated = true;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private final List<MemberAuthority> memberAuthorities = new ArrayList<>();

    @Builder
    public Member(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void addMemberAuthority(MemberAuthority memberAuthority) {
        this.memberAuthorities.add(memberAuthority);
    }

    public void removeMemberAuthority(MemberAuthority memberAuthority) {
        this.memberAuthorities.remove(memberAuthority);
    }

    public void activate(boolean flag) {
        this.activated = flag;
    }

    public void encodePassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }
}
