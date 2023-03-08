package minuhy.xiaoxiang.blog.util;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher; 
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;


/**
 * �ı�����
 * @author y17mm
 * ����ʱ��:2023-2-17 23:16:11
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

        return s.substring(0,len-1)+"��";
    }
    
    /**
     * �ѳ����ֱ�Ϊ ����ʼ....������ �ĸ�ʽ
     * @param s ����
     * @param len ѹ���󳤶�
     * @return ѹ��������
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
            // ���ȿ���β�ͣ���ֻ��������ʱ�� n...
            b-=1;
        }

        // System.out.println(a+":"+b);

        String h = s.substring(0,a);
        String t = s.substring(s.length()-b);

        return h + "��" + t;
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

        return "��" + s.substring(s.length()-len+1);
    }

    /**
     * �����obj�ַ������򷵻��ַ��������򷵻ؿ�
     * @param obj Ҫ�жϵĶ���
     * @return null or obj
     */
    public static String isString(Object obj){
        return isString(obj,null);
    }

    /**
     * ���obj���ַ������򷵻��ַ��������򷵻�def
     * @param obj Ҫ�жϵĶ���
     * @param def Ĭ��ֵ
     * @return def or ��string��obj
     */
    public static String isString(Object obj,String def){
        if (obj!=null){
            if (obj instanceof String){
            	String s = (String) obj;
            	if(DebugConfig.isDebug) {
            		log.debug("����ַ�����{}",s);
            	}
                return s;
            }
        }
    	if(DebugConfig.isDebug) {
    		log.debug("Ĭ���ַ�����{}",def);
    	}
        return def;
    }
    /**
     * ���html��ʽ��ֻ�����ı�
     * @param htmlStr
     * @return
     */
    public static String delHtmlTag(String htmlStr){ 
        String regEx_script="<script[^>]*?>[\\s\\S]*?<\\/script>"; //����script��������ʽ 
        String regEx_style="<style[^>]*?>[\\s\\S]*?<\\/style>"; //����style��������ʽ 
        String regEx_html="<[^>]+>"; //����HTML��ǩ��������ʽ 
         
        Pattern p_script=Pattern.compile(regEx_script,Pattern.CASE_INSENSITIVE); 
        Matcher m_script=p_script.matcher(htmlStr); 
        htmlStr=m_script.replaceAll(""); //����script��ǩ 
         
        Pattern p_style=Pattern.compile(regEx_style,Pattern.CASE_INSENSITIVE); 
        Matcher m_style=p_style.matcher(htmlStr); 
        htmlStr=m_style.replaceAll(""); //����style��ǩ 
         
        Pattern p_html=Pattern.compile(regEx_html,Pattern.CASE_INSENSITIVE); 
        Matcher m_html=p_html.matcher(htmlStr); 
        htmlStr=m_html.replaceAll(""); //����html��ǩ 
 
        return htmlStr.trim(); //�����ı��ַ��� 
    }
}
