package minuhy.xiaoxiang.blog.servlet.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import minuhy.xiaoxiang.blog.util.ResultUtil;

/**
 * �ļ�����
 * ����ʱ��:2023-02-27 00:44 
 */
public class FileDownloadServlet extends FileBaseServlet{
	/**
	 * UID
	 */
	private static final long serialVersionUID = -6731666011723912115L;

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    
		if(!init) {
			String jsonStr = ResultUtil.fail("ϵͳ��ʼ��δ���").toString();
			backJson(jsonStr,response);
			return;
		}
		
		// ��ȡ����
		String filename = request.getParameter("f");
		if(filename!=null&&filename.length()!=0) {
			String suffixName = filename.substring(filename.lastIndexOf('.'));
            if(isOnlyPicture) {
            	if(!suffixName.equals(".jpg") 
            			&& !suffixName.equals(".png")
            			&& !suffixName.equals(".gif")
            			&& !suffixName.equals(".webp")
            			&& !suffixName.equals(".ico")
            			) {
            		// �ļ����Ͳ�֧��
            		response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        			return;
            	}
            }
            
            String type = "image/jpeg";
            if (suffixName.equals(".png")) {
                type = "image/png";
            }else if (suffixName.equals(".gif")) {
                type = "image/gif";
            }else if (suffixName.equals(".webp")) {
                type = "image/webp";
            }else if (suffixName.equals(".ico")) {
                type = "image/x-icon";
            }
            
            File file = new File(uploadFilePath + filename);
            if (file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                response.setContentType(type);
                OutputStream outputStream = response.getOutputStream();
                // д������
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len = fileInputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, len);
                }
                fileInputStream.close();
            } else {
    			// �Ҳ��������ڵ��ļ�
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
		}else {
			// �Ҳ��������ڵ��ļ�
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
	}

}
