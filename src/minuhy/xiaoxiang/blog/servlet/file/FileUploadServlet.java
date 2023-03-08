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
 * �ļ��ϴ�
 * @author xxxy1116
 * ����ʱ��:2023-02-27 01:49
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
			String jsonStr = ResultUtil.fail("ϵͳ��ʼ��δ���").toString();
			backJson(jsonStr,response);
			return;
		}
		
		if(getLoginUserBean(request) == null) {
			String jsonStr = ResultUtil.fail("���ȵ�¼").toString();
			backJson(jsonStr,response);
			return;
		}
		
		String contentType = request.getContentType();
	    if ((contentType.indexOf("multipart/form-data") >= 0)) {
	    	
	        DiskFileItemFactory factory = new DiskFileItemFactory();
	        
	        // ���洢���ڴ��е�����С
	        factory.setSizeThreshold(maxMemorySize);
	        
	        // �����������ڴ��С�����ݵ�λ��
	        factory.setRepository(new File(cacheFilePath));
	        
	        // �����µ��ļ��ϴ��������
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        
	        // Ҫ�ϴ�������ļ���С
	        upload.setSizeMax(maxFileSize);
	        
	        try {
	            // ���������Ի�ȡ�ļ���
	            List<FileItem> fileItems = upload.parseRequest(request);
	            
	            // �����ϴ����ļ���
	            ArrayList<String> names = new ArrayList<>();
	            Iterator<FileItem> i = fileItems.iterator();
	            while (i.hasNext()) {
	                FileItem fi = i.next();
	                if (!fi.isFormField()) {
	                	
	                    // ��ȡ�ϴ����ļ�����
	                    String fileName = fi.getName();
	                    
	                    if(DebugConfig.isDebug) {
		                    String fieldName = fi.getFieldName();
		                    boolean isInMemory = fi.isInMemory();
		                    long sizeInBytes = fi.getSize();
		                    log.debug("��������{}",fieldName);
		                    log.debug("�ļ�����{}",fileName);
		                    log.debug("�Ƿ����ڴ棺{}",isInMemory);
		                    log.debug("�ֽڴ�С��{}",sizeInBytes);
	                    }
	                    // �����ļ���
	                    String suffixName = fileName.substring(fileName.lastIndexOf('.'));
	                    if(isOnlyPicture) {
	                    	if(!suffixName.equals(".jpg") 
	                    			&& !suffixName.equals(".png")
	                    			&& !suffixName.equals(".gif")
	                    			&& !suffixName.equals(".webp")
	                    			&& !suffixName.equals(".ico")
	                    			) {
	                    		// �ļ����Ͳ�֧��
	                    		String jsonStr = ResultUtil.fail("�ļ����Ͳ���֧��").toString();
	                			backJson(jsonStr,response);
	                			return;
	                    	}
	                    }
	                    fileName = getMd5(fi)+suffixName;

	                    // д���ļ�
	                    File file;
	                    if (fileName.lastIndexOf("\\") >= 0) {
	                        file = new File(uploadFilePath + fileName.substring(fileName.lastIndexOf("\\")));
	                    } else {
	                        file = new File(uploadFilePath + fileName.substring(fileName.lastIndexOf("\\") + 1));
	                    }
	                    
	                    fi.write(file);
	                    
	                    if(DebugConfig.isDebug) {
	                    	log.debug("�ļ����ϴ���"+ uploadFilePath + fileName);
	                    }
	                    names.add(fileName);
	                }
	            }

                // �ϴ����
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("filenames", names);
                String jsonStr = ResultUtil.success(jsonObject).toString();
    			backJson(jsonStr,response);
    			return;
	        }catch(SizeLimitExceededException ex) {
	        	if(DebugConfig.isDebug) {
		        	log.error("�ļ���С����������");
	        	}
	            // �ļ�̫��
				String jsonStr = ResultUtil.fail("�ļ�̫��").toString();
				backJson(jsonStr,response);
				return;
	        } catch (Exception ex) {
	        	log.error("�ļ��ϴ�����{}",ex);
	            // ��������
				String jsonStr = ResultUtil.fail("�����������").toString();
				backJson(jsonStr,response);
				return;
	        }
	    } else {
	        // �������
			String jsonStr = ResultUtil.fail("������֧��").toString();
			backJson(jsonStr,response);
			return;
	    }

	}
	
}
