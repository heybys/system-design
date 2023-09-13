package com.devtraining.systemdesign.apis;

import com.devtraining.systemdesign.post.service.PostDto;
import com.devtraining.systemdesign.post.service.PostService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @PostMapping()
    public ResponseEntity<PostDto> createPost(@RequestBody PostDto postDto) {

        Long savedPostId = postService.createPost(postDto);

        URI savedPostUri = URI.create("/post/" + savedPostId);

        return ResponseEntity.created(savedPostUri).build();
    }

    @GetMapping()
    public ResponseEntity<List<PostDto>> retrieveAllPosts() {

        List<PostDto> postDtos = postService.retrieveAllPosts();

        return ResponseEntity.ok(postDtos);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> retrievePost(@PathVariable Long postId) {

        PostDto postDto = postService.retrievePost(postId);

        return ResponseEntity.ok(postDto);
    }
}
