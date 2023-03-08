package minuhy.xiaoxiang.blog.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * �ַ���������֤����
 * @author y17mm
 * ����ʱ��:2023-2-14 1:11:11
 */
public class VerifyUtil {

	private static final Logger log = LoggerFactory.getLogger(VerifyUtil.class);


    /**
     * ��֤һ���ַ�����������������ʽ
     *
     * @param json    JSON����
     * @param key     ��
     * @param pattern ������ʽ
     * @return ������Ϸ���true�����򷵻�false
     */
    public static  boolean verifyStringByRegEx(String str, String pattern) {
    	try {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(str);
            return m.find();
        } catch (PatternSyntaxException patternSyntaxException) {
            log.error("������ʽ��������" + pattern, patternSyntaxException);
            return false;
        }
    }
}
