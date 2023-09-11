package com.devtraining.systemdesign.post.service;

import com.devtraining.systemdesign.post.domain.Post;
import com.devtraining.systemdesign.post.domain.PostRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public Long createPostInfo(PostInfo postInfo) {
        Post savedPost = postRepository.save(postInfo.toEntity());

        return savedPost.getId();
    }

    @Transactional(readOnly = true)
    public List<PostInfo> retrieveAllPostInfos() {
        return postRepository.findAll().stream().map(PostInfo::of).toList();
    }

    @Transactional(readOnly = true)
    public PostInfo retrievePostInfo(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();

        return PostInfo.of(post);
    }
}
