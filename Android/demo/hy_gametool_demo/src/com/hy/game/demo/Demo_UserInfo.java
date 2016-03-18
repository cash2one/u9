package com.hy.game.demo;

public class Demo_UserInfo{
		private String userId;
		private String userName;
		private String token;
		private LoginCallback loginCallback;
		
		public void setUserId(String userId){
			this.userId = userId;
		}
		public String getUserId(){
			return userId;
		}
		public void setUserName(String userName){
			this.userName = userName;
		}
		public String getUserName(){
			return userName;
		}public void setToken(String token){
			this.token = token;
		}
		public String getToken(){
			return token;
		} 
		public void setLoginCallback(LoginCallback loginCallback){
			this.loginCallback = loginCallback;
		}
		public LoginCallback getLoginCallback(){
			return this.loginCallback;
		}
	}