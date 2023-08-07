package com.springboot.blog.service.impl;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Tweets;
import com.springboot.blog.entity.User;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.TweetsDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.FollowRepository;
import com.springboot.blog.repository.LikesRepository;
import com.springboot.blog.repository.RetweetsRepository;
import com.springboot.blog.repository.TweetsRepository;
import com.springboot.blog.repository.UserRepository;
import com.springboot.blog.service.TweetsService;


@Service
public class TweetsServiceImpl implements TweetsService{
	
	    private TweetsRepository tweetsRepository;	  
	    private UserRepository userRepository; 
	    private ModelMapper modelMapper;
	    private LikesRepository likesRepository;    
	    private FollowRepository followRepository;
	    private RetweetsRepository retweetsRepository;
	    private CommentRepository commentRepository;

	  
	 
	    public TweetsServiceImpl(TweetsRepository tweetsRepository, UserRepository userRepository,
				ModelMapper modelMapper ,
				LikesRepository likesRepository,CommentRepository commentRepository,
				FollowRepository followRepository,RetweetsRepository retweetsRepository) {
			super();
			this.tweetsRepository = tweetsRepository;
			this.userRepository = userRepository;
			this.modelMapper = modelMapper;
			this.likesRepository=likesRepository;
			this.followRepository =followRepository;
			this.retweetsRepository=retweetsRepository;
			this.commentRepository =commentRepository;
		}

		//START :: CREATE TWEET BY USER ID
	    @Override
	    public TweetsDto createTweet(long user_id, TweetsDto tweetsDto) {
	
	        Tweets tweet = mapToEntity(tweetsDto);
	
	        // retrieve user entity by id
	        User userid = userRepository.findById(user_id).orElseThrow(
	                () -> new ResourceNotFoundException("User", "id", user_id));
	       
	        // set user to TWEET entity
	        tweet.setUser(userid);
	        //Check TWEET content length for validation
	        if(tweet.getContent().length()>280) {
				
				throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Length must be between 0 to 280 characters!.");
			}
	        // TWEET entity to DB
	        Tweets newTweet =  tweetsRepository.save(tweet);
	        tweetsDto = mapToDTO(newTweet);
	        tweetsDto.setUser_id(user_id);
	       
	        return tweetsDto;
	    }
	    //END :: CREATE TWEET BY USER ID
    
    
	  //START :: GET ALL TWEETS BY USER ID
		@Override
		public List<TweetsDto> getTweetsByUserId(long user_id) {
			 List<Tweets> tweets = tweetsRepository.findByUserId(user_id);
	
		        // convert list of TWEET entities to list of TWEET dto's
			 //List<TweetsDto> tweetDtoList = tweets.stream().map(tweet -> mapToDTO(tweet)).collect(Collectors.toList());
			 List<TweetsDto> tweetDtoList = tweets.stream()
			  .sorted(Comparator.comparing(Tweets::getId).reversed())
			  .map(tweet -> mapToDTO(tweet))
			  .collect(Collectors.toList());
		        //getting USERNAME for EACH TWEET
				 for (TweetsDto userD : tweetDtoList) {
					Long tweetId = userD.getId();
					 Tweets tweet = tweetsRepository.findById(tweetId).orElseThrow(() ->
		                new ResourceNotFoundException("Tweet", "id", tweetId));
					 
					 User fTweeterUser = userRepository.findById(tweet.getUser().getId()).orElseThrow(
				                () -> new ResourceNotFoundException("User", "id", tweet.getUser().getId()));
					 //comments of each TWEET
					 List<Comment> allTweetComments = commentRepository.findByTweetId(tweetId);
					 if(allTweetComments!=null) {
						 List<String> listOfComments = new LinkedList<String>();
						 for (Comment commentId : allTweetComments) {
							 String allTweetCommentMsg  = commentId.getCommentMsg();
							 listOfComments.add(allTweetCommentMsg);
						 }
						
						 userD.setComments(listOfComments);
					 }
					
					 userD.setUsername(fTweeterUser.getName()); 
				}	
				 return tweetDtoList;
		}
		//ENS :: GET ALL TWEETS BY USER ID
	
		//START :: GET LIST OF OWN , LIKED  AND FOLLOWED TWEETS FOR PROFILE
		@Override
		public List<TweetsDto> getAllOwnLikedAndRetweetedList(long user_id) {
		
			//TWEET by user
			 List<Tweets> Usertweets = tweetsRepository.findByUserId(user_id);
			 
			//TWEET liked by user
			 List<Long> UserLikedtweetsId = likesRepository.findLikesById(user_id);		
			 List<Tweets> likedTweets =tweetsRepository.findAllById(UserLikedtweetsId); 
			 Usertweets.addAll(likedTweets);
			 
			//RETWEETED TWEETS by user
			 List<Long> UserRetweetsId = retweetsRepository.findRetweetsByUserId(user_id);	
			  System.out.println(UserRetweetsId);
			 List<Tweets> RetweetedTweets =tweetsRepository.findAllById(UserRetweetsId); 
			 Usertweets.addAll(RetweetedTweets);
			 
			 return Usertweets.stream().map(tweet -> mapToDTO(tweet)).collect(Collectors.toList());
		
		}
		//END :: GET LIST OF OWN , LIKED  AND FOLLOWED TWEETS FOR PROFILE
		
