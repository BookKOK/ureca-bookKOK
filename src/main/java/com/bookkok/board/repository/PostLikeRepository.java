package com.bookkok.board.repository;

import com.bookkok.board.entity.Post;
import com.bookkok.board.entity.PostLike;
import com.bookkok.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);

}
