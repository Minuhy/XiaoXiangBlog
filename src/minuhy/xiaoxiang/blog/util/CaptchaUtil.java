package minuhy.xiaoxiang.blog.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;
/**
 * 生成一张验证码
 * 创建时间:2023-2-13 10:23:06
 */
public class CaptchaUtil {
	public class CaptchaInfo{
		public String formula;
		public String result;
		public BufferedImage image;
	}
	
	static CaptchaUtil captchaUtil;
	
	//定义随机数
	private static final Random random = new Random();

	// 字体
	private static Font font;
	
	/**
	 * 获取随机颜色
	 * @return 一个随机的颜色
	 */
	public Color getColor() {
	
	    int red = random.nextInt(200);
	    int green = random.nextInt(200);
	    int blue = random.nextInt(200);
	
	    return new Color(red, green, blue);
	}

	/**
	 * 生成验证码
	 * @return 验证码结果集
	 */
	public CaptchaInfo GgenerateCaptcha(int width,int height,int line,int fontSize,String fontName) {
	    
		// 初始化字体
		if(font==null 
	    		|| font.getSize() != fontSize
	    		|| !(font.getFontName().equals(fontName))
	    		) {
	    	font = new Font(fontName, Font.PLAIN, fontSize);
	    }
	    
		// 获取验证方案
		CaptchaInfo info = new CaptchaInfo();
		String formula = "0/7="; // 公式
	    String result = "0"; // 结果
	    {
	        int a, b;
	
	        a = random.nextInt(10);
	        b = random.nextInt(10);
	
	        switch (random.nextInt(3)) {
	            case 0:
	                // 加法
	                formula = a + "加" + b;
	                result = String.valueOf(a + b);
	                break;
	            case 1:
	                // 减法
	                if (a < b) {
	                    int t = a;
	                    a = b;
	                    b = t;
	                }
	                formula = a + "减" + b;
	                result = String.valueOf(a - b);
	                break;
	            case 2:
	                formula = a + "乘" + b;
	                result = String.valueOf(a * b);
	                break;
	        }
	    }
	
	    // 验证码放入 返回结果集
	    info.result = result;
	    info.formula = formula;
	
	
	    // 画板
	    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	    // 画笔
	    Graphics pen = image.getGraphics();
	    // 字体
	    pen.setFont(font);
	
	    // 随机x
	    int xRandom = (width - (fontSize * formula.length())) / formula.length();
	    if (xRandom < 1) {
	        xRandom = 1;
	    }
	
	    // 随机y
	    int yRandom = height - fontSize;
	    if (yRandom < 1) {
	        yRandom = 1;
	    }
	
	    int xOffset = width / formula.length();
	
	    // 绘制图片
	    for (int i = 0; i < formula.length(); i++) {
	        // 颜色
	        pen.setColor(getColor());
	
	        // 画字
	        pen.drawString(
	                String.valueOf(formula.charAt(i)),
	                i * xOffset + random.nextInt(xRandom),
	                fontSize + random.nextInt(yRandom)
	        );
	    }
	
	    for (int i = 0; i < line; i++) {
	        // 颜色
	        pen.setColor(getColor());
	        // 画线
	        pen.drawLine(random.nextInt(width), random.nextInt(height), random.nextInt(width), random.nextInt(height));
	    }
	
	    // 打包图片流
	    info.image = image;
	    
	    return info;
	}
}