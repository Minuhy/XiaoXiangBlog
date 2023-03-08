package minuhy.xiaoxiang.blog.servlet.util;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.config.SessionAttributeNameConfig;
import minuhy.xiaoxiang.blog.util.CaptchaUtil;
import minuhy.xiaoxiang.blog.util.RequestUtil;

/**
 * 验证码
 * 
 * 使用传统方式交互
 * 
 * @author y17mm
 */
@WebServlet("/util/captcha")
public class CaptchaServlet extends HttpServlet {
	
	/**
	 * UID
	 */
	private static final long serialVersionUID = -8665852098389921334L;
	private static final Logger log = LoggerFactory.getLogger(CaptchaServlet.class);
	
	CaptchaUtil captchaUtil;

	@Override
	public void init() throws ServletException {
		captchaUtil = new CaptchaUtil();
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		CaptchaUtil.CaptchaInfo info = 
				captchaUtil.GgenerateCaptcha(80,25,3,14,"宋体");
		
		int t = RequestUtil.getReqParam(req, "t", 0);
		if(DebugConfig.isDebug) {
			log.debug("第{}次获取验证码 {} -> {}", t,info.formula,info.result);
		}
		
		// 验证码放入 session
        HttpSession session = req.getSession();
        session.setAttribute(SessionAttributeNameConfig.CAPTCHA, info.result);
		
		// response对象绘制图片到页面，Servlet输出流进行图片的输出
        ServletOutputStream sos = resp.getOutputStream();
        ImageIO.write(info.image, "png", sos);

        sos.flush();
        sos.close();
	}
	
}
