package minuhy.xiaoxiang.blog.util;

import com.alibaba.fastjson.JSONObject;
/**
 * Json����ģ�幤�ߣ��淶���ظ�ʽ
 * ����ʱ��:2023-2-15 1:37:17
 */
public class ResultUtil {
	public static final int SUCCESS = 200; // �����ɹ�
    public static final int FAIL = 400; // ����ʧ��
    public static final int ERROR = 500; // �����Ҫ��ת��

    int code;
    String msg;
    Object data;

    public ResultUtil(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * �ɹ� ������
     */
    public static ResultUtil success(){
        return success(null);
    }

    /**
     * �ɹ�����һ����Ϣ
     * @param msg ��Ϣ����
     */
    public static ResultUtil success(String msg){
        return success(msg,null);
    }

    /**
     * �ɹ�����һ������
     * @param data ����
     */
    public static ResultUtil success(Object data){
        return success(null,data);
    }

    /**
     * �ɹ�����һ�����ݺ�һ����Ϣ
     * @param msg ��Ϣ����
     * @param data ��Ϣ����
     */
    public static ResultUtil success(String msg, Object data){
        return result(SUCCESS,msg,data);
    }

    /**
     * ʧ�� ������
     */
    public static ResultUtil fail(){
        return fail(null);
    }

    /**
     * ʧ�ܣ���һ����Ϣ
     * @param msg ��Ϣ����
     */
    public static ResultUtil fail(String msg){
        return fail(msg,null);
    }

    /**
     * ʧ�ܣ���һ������
     * @param data ����
     */
    public static ResultUtil fail(Object data){
        return fail(null,data);
    }

    /**
     * ʧ�ܣ���һ�����ݺ�һ����Ϣ
     * @param msg ��Ϣ����
     * @param data ��Ϣ����
     */
    public static ResultUtil fail(String msg, Object data){
        return result(FAIL,msg,data);
    }


    /**
     * ���� ������
     */
    public static ResultUtil error(){
        return error(null);
    }

    /**
     * ���󣬴�һ����Ϣ
     * @param msg ��Ϣ����
     */
    public static ResultUtil error(String msg){
        return error(msg,null);
    }

    /**
     * ���󣬴�һ������
     * @param data ����
     */
    public static ResultUtil error(Object data){
        return error(null,data);
    }

    /**
     * ���󣬴�һ�����ݺ�һ����Ϣ
     * @param msg ��Ϣ����
     * @param data ��Ϣ����
     */
    public static ResultUtil error(String msg, Object data){
        return result(ERROR,msg,data);
    }

    /**
     * ����һ����Ϣ
     * @param code ״̬��
     * @param msg ��Ϣ
     * @param data ����
     * @return ��Ϣ
     */
    public static ResultUtil result(int code,String msg, Object data){
        return new ResultUtil(code,msg,data);
    }


    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("code",this.code);
        json.put("msg",this.msg);
        json.put("data",this.data);
        return json.toJSONString();
    }
}
