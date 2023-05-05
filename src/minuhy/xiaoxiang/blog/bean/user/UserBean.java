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
 * �û������Ϣ
 * @author y17mm
 *
 */
public class UserBean {
	private static final Logger log = LoggerFactory.getLogger(UserBean.class);
	private static final int UPDATE_TIME = 10000;
	
	long lastUpdateTime = 0;
	
	
	int id; // ID���Զ����ɣ�Ψһ
	String account; // �˺ţ���¼�ã������޸�
	String nick; // �ǳ�
	String signature; // ǩ��
	int avatar; // ͷ��ID
	int role; // ��ɫ��0����ͨ��1������Ա
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
							log.debug("�û���¼�ѹ���");
						}
					}
				}
			} catch (SQLException e) {
				if(DebugConfig.isDebug) {
					log.error("���»�ȡ����ʱ����");
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
			log.warn("��ѯ�û�IDʱ����");
			return -1;
		}
	}
	
	public String getData(String id) throws SQLException {
        if (DebugConfig.isDebug) {
            log.debug("��ȡ�û���{}",id);
        }
		// ����û���Ϣ
    	UserDb userDb = new UserDb();
    	UserEntity userEntity = userDb.getUserById(id);
    	if(userEntity != null) {
            if (DebugConfig.isDebug) {
                log.debug("�û���{}��ͷ��{}",userEntity.getAccount(), userEntity.getAvatar());
            }
    		this.id = userEntity.getId(); // ID���Զ����ɣ�Ψһ
    		this.account= userEntity.getAccount(); // �˺ţ���¼�ã������޸�
    		this.nick= userEntity.getNick(); // �ǳ�
    		this.signature= userEntity.getSignature(); // ǩ��
    		this.avatar= userEntity.getAvatar(); // ͷ��ID
    		this.role = userEntity.getRole();
    		return userEntity.getPasswd();
    	}else {
    		try {
    			this.id = Integer.parseInt(id);
    		}catch (NumberFormatException e) {
    			this.id = -1;
			}
            this.account = "null";
            this.nick = "�û���ע��";
            this.signature = "";
            this.avatar = 0;
            this.role = 0;
            if (DebugConfig.isDebug) {
                log.debug("�û���ȡʧ�ܣ�{}",this.avatar);
            }
    	}
    	return null;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
}
