package com.devtraining.systemdesign.post.service;

import com.devtraining.systemdesign.post.domain.Post;
import com.devtraining.systemdesign.post.domain.PostComment;
import java.util.List;

public record PostInfo(String title, String contents, List<String> comments) {

    public static PostInfo of(Post post) {
        List<String> comments =
                post.getComments().stream().map(PostComment::getText).toList();
        return new PostInfo(post.getTitle(), post.getContents(), comments);
    }

    public Post toEntity() {
        return Post.builder().title(this.title).contents(this.contents).build();
    }
}
