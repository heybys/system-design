package com.devtraining.systemdesign.post.domain;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from post p left join fetch p.comments")
    List<Post> findAllWithComments();

    @Query("select p from post p left join fetch p.comments where p.id = :postId")
    Optional<Post> findWithCommentsById(Long postId);
}
