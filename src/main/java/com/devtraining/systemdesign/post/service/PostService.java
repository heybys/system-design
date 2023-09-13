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
    public Long createPost(PostDto postDto) {
        Post savedPost = postRepository.save(postDto.toEntity());

        return savedPost.getId();
    }

    @Transactional(readOnly = true)
    public List<PostDto> retrieveAllPosts() {
        return postRepository.findAllWithComments().stream().map(PostDto::of).toList();
    }

    @Transactional(readOnly = true)
    public PostDto retrievePost(Long postId) {
        Post post = postRepository.findWithCommentsById(postId).orElseThrow();

        return PostDto.of(post);
    }
}
