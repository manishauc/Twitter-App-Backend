package com.springboot.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.springboot.blog.entity.Likes;


@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {
	
	List<Likes> findByUserId(long user_id);
	List<Likes> findByTweetId(long tweet_id);
	List<Likes> findByUserAndTweetId(long tweet_id,long user_id);
	
	@Query(value = "SELECT count(id) FROM likes where tweet_id = ?1 AND user_id = ?2", nativeQuery = true)
    Long CountOfAlreadyLiked(long tweetId,long userId);
	
	@Query(value = "select L.id from Likes L JOIN User U on L.user=U.id JOIN Tweets T on L.tweet=T.id  WHERE T.id like ?1 AND U.id like ?2")
	Long CheckIfAlreadyLikedId(long tweetId,long userId);
	
	
	@Query(value = "select T.id from Likes L JOIN User U on L.user=U.id JOIN Tweets T on L.tweet=T.id WHERE U.id like ?1")
	List<Long> findLikesById(long user_id);
}
