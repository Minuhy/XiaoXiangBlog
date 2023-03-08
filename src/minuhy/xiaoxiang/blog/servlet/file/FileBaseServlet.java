package minuhy.xiaoxiang.blog.servlet.file;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import minuhy.xiaoxiang.blog.servlet.BaseHttpServlet;
/**
 * 文件基础类
 * @author y17mm
 * 创建时间:2023-02-27 01:03 
 */
public class FileBaseServlet  extends BaseHttpServlet{
	protected static final String DEFAULT_FILE_PATH = "upload";
	protected static final int DEFAULT_MAX_FILE_SIZE = 10000 * 1024;
	protected static final int DEFAULT_MAX_MEMORY_SIZE = 5000 * 1024;
	protected static final boolean DEFAULT_IS_TOM_DIRECTORY = true;
	protected static final boolean DEFAULT_IS_ONLY_PICTURE = true;

	/**
	 * UID
	 */
	protected static final long serialVersionUID = 2772132795810487909L;
	private static final Logger log = LoggerFactory.getLogger(FileBaseServlet.class);
	protected static String uploadFilePath;
	protected static String cacheFilePath;
	
	protected static boolean init = false;
	
	protected static int maxFileSize;
	protected static int maxMemorySize;
	protected static boolean isOnlyPicture;

	@Override
	public void init(ServletConfig config) throws ServletException {
		if(init) {
			log.info("已初始化成功");
			return;
		}
		
		boolean isTomDirectory = Boolean.parseBoolean(
				config.getInitParameter("is-tom-path")==null?
						String.valueOf(DEFAULT_IS_TOM_DIRECTORY):
							config.getInitParameter("is-tom-path")
				);
		
		isOnlyPicture = Boolean.parseBoolean(
				config.getInitParameter("only-picture")==null?
						String.valueOf(DEFAULT_IS_ONLY_PICTURE):
							config.getInitParameter("only-picture")
				);
		
		uploadFilePath = getDirectory(config,"file-upload-path",DEFAULT_FILE_PATH,isTomDirectory);
		cacheFilePath = getDirectory(config,"cache-path",DEFAULT_FILE_PATH,isTomDirectory);

	    log.info("uploadFilePath 文件上传路径 " + uploadFilePath);
	    log.info("cacheFilePath 文件缓存路径 " + cacheFilePath);
		
	    if(!checkAndCreateDirectory(uploadFilePath) 
	    		|| !checkAndCreateDirectory(cacheFilePath) ) {
	    	log.error("文件上传目录初始化失败");
	    	return;
	    }

	    maxFileSize = getInitParam(config,"max-file-size",DEFAULT_MAX_FILE_SIZE);
	    maxMemorySize = getInitParam(config,"max-memory-size",DEFAULT_MAX_MEMORY_SIZE);
	    
	    log.info("maxFileSize 文件最大大小 " + maxFileSize);
	    log.info("maxMemorySize 文件最大内存大小 " + maxMemorySize);
	    
	    init = true;
	}

    /**
     * 获取上传文件的md5
     *
     * @param file 文件
     * @return MD5值
     */
    public String getMd5(FileItem fi) {
        try {
            byte[] uploadBytes = fi.get();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e) {
            log.error("计算文件MD5时出错：{} - {}",e.toString(), e);
        }
        return null;
    }
	
	/**
	 * 将不太合理的目录转换成合理的目录
	 * @param path 目录
	 * @return
	 */
	public String fixDirectory(String path) {
		path = path.replace("\\", "/");
		if(!path.startsWith("/") && path.charAt(1)!=':') {
			path = "/"+path;
		}
		if(!path.endsWith("/")) {
			path = path + "/";
		}
		return path;
	}
	
	/**
	 * 获取路径
	 * @param config
	 * @param paramName
	 * @param defaultValue
	 * @param isTomDirectory
	 * @return
	 */
	public String getDirectory(ServletConfig config, String paramName,String defaultValue, boolean isTomDirectory) {
		String dir = config.getInitParameter(paramName);
	    if(dir == null) {
	    	if(isTomDirectory) {
	    		// dir = config.getServletContext().getRealPath(fixDirectory(defaultValue))
	    		dir = System.getProperty("catalina.home")+"/appdata"+fixDirectory(defaultValue);
	    	}else {
	    		dir = fixDirectory(defaultValue);
	    	}
	    }else {
	    	dir = fixDirectory(dir);
	    	if(isTomDirectory) {
	    		dir = System.getProperty("catalina.home")+"/appdata"+dir;
	    		// dir = config.getServletContext().getRealPath(dir);
	    	}
	    }
	    return dir;
	}
	
	/**
	 * 检查路径，如果不存在会创建
	 * @param path
	 * @return
	 */
	public boolean checkAndCreateDirectory(String path) {
		File file = new File(path);
	    if(!file.isDirectory()) {
	    	if(!file.mkdirs()) {
	    		log.error("创建文件夹失败");
	    		return false;
	    	}else {
	    		log.info("创建文件夹：" + file.getPath());
	    	}
	    }
	    return true;
	}
	
	/**
	 * 获取整型参数
	 * @param config
	 * @param paramName
	 * @param defaultValue
	 * @return
	 */
	public int getInitParam(ServletConfig config, String paramName, int defaultValue) {
		String val = config.getInitParameter(paramName);
		try {
			return Integer.parseInt(val);
		}catch (NumberFormatException e) {
			return defaultValue;
		}
	}
}
