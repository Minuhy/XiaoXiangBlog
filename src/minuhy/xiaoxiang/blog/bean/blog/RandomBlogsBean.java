package minuhy.xiaoxiang.blog.bean.blog;

import java.sql.SQLException;

import minuhy.xiaoxiang.blog.database.BlogDb;
import minuhy.xiaoxiang.blog.entity.BlogEntity;

public class RandomBlogsBean {
	BlogBean[] blogBeans;
	
	public RandomBlogsBean() {}

	public BlogBean[] getBlogBeans() {
		return blogBeans;
	}

	public void setBlogBeans(BlogBean[] blogBeans) {
		this.blogBeans = blogBeans;
	}
	
	public void getData(int len) throws SQLException {
		BlogDb blogDb = new BlogDb();
		BlogEntity[] blogEntitys = blogDb.getRandomBlog(len);
		if(blogEntitys!=null && blogEntitys.length > 0) {
			// 有数据
			blogBeans = new BlogBean[blogEntitys.length];
			for(int i=0;i<blogEntitys.length;i++) {
				blogBeans[i] = new BlogBean(blogEntitys[i]);
			}
		}else {
			// 没数据
			blogBeans = new BlogBean[0];
		}
	}
}
