package com.devtraining.systemdesign;

import static org.assertj.core.api.Assertions.assertThat;

import com.devtraining.systemdesign.post.domain.Post;
import com.devtraining.systemdesign.post.domain.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class SystemDesignApplicationTests {

    @Autowired
    private PostRepository postRepository;

    @Test
    @Transactional
    @DisplayName("create post test")
    void createPostTest() {
        // given
        Post post = Post.builder().title("title").contents("contents").build();

        // when
        Post savedPost = postRepository.save(post);

        // then
        assertThat(savedPost).isEqualTo(post);
    }

    @Test
    @Transactional
    @DisplayName("update post test")
    void updatePostTest() {
        // given
        Post post = Post.builder().title("title").contents("contents").build();
        Post savedPost = postRepository.save(post);

        // when
        Post postToUpdate = postRepository.findById(savedPost.getId()).orElseThrow();
        postToUpdate.updateContents("updated contents");
        postRepository.save(postToUpdate);

        // then
        Post selectedPost = postRepository.findById(postToUpdate.getId()).orElseThrow();

        assertThat(selectedPost.getContents()).isEqualTo("updated contents");
    }
}
