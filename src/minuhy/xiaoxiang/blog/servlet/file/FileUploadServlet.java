package minuhy.xiaoxiang.blog.servlet.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.SizeLimitExceededException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import minuhy.xiaoxiang.blog.config.DebugConfig;
import minuhy.xiaoxiang.blog.util.ResultUtil;

/**
 * 文件上传
 * @author xxxy1116
 * 创建时间:2023-02-27 01:49
 */
public class FileUploadServlet extends FileBaseServlet{

	/**
	 * UID
	 */
	private static final long serialVersionUID = 5342015741115399047L;
	private static final Logger log = LoggerFactory.getLogger(FileUploadServlet.class);

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		if(!init) {
			String jsonStr = ResultUtil.fail("系统初始化未完成").toString();
			backJson(jsonStr,response);
			return;
		}
		
		if(getLoginUserBean(request) == null) {
			String jsonStr = ResultUtil.fail("请先登录").toString();
			backJson(jsonStr,response);
			return;
		}
		
		String contentType = request.getContentType();
	    if ((contentType.indexOf("multipart/form-data") >= 0)) {
	    	
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        
	        // 将存储在内存中的最大大小
	        factory.setSizeThreshold(maxMemorySize);
	        
	        // 保存大于最大内存大小的数据的位置
	        factory.setRepository(new File(cacheFilePath));
	        
	        // 创建新的文件上传处理程序
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        
	        // 要上传的最大文件大小
	        upload.setSizeMax(maxFileSize);
	        
	        try {
	            // 解析请求以获取文件项
	            List<FileItem> fileItems = upload.parseRequest(request);
	            
	            // 处理上传的文件项
	            ArrayList<String> names = new ArrayList<>();
	            Iterator<FileItem> i = fileItems.iterator();
	            while (i.hasNext()) {
	                FileItem fi = i.next();
	                if (!fi.isFormField()) {
	                	
	                    // 获取上传的文件参数
	                    String fileName = fi.getName();
	                    
	                    if(DebugConfig.isDebug) {
		                    String fieldName = fi.getFieldName();
		                    boolean isInMemory = fi.isInMemory();
		                    long sizeInBytes = fi.getSize();
		                    log.debug("参数名：{}",fieldName);
		                    log.debug("文件名：{}",fileName);
		                    log.debug("是否在内存：{}",isInMemory);
		                    log.debug("字节大小：{}",sizeInBytes);
	                    }
	                    // 处理文件名
	                    String suffixName = fileName.substring(fileName.lastIndexOf('.'));
	                    if(isOnlyPicture) {
	                    	if(!suffixName.equals(".jpg") 
	                    			&& !suffixName.equals(".png")
	                    			&& !suffixName.equals(".gif")
	                    			&& !suffixName.equals(".webp")
	                    			&& !suffixName.equals(".ico")
	                    			) {
	                    		// 文件类型不支持
	                    		String jsonStr = ResultUtil.fail("文件类型不受支持").toString();
	                			backJson(jsonStr,response);
	                			return;
	                    	}
	                    }
	                    fileName = getMd5(fi)+suffixName;

	                    // 写入文件
	                    File file;
	                    if (fileName.lastIndexOf("\\") >= 0) {
	                        file = new File(uploadFilePath + fileName.substring(fileName.lastIndexOf("\\")));
	                    } else {
	                        file = new File(uploadFilePath + fileName.substring(fileName.lastIndexOf("\\") + 1));
	                    }
	                    
	                    fi.write(file);
	                    
	                    if(DebugConfig.isDebug) {
	                    	log.debug("文件已上传："+ uploadFilePath + fileName);
	                    }
	                    names.add(fileName);
	                }
	            }

                // 上传完成
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("filenames", names);
                String jsonStr = ResultUtil.success(jsonObject).toString();
    			backJson(jsonStr,response);
    			return;
	        }catch(SizeLimitExceededException ex) {
	        	if(DebugConfig.isDebug) {
		        	log.error("文件大小超出了限制");
	        	}
	            // 文件太大
				String jsonStr = ResultUtil.fail("文件太大").toString();
				backJson(jsonStr,response);
				return;
	        } catch (Exception ex) {
	        	log.error("文件上传出错：{}",ex);
	            // 发生意外
				String jsonStr = ResultUtil.fail("发生意外错误").toString();
				backJson(jsonStr,response);
				return;
	        }
	    } else {
	        // 请求错误
			String jsonStr = ResultUtil.fail("请求不受支持").toString();
			backJson(jsonStr,response);
			return;
	    }

	}
	
}
