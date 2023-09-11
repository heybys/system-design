package com.devtraining.systemdesign.post.domain;

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

@ToString(exclude = {"comments"})
@Getter
@Entity(name = "post")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @Column(name = "post_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 512)
    private String title;

    @Column(nullable = false, length = 1024)
    private String contents;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private final List<PostComment> comments = new ArrayList<>();

    @Builder
    public Post(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }

    public void updateContents(String contents) {
        this.contents = contents;
    }

    public void addComments(PostComment comment) {
        this.comments.add(comment);
        comment.setPost(this);
    }
}