		//START :: A LIST OF TWEETS FROM THE USER AND THOSE THEY FOLLOW FOR FEEDS
		@Override
		public List<TweetsDto> getAllUserAndFollowedTweets(long user_id) {
			
			// retrieve user entity by id
	        User user = userRepository.findById(user_id).orElseThrow(
	                () -> new ResourceNotFoundException("User", "id", user_id));
	        
			//TWEETS by user
			 List<Tweets> Usertweets = tweetsRepository.findByUserId(user_id);
			
			//TWEETS  by followed user
			 List<Long> UserFollowedtweetsId = followRepository.findFollowedById(user_id);		
			
			 
			 for (Long userId : UserFollowedtweetsId) {
				 
				 List<Tweets> followedTweet = tweetsRepository.findByUserId(userId);
				
				 Usertweets.addAll(followedTweet);
			}
			 
			 //sorting and mapping to DTO
			 List<TweetsDto> tweetDtoList= Usertweets.stream()
					  .sorted(Comparator.comparing(Tweets::getId).reversed())
					  .map(tweet -> mapToDTO(tweet))
					  .collect(Collectors.toList());
			 
			 //getting USERNAME for EACH TWEET
			 for (TweetsDto userD : tweetDtoList) {
				Long tweetId = userD.getId();
				 Tweets tweet = tweetsRepository.findById(tweetId).orElseThrow(() ->
	                new ResourceNotFoundException("Tweet", "id", tweetId));
				 
				 User fTweeterUser = userRepository.findById(tweet.getUser().getId()).orElseThrow(
			                () -> new ResourceNotFoundException("User", "id", tweet.getUser().getId()));
				 //comments of each TWEET
				 List<Comment> allTweetComments = commentRepository.findByTweetId(tweetId);
				 if(allTweetComments!=null) {
					 List<String> listOfComments = new LinkedList<String>();
					 for (Comment commentId : allTweetComments) {
						 String allTweetCommentMsg  = commentId.getCommentMsg();
						 listOfComments.add(allTweetCommentMsg);
					 }
					
					 userD.setComments(listOfComments);
				 }
				
				 userD.setUsername(fTweeterUser.getName()); 
			}	
			 
			 
			 return tweetDtoList;
		}
		//END :: A LIST OF TWEETS FROM THE USER AND THOSE THEY FOLLOW FOR FEEDS PAGE
		
		
		
		//START :: GET TWEET BY ID
		@Override
		public TweetsDto getTweetById(Long user_id, Long tweetId) {
			   // retrieve user entity by id
	        User user = userRepository.findById(user_id).orElseThrow(
	                () -> new ResourceNotFoundException("User", "id", user_id));
	
	        // retrieve TWEET by id
	        Tweets tweet = tweetsRepository.findById(tweetId).orElseThrow(() ->
	                new ResourceNotFoundException("Tweet", "id", tweetId));
	
	        if(!tweet.getUser().getId().equals(user.getId())){
	            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Tweet does not belong to user");
	        }
	
	        return mapToDTO(tweet);
		}
		//END :: GET TWEET BY ID
		
		
		//START :: UPDATE TWEET
		@Override
		public TweetsDto updateTweet(Long user_id, long tweetId, TweetsDto tweetRequest) {
			// retrieve user entity by id
	        User user = userRepository.findById(user_id).orElseThrow(
	                () -> new ResourceNotFoundException("User", "id", user_id));
	
	        // retrieve TWEET by id
	        Tweets tweet = tweetsRepository.findById(tweetId).orElseThrow(() ->
	                new ResourceNotFoundException("Tweet", "id", tweetId));
	
	        if(!tweet.getUser().getId().equals(user.getId())){
	            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Tweet does not belongs to user");
	        }
	
	        tweet.setContent(tweetRequest.getContent());
	        tweet.setImage(tweetRequest.getImage());
	        Tweets updatedTweet = tweetsRepository.save(tweet);
	        return mapToDTO(updatedTweet);
		}
		//END :: UPDATE TWEET

		
		//START :: DELETE TWEET
		@Override
		public void deleteTweet(Long user_id, Long tweetId) {
			 // retrieve user entity by id
	        User user = userRepository.findById(user_id).orElseThrow(
	                () -> new ResourceNotFoundException("User", "id", user_id));
	
	        // retrieve TWEET by id
	        Tweets tweet = tweetsRepository.findById(tweetId).orElseThrow(() ->
	                new ResourceNotFoundException("Tweet", "id", tweetId));
	
	        if(!tweet.getUser().getId().equals(user.getId())){
	            throw new BlogAPIException(HttpStatus.BAD_REQUEST, "Tweet does not belongs to user");
	        }
	
	        tweetsRepository.delete(tweet);
			
		}
		//END :: DELETE TWEET
	
		
	    private TweetsDto mapToDTO(Tweets tweets){
	    	TweetsDto tweetsDto = modelMapper.map(tweets, TweetsDto.class);
	
	        return  tweetsDto;
	    }
	
	    private Tweets mapToEntity(TweetsDto tweetsDto){
	    	Tweets tweets = modelMapper.map(tweetsDto, Tweets.class);
	        return  tweets;
	    }

}
