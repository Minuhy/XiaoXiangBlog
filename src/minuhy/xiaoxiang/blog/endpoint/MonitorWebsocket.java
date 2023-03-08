package minuhy.xiaoxiang.blog.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.util.TimeUtil;

/**
 * 
 * @author y17mm
 *
 */
@ServerEndpoint("/ep/monitor")
public class MonitorWebsocket {
	private static final Logger log = LoggerFactory.getLogger(MonitorWebsocket.class);
	private static List<Session> sessions = new ArrayList<>();

	private Session session;
	private boolean isStart;
	
	private static long lastSendTime = 0;
	
	/**
	 * 消息最快1s一次
	 * @return 是否可发送消息
	 */
	public static boolean isSendable() {
		long curTime = TimeUtil.getTimestampMs();
		if((curTime - lastSendTime) > 1000) {
			return true;
		}
		return false;
	}
	
	/**
	 * 建立链接
	 * 
	 * @param session
	 */
	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		this.isStart = false;
	}

	/**
	 * 断开链接
	 */
	@OnClose
	public void onClose() {
		if(this.isStart) {
			MonitorWebsocket.sessions.remove(this.session);
		}
		this.isStart = false;
		this.session = null;
	}

	/**
	 * 收到来自客户端的消息
	 * 
	 * @param message
	 * @param session
	 */
	@OnMessage
	public void onMessage(String message, Session session) {
		if(session.equals(this.session)) {
			if(message.equals("start monitor")) {
				MonitorWebsocket.sessions.add(session);
				this.isStart = true;
			}
		}
	}

	/**
	 * 发生错误
	 * 
	 * @param session
	 * @param error
	 */
	@OnError
	public void onError(Session session, Throwable error) {
		if(DebugConfig.isDebug) {
			log.debug("WebSocket发生错误：{}，{}",session,error);
		}
		this.onClose();
	}

	/**
	 * 发送消息
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendMessage(String message) throws IOException {
		this.session.getBasicRemote().sendText(message);
	}
	
	/**
	 * 广播消息
	 * @param message
	 */
	public static void broadcastMessage(String message) {
		if(!isSendable()) {
			return;
		}
		
		try {
			int i=0;
			while(true) {
				try {
					if(MonitorWebsocket.sessions instanceof ArrayList) {
						if(i<MonitorWebsocket.sessions.size()) {
							Session session = MonitorWebsocket.sessions.get(i);
							if(session.isOpen()) {
								session.getBasicRemote().sendText(message);
							}
							i++;
						}else {
							break;
						}
					}else {
						break;
					}
				}catch(IllegalStateException e) {
					if(DebugConfig.isDebug) {
						log.debug("WebSocket发送消息客户端时正忙：{}",e);
					}
				}catch (IOException e) {
					if(DebugConfig.isDebug) {
						log.debug("WebSocket发送消息时出错：{}",e);
					}
				}catch (Exception e) {
					if(DebugConfig.isDebug) {
						log.error("发送web Socket广播时出错:{}",e);
					}
				}
			}
			lastSendTime = TimeUtil.getTimestampMs();
		}catch (Exception e) {
			log.error("发送web Socket广播时出错:{}",e);
		}
	}

}
