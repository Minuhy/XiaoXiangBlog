package minuhy.xiaoxiang.blog.bean.user;

import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.database.UserDb;
import minuhy.xiaoxiang.blog.entity.UserEntity;
import minuhy.xiaoxiang.blog.util.TimeUtil;
/**
 * 用户相关信息
 * @author y17mm
 *
 */
public class UserBean {
	private static final Logger log = LoggerFactory.getLogger(UserBean.class);
	private static final int UPDATE_TIME = 10000;
	
	long lastUpdateTime = 0;
	
	
	int id; // ID，自动生成，唯一
	String account; // 账号，登录用，不可修改
	String nick; // 昵称
	String signature; // 签名
	int avatar; // 头像ID
	int role; // 角色，0：普通，1：管理员
	private String passwd;
	HttpSession session;
	
	public UserBean() {}
	
    public UserBean(int id, String account,int role, String nick, String signature, int avatar,HttpSession session) {
        this.id = id;
        this.account = account;
        this.nick = nick;
        this.signature = signature;
        this.avatar = avatar;
        this.role = role;
        this.session = session;
    }
	
    private void update() {
    	long currentTime = TimeUtil.getTimestampMs();
    	if(currentTime - lastUpdateTime > UPDATE_TIME) {
    		try {
				if(this.passwd!=null) {
					if(!this.passwd.equals(getData(String.valueOf(id)))) {
						if(session!=null) {
							session.removeAttribute(SessionAttributeNameConfig.USER_INFO);
						}
						if(DebugConfig.isDebug) {
							log.debug("用户登录已过期");
						}
					}
				}
			} catch (SQLException e) {
				if(DebugConfig.isDebug) {
					log.error("重新获取数据时出错");
				}
				if(session!=null) {
					session.removeAttribute(SessionAttributeNameConfig.USER_INFO);
				}
			}
    		lastUpdateTime = currentTime;
    	}
    }
    
	public int getId() {
		return id;
	}
	public String getAccount() {
		return account;
	}
	public String getNick() {
		update();
		return nick;
	}
	public String getSignature() {
		update();
		return signature;
	}
	public int getAvatar() {
		update();
		return avatar;
	}
	
	public int getRole() {
		update();
		return role;
	}

	public void updateProfile(int avatar,String nick,String signature) {
		update();
        this.nick = nick;
        this.signature = signature;
        this.avatar = avatar;
	}
	
	public int getIdByAccount(String account) {
		UserDb userDb = new UserDb();
		try {
			return userDb.getIdByAccount(account);
		} catch (SQLException e) {
			e.printStackTrace();
			log.warn("查询用户ID时出错");
			return -1;
		}
	}
	
	public String getData(String id) throws SQLException {
        if (DebugConfig.isDebug) {
            log.debug("获取用户：{}",id);
        }
		// 查出用户信息
    	UserDb userDb = new UserDb();
    	UserEntity userEntity = userDb.getUserById(id);
    	if(userEntity != null) {
            if (DebugConfig.isDebug) {
                log.debug("用户：{}，头像：{}",userEntity.getAccount(), userEntity.getAvatar());
            }
    		this.id = userEntity.getId(); // ID，自动生成，唯一
    		this.account= userEntity.getAccount(); // 账号，登录用，不可修改
    		this.nick= userEntity.getNick(); // 昵称
    		this.signature= userEntity.getSignature(); // 签名
    		this.avatar= userEntity.getAvatar(); // 头像ID
    		this.role = userEntity.getRole();
    		return userEntity.getPasswd();
    	}else {
    		try {
    			this.id = Integer.parseInt(id);
    		}catch (NumberFormatException e) {
    			this.id = -1;
			}
            this.account = "null";
            this.nick = "用户已注销";
            this.signature = "";
            this.avatar = 0;
            this.role = 0;
            if (DebugConfig.isDebug) {
                log.debug("用户获取失败：{}",this.avatar);
            }
    	}
    	return null;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
