package minuhy.xiaoxiang.blog.config;

public class SessionAttributeNameConfig {
	public static final String CAPTCHA = "captcha";
	public static final String USER_INFO = "userInfo";
	
	public static final String GET_BLOG_TIME = "getBlogTime";
	public static final String GET_BLOG_ID = "getBlogId";
	public static final String GET_BLOG_USER_ID = "getBlogUserId";
	
	public static final String LOGIN_PRE_PAGE = "loginPrePage";
	public static final String LOGIN_PRE_PAGE_NAME = "loginPrePageName";
	
	
	/***************下面这些玩意越来越多的时候就不好管理了，应该作为一个任务来来缓存***********/
	
	// 注册到登录的页面
	public static final String LOGIN_PAGE = "loginPage";
	
	
	// 个人资料修改页面
	public static final String PROFILE_AVATAR = "profileAvatar";
	public static final String PROFILE_NICK = "profileNick";
	public static final String PROFILE_SIGNATURE = "profileSignature";
	public static final String PROFILE_SEX = "profileSex";
	public static final String PROFILE_HOMETOWN = "profileHometown";
	public static final String PROFILE_LINK = "profileLink";
	
	// 登录页面
	public static final String LOGIN_ACC = "loginAcc";
	public static final String LOGIN_PWD = "loginPwd";
	public static final String LOGIN_REME = "loginReme";
	
	// 阅读页面评论
	public static final String COMMENT_CONTENT = "commentContent";
	
}
