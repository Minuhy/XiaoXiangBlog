package minuhy.xiaoxiang.blog.entity;

public class MessageEntity {
	int id; // ������Ψһ
    int senderId; // ������ID
    int receiverId; // ������ID
    int state; // �鿴״̬��1�Ѳ鿴��0δ�鿴
    String targetUrl; // Ŀ������
    String title; // ��Ϣ����
    String content; // ��Ϣ����
    int msgType; // ��Ϣ���ͣ�1�ظ���2�ᵽ��3���ޣ�4ϵͳ��Ϣ
    long createTimestamp; // ����ʱ��
    public MessageEntity(){}
    public MessageEntity(int id, int senderId, int receiverId, int state, String targetUrl, String title, String content, int msgType, long createTimestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.state = state;
        this.targetUrl = targetUrl;
        this.title = title;
        this.content = content;
        this.msgType = msgType;
        this.createTimestamp = createTimestamp;
    }

    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getSenderId() {
		return senderId;
	}
	public void setSenderId(int senderId) {
		this.senderId = senderId;
	}
	public int getReceiverId() {
		return receiverId;
	}
	public void setReceiverId(int receiverId) {
		this.receiverId = receiverId;
	}
	public int getState() {
		return state; // 1�Ѳ鿴��0δ�鿴
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getMsgType() {
		return msgType; // ��Ϣ���ͣ�1�ظ���2�ᵽ��3���ޣ�4ϵͳ��Ϣ
	}
	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}
	public long getCreateTimestamp() {
		return createTimestamp;
	}
	public void setCreateTimestamp(long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}
	@Override
    public String toString() {
        return "MessageEntity{" +
                "id=" + id +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", state=" + state +
                ", targetUrl='" + targetUrl + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", msgType=" + msgType +
                ", createTimestamp=" + createTimestamp +
                '}';
    }
}
