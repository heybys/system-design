package com.devtraining.systemdesign.apis;

import com.devtraining.systemdesign.post.service.PostInfo;
import com.devtraining.systemdesign.post.service.PostService;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @PostMapping()
    public ResponseEntity<PostInfo> createPost(@RequestBody PostInfo postInfo) {

        Long savedPostId = postService.createPostInfo(postInfo);

        URI savedPostUri = URI.create("/post/" + savedPostId);

        return ResponseEntity.created(savedPostUri).build();
    }

    @GetMapping()
    public ResponseEntity<List<PostInfo>> retrieveAllPostInfos() {

        List<PostInfo> postInfos = postService.retrieveAllPostInfos();

        return ResponseEntity.ok(postInfos);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostInfo> retrievePostInfo(@PathVariable Long postId) {

        PostInfo postInfo = postService.retrievePostInfo(postId);

        return ResponseEntity.ok(postInfo);
    }
}
