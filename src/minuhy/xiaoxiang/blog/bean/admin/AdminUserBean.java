package minuhy.xiaoxiang.blog.bean.admin;

import minuhy.xiaoxiang.blog.bean.BeanTimeController;
import minuhy.xiaoxiang.blog.bean.PaginationBean;
import minuhy.xiaoxiang.blog.config.DatabaseConfig;
import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.database.AdminDb;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.util.EncryptionUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class AdminUserBean extends BeanTimeController {
	private static final Logger log = LoggerFactory.getLogger(AdminUserBean.class);
	PaginationBean paginationBean;
	Map<Integer, UserEntity[]> cache;


	public AdminUserBean() {
		cache = new HashMap<>();
		paginationBean = new PaginationBean();
	}

	public int getCurrentPage() {
		if (paginationBean == null) {
			return 0;
		}
		return paginationBean.getCurrent();
	}

	public PaginationBean getPaginationBean() {
		return paginationBean;
	}

	public int getTotal() {
		if (paginationBean == null) {
			return 0;
		}
		return paginationBean.getTotal();
	}

	/**
	 * 获取数据
	 * 
	 * @param page 从0开始
	 * @return
	 * @throws SQLException
	 */
	public UserEntity[] getData(int page) throws SQLException {
		boolean refresh = false;
		AdminDb adminDb = new AdminDb();
		if (getTotal() < 1 || isCanRefresh()) {
			// 获取总页数
			refresh = true;
			int n = adminDb.getUserTotal();
			int total = (n / DatabaseConfig.PAGE_ITEM_COUNT) + ((n % DatabaseConfig.PAGE_ITEM_COUNT) == 0 ? 0 : 1);
			paginationBean.setTotal(total);
			paginationBean.setCurrent(page + 1);

			if (DebugConfig.isDebug) {
				log.debug("页数：{}-{}", total, page);
			}
		}

		if (page < 0 || page > getTotal()) {
			if (DebugConfig.isDebug) {
				log.debug("页面数据不正确：{}-{}，{}", 0, getTotal(), page);
			}
			return null;
		}

		// 如果没到刷新时候，并且缓存中有数据
		if (!isCanRefresh() && cache.containsKey(page)) {
			return cache.get(page);
		}

		// 从数据库中拿数据
		UserEntity[] userEntities = adminDb.getUserInfoByPageOrderByTime(page);
		if (userEntities != null && userEntities.length > 0) {
			cache.put(page, userEntities);
		}

		// 设置刷新时间
		if (refresh) {
			refresh();
		}

		return userEntities;
	}

	/**
	 * 编辑用户
	 **/
	public String editUser(HttpServletRequest req) {
		UserEntity entity = new UserEntity();

		String id = req.getParameter("id");
		// int id; // ID，自动生成，唯一
		// String label, String name, String hint, String value
		// editBeans[0] = new EditBean(false, "编号", "id", "编号，不可修改",
		// String.valueOf(userEntity.getId()));
		try {
			int i = Integer.parseInt(id);
			if (i < 1) {
				throw new NumberFormatException("编号范围不受支持");
			}
			entity.setId(i);
		} catch (Exception e) {
			return "编号有误：" + e.getMessage();
		}

		String active = req.getParameter("active");
		// int active; // 账号是否激活：1激活，0禁用
		// String label, String name, String hint, String value
		// editBeans[1] = new EditBean("状态", "active", "1激活，0禁用",
		// userEntity.getActive());
		// editBeans[1].setValChoose(new String[]{"1","激活","0","冻结"});
		if (active == null) {
			return "缺少激活状态参数";
		}
		if (!active.equals("1") && !active.equals("0")) {
			return "激活状态参数错误";
		}
		entity.setActive(Integer.parseInt(active));

		String passwd = req.getParameter("passwd");
		// String passwd; // 密码，登录用，MD5加密
		// String label, String name, String hint, String value
		// editBeans[2] = new EditBean("密码", "passwd", "（未修改）", "");
		// editBeans[2].setType("password");
		if (passwd != null && passwd.length() > 0) {
			if (passwd.length() > 5) {
				// 加密密码
				try {
					UserDb userDb = new UserDb();
					String account = userDb.getAccountById(id);
					if (account == null) {
						throw new Exception("查不到账号");
					}
					passwd = EncryptionUtil.EncodePasswd(account, passwd);
					entity.setPasswd(passwd);
				} catch (Exception e) {
					return "加密密码时出错：" + e.getMessage();
				}
			} else {
				return "密码长度太短";
			}
		} else {
			passwd = null;
		}

		String role = req.getParameter("role");
		// int role; // 角色，0：普通，1：管理员
		// String label, String name, String hint, String value
		// editBeans[3] = new EditBean("角色", "role", "0：普通，1：管理员",
		// userEntity.getRole());
		// editBeans[3].setValChoose(new String[]{"0","普通","1","管理员"});
		if (role == null) {
			return "缺少角色参数";
		}
		if (!role.equals("1") && !role.equals("0")) {
			return "角色参数错误";
		}
		entity.setRole(Integer.parseInt(role));

		String nick = req.getParameter("nick");
		// String nick; // 昵称
		// String label, String name, String hint, String value
		// editBeans[4] = new EditBean("昵称", "nick", "至多24字", userEntity.getNick());
		if (nick == null) {
			return "缺少昵称参数";
		}
		if (nick.length() < 1 || nick.length() > 24) {
			return "昵称长度不正确（1-24字）";
		}
		entity.setNick(nick);

		String signature = req.getParameter("signature");
		// String signature; // 签名
		// String label, String name, String hint, String value
		// editBeans[5] = new EditBean("签名", "signature", "至多60字",
		// userEntity.getSignature());
		if (signature == null) {
			signature = "";
		}
		if (signature.length() > 60) {
			return "签名长度不正确（至多60字）";
		}
		entity.setSignature(signature);

		String sex = req.getParameter("sex");
		// int sex; // 性别，0：未设置，1：男，2：女
		// String label, String name, String hint, String value
		// editBeans[6] = new EditBean("性别", "sex", "0：未设置，1：男，2：女",
		// userEntity.getSex());
		// editBeans[6].setValChoose(new String[]{"0","未设置","1","男","2","女"});
		if (sex == null) {
			return "缺少性别参数";
		}
		if (!sex.equals("1") && !sex.equals("0") && !sex.equals("2")) {
			return "性别参数错误";
		}
		entity.setSex(Integer.parseInt(sex));

		String hometown = req.getParameter("hometown");
		// String hometown; // 家乡
		// String label, String name, String hint, String value
		// editBeans[7] = new EditBean("家乡", "hometown", "至多60字",
		// userEntity.getHometown());
		if (hometown == null) {
			hometown = "";
		}
		if (hometown.length() > 60) {
			return "家乡长度不正确（至多60字）";
		}
		entity.setHometown(hometown);

		String link = req.getParameter("link");
		// String link; // 联系方式
		// String label, String name, String hint, String value
		// editBeans[8] = new EditBean("联系方式", "link", "至多30字", userEntity.getLink());
		if (link == null) {
			link = "";
		}
		if (link.length() > 30) {
			return "联系方式长度不正确（至多30字）";
		}
		entity.setLink(link);

		String avatar = req.getParameter("avatar");
		// int avatar; // 头像ID
		// String label, String name, String hint, String value
		// editBeans[9] = new EditBean("头像编号", "avatar", "1-138",
		// userEntity.getAvatar());
		// editBeans[9].setType("number");
		try {
			int i = Integer.parseInt(avatar);
			if (i < 1 || i > 138) {
				throw new NumberFormatException("头像编号不受支持");
			}
			entity.setAvatar(i);
		} catch (Exception e) {
			return "头像编号有误：" + e.getMessage();
		}

		String blogCount = req.getParameter("blogCount");
		// int blogCount; // 博客数量计数
		// String label, String name, String hint, String value
		// editBeans[10] = new EditBean("博客数量计数", "blogCount", "大于等于 0",
		// userEntity.getBlogCount());
		// editBeans[10].setType("number");
		try {
			int i = Integer.parseInt(blogCount);
			if (i < 0) {
				throw new NumberFormatException("不能为负数");
			}
			entity.setBlogCount(i);
		} catch (Exception e) {
			return "博客数量有误：" + e.getMessage();
		}

		String blogReadCount = req.getParameter("blogReadCount");
		// int blogReadCount; // 博客阅读计数
		// String label, String name, String hint, String value
		// editBeans[11] = new EditBean("博客被阅读计数", "blogReadCount", "大于等于 0",
		// userEntity.getBlogReadCount());
		// editBeans[11].setType("number");
		try {
			int i = Integer.parseInt(blogReadCount);
			if (i < 0) {
				throw new NumberFormatException("不能为负数");
			}
			entity.setBlogReadCount(i);
		} catch (Exception e) {
			return "博客阅读数有误：" + e.getMessage();
		}

		String blogLikeCount = req.getParameter("blogLikeCount");
		// int blogLikeCount; // 博客被点赞计数
		// String label, String name, String hint, String value
		// editBeans[12] = new EditBean("博客被点赞计数", "blogLikeCount", "大于等于 0",
		// userEntity.getBlogLikeCount());
		// editBeans[12].setType("number");
		try {
			int i = Integer.parseInt(blogLikeCount);
			if (i < 0) {
				throw new NumberFormatException("不能为负数");
			}
			entity.setBlogLikeCount(i);
		} catch (Exception e) {
			return "博客点赞数有误：" + e.getMessage();
		}

		AdminDb adminDb = new AdminDb();

		try {
			if (!adminDb.editUser(entity)) {
				return "资料未更改";
			}
		} catch (SQLException e) {
			log.error("写入数据库出错:" + e);
			return "数据库错误";
		}

		if (passwd != null) {
			try {
				if (!adminDb.editUserPasswd(entity)) {
					return "密码未更改";
				}
			} catch (SQLException e) {
				log.error("写入数据库出错:" + e);
				return "数据库错误";
			}
		}

		return null;
	}

}
