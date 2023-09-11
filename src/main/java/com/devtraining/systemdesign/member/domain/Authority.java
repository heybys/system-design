package com.devtraining.systemdesign.member.domain;

import com.devtraining.systemdesign.generic.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "authority")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_id")
    private Long id;

    @Column(name = "authority_name", unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer priority = 0;

    @Builder
    public Authority(String name) {
        this.name = name;
    }

    public void updatePriority(Integer priority) {
        this.priority = priority;
    }
}
