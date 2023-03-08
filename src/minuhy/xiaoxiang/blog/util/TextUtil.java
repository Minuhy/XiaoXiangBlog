package minuhy.xiaoxiang.blog.util;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;


/**
 * 文本工具
 * @author y17mm
 * 创建时间:2023-2-17 23:16:11
 */
public class TextUtil {	
	private static final Logger log = LoggerFactory.getLogger(TextUtil.class);

	public static boolean isEmpty(String s){
        return s == null || s.equals("");
    }
	
	public static long getStringLenByUtf8(String str) {
		    return str.getBytes(StandardCharsets.UTF_8).length;
	}
    public static String maxLen(String s,int len){
        if (len<1){
            return "";
        }

        if (null==s){
            return "";
        }

        if (s.length()<=len){
            return s;
        }

        return s.substring(0,len-1)+"…";
    }
    
    /**
     * 把长文字变为 “开始....结束” 的格式
     * @param s 文字
     * @param len 压缩后长度
     * @return 压缩后文字
     */
    public static String maxLenJustify(String s,int len){
        if (len<1){
            return "";
        }

        if (null==s){
            return "";
        }

        if (s.length()<=len){
            return s;
        }

        int a = len/2;
        int b = len - a;

        // System.out.println(a+":"+b);

        if (a>b){
            a-=1;
        }else if(b>=a){
            // 优先砍掉尾巴，在只有两个的时候 n...
            b-=1;
        }

        // System.out.println(a+":"+b);

        String h = s.substring(0,a);
        String t = s.substring(s.length()-b);

        return h + "…" + t;
    }

    public static String maxLenRight(String s,int len){
        if (len<1){
            return "";
        }

        if (null==s){
            return "";
        }

        if (s.length()<=len){
            return s;
        }

        return "…" + s.substring(s.length()-len+1);
    }

    /**
     * 如果是obj字符串，则返回字符串，否则返回空
     * @param obj 要判断的对象
     * @return null or obj
     */
    public static String isString(Object obj){
        return isString(obj,null);
    }

    /**
     * 如果obj是字符串，则返回字符串，否则返回def
     * @param obj 要判断的对象
     * @param def 默认值
     * @return def or （string）obj
     */
    public static String isString(Object obj,String def){
        if (obj!=null){
            if (obj instanceof String){
            	String s = (String) obj;
            	if(DebugConfig.isDebug) {
            		log.debug("获得字符串：{}",s);
            	}
                return s;
            }
        }
    	if(DebugConfig.isDebug) {
    		log.debug("默认字符串：{}",def);
    	}
        return def;
    }
    /**
     * 清除html格式，只保留文本
     * @param htmlStr
     * @return
     */
    public static String delHtmlTag(String htmlStr){ 
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //定义script的正则表达式 
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //定义style的正则表达式 
        String regEx_html="<[^>]+>"; //定义HTML标签的正则表达式 
         
        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
        Matcher m_script=p_script.matcher(htmlStr); 
        htmlStr=m_script.replaceAll(""); //过滤script标签 
         
        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
        Matcher m_style=p_style.matcher(htmlStr); 
        htmlStr=m_style.replaceAll(""); //过滤style标签 
         
        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
        Matcher m_html=p_html.matcher(htmlStr); 
        htmlStr=m_html.replaceAll(""); //过滤html标签 
 
        return htmlStr.trim(); //返回文本字符串 
    }
}
