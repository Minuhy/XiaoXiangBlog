package minuhy.xiaoxiang.blog.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 字符串正则验证工具
 * @author y17mm
 * 创建时间:2023-2-14 1:11:11
 */
public class VerifyUtil {

	private static final Logger log = LoggerFactory.getLogger(VerifyUtil.class);


    /**
     * 验证一个字符串符不符合正则表达式
     *
     * @param json    JSON数据
     * @param key     键
     * @param pattern 正则表达式
     * @return 如果符合返回true，否则返回false
     */
    public static  boolean verifyStringByRegEx(String str, String pattern) {
    	try {
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(str);
            return m.find();
        } catch (PatternSyntaxException patternSyntaxException) {
            log.error("正则表达式解析错误：" + pattern, patternSyntaxException);
            return false;
        }
    }
}
