package minuhy.xiaoxiang.blog.util;

import com.alibaba.fastjson.JSONObject;
/**
 * Json返回模板工具，规范返回格式
 * 创建时间:2023-2-15 1:37:17
 */
public class ResultUtil {
	public static final int SUCCESS = 200; // 操作成功
    public static final int FAIL = 400; // 操作失败
    public static final int ERROR = 500; // 大多数要跳转的

    int code;
    String msg;
    Object data;

    public ResultUtil(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功 无数据
     */
    public static ResultUtil success(){
        return success(null);
    }

    /**
     * 成功，带一个消息
     * @param msg 消息内容
     */
    public static ResultUtil success(String msg){
        return success(msg,null);
    }

    /**
     * 成功，带一个数据
     * @param data 数据
     */
    public static ResultUtil success(Object data){
        return success(null,data);
    }

    /**
     * 成功，带一个数据和一个消息
     * @param msg 消息内容
     * @param data 消息内容
     */
    public static ResultUtil success(String msg, Object data){
        return result(SUCCESS,msg,data);
    }

    /**
     * 失败 无数据
     */
    public static ResultUtil fail(){
        return fail(null);
    }

    /**
     * 失败，带一个消息
     * @param msg 消息内容
     */
    public static ResultUtil fail(String msg){
        return fail(msg,null);
    }

    /**
     * 失败，带一个数据
     * @param data 数据
     */
    public static ResultUtil fail(Object data){
        return fail(null,data);
    }

    /**
     * 失败，带一个数据和一个消息
     * @param msg 消息内容
     * @param data 消息内容
     */
    public static ResultUtil fail(String msg, Object data){
        return result(FAIL,msg,data);
    }


    /**
     * 错误 无数据
     */
    public static ResultUtil error(){
        return error(null);
    }

    /**
     * 错误，带一个消息
     * @param msg 消息内容
     */
    public static ResultUtil error(String msg){
        return error(msg,null);
    }

    /**
     * 错误，带一个数据
     * @param data 数据
     */
    public static ResultUtil error(Object data){
        return error(null,data);
    }

    /**
     * 错误，带一个数据和一个消息
     * @param msg 消息内容
     * @param data 消息内容
     */
    public static ResultUtil error(String msg, Object data){
        return result(ERROR,msg,data);
    }

    /**
     * 返回一个消息
     * @param code 状态码
     * @param msg 消息
     * @param data 数据
     * @return 消息
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
