package com.devtraining.systemdesign.post.service;

import com.devtraining.systemdesign.post.domain.PostComment;

public record PostCommentDto(Long commentId, String text) {

    public static PostCommentDto of(PostComment postComment) {
        return new PostCommentDto(postComment.getId(), postComment.getText());
    }
}
