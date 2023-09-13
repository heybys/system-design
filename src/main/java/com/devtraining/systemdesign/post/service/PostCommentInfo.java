package com.devtraining.systemdesign.post.service;

import com.devtraining.systemdesign.post.domain.PostComment;

public record PostCommentInfo(String text) {

    public static PostCommentInfo of(PostComment postComment) {
        return new PostCommentInfo(postComment.getText());
    }
}
