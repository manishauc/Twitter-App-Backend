package com.springboot.blog.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowUserDto {
	
   private long id;
   
   private boolean msg_followed;
   


	public boolean isMsg_followed() {
	return msg_followed;
}

		public void setMsg_followed(boolean msg_followed) {
	this.msg_followed = msg_followed;
}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
			
}
