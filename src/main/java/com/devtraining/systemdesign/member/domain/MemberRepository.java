package com.devtraining.systemdesign.member.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("select m from member m join fetch m.memberAuthorities ma join fetch ma.authority")
    List<Member> findAllWithAuthority();

    @Query("select m from member m join fetch m.memberAuthorities ma join fetch ma.authority where m.id = :memberId")
    Optional<Member> findWithAuthorityById(Long memberId);

    @Query(
            "select m from member m join fetch m.memberAuthorities ma join fetch ma.authority where m.username = :username")
    Optional<Member> findWithAuthorityByUsername(String username);

    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);
}
