package com.devtraining.systemdesign.post.service;

import com.devtraining.systemdesign.post.domain.Post;
import java.util.List;

public record PostInfo(String title, String contents, List<PostCommentInfo> comments) {

    public static PostInfo of(Post post) {
        List<PostCommentInfo> comments =
                post.getComments().stream().map(PostCommentInfo::of).toList();
        return new PostInfo(post.getTitle(), post.getContents(), comments);
    }

    public Post toEntity() {
        return Post.builder().title(this.title).contents(this.contents).build();
    }
}
