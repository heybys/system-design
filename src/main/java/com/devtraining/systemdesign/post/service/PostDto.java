package com.devtraining.systemdesign.post.service;

import com.devtraining.systemdesign.post.domain.Post;
import java.util.List;

public record PostDto(Long postId, String title, String contents, List<PostCommentDto> comments) {

    public static PostDto of(Post post) {
        List<PostCommentDto> comments =
                post.getComments().stream().map(PostCommentDto::of).toList();
        return new PostDto(post.getId(), post.getTitle(), post.getContents(), comments);
    }

    public Post toEntity() {
        return Post.builder().title(this.title).contents(this.contents).build();
    }
}
