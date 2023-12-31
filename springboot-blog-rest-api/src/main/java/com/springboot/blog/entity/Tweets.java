package com.springboot.blog.entity;



import java.util.Date;

import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(
        name = "tweets"
)
public class Tweets {
	
	@Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
	private long id;
	
	@Length(max=280, message = "Length must be between 0 to 280 characters")
	@Column(name = "content", nullable = false)
	private String content;
	
	@Lob
	@Column(name = "image_data")
	private byte[] imageData;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
	
	private String image;
	
	private long retweets;
	
	private long likes;
	
	@Column(name = "created_at")
	private Date created_at = new Date();

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public long getRetweets() {
		return retweets;
	}

	public void setRetweets(long retweets) {
		this.retweets = retweets;
	}

	public Date getCreated_at() {
		return created_at;
	}

	public void setCreated_at(Date created_at) {
		this.created_at = created_at;
	}

	public long getLikes() {
		return likes;
	}

	public void setLikes(long likes) {
		this.likes = likes;
	}
	
	
}
