package minuhy.xiaoxiang.blog.bean.admin;

import minuhy.xiaoxiang.blog.database.AdminDb;

public class CountBean {
	String newUserCount;
	String newBlogCount;
	String newCommentCount;
	String newLikeCount;
	
	public CountBean() {
		
	}
	
	
	
	public String getNewUserCount() {
		return newUserCount;
	}



	public String getNewBlogCount() {
		return newBlogCount;
	}



	public String getNewCommentCount() {
		return newCommentCount;
	}



	public String getNewLikeCount() {
		return newLikeCount;
	}



	public void getData() {
		AdminDb adminDb = new AdminDb();
		int blogCount = adminDb.getNewBlogMonthCount();
		int userCount = adminDb.getNewUserMonthCount();
		int commentCount = adminDb.getNewCommentMonthCount();
		int likeCount = adminDb.getNewLikeMonthCount();
		
		 this.newUserCount = String.valueOf(userCount);
		 this.newBlogCount = String.valueOf(blogCount);
		 this.newCommentCount = String.valueOf(commentCount);
		 this.newLikeCount = String.valueOf(likeCount);
	}
}
