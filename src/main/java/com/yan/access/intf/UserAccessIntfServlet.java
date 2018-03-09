package com.yan.access.intf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.alibaba.fastjson.JSON;
import com.yan.access.dao.UserMongoDaoUtil;
import com.yan.access.intf.vo.SelectOptionVo;

public class UserAccessIntfServlet  extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession httpSession = request.getSession();
		
		//获取ServletContext 再获取 WebApplicationContextUtils  
        ServletContext servletContext = this.getServletContext();  
        WebApplicationContext context =   
                WebApplicationContextUtils.getWebApplicationContext(servletContext);  
        UserMongoDaoUtil userMongoDaoUtil = (UserMongoDaoUtil) context.getBean("userMongoDaoUtil"); 
		
		//获取入参
		String codeType = request.getParameter("intfCode");
		String codeCode = request.getParameter("userCode");
		String required = request.getParameter("passwordMD5");
		String selected = request.getParameter("selected");
		//从数据库查询
		List<SelectOptionVo> list = new ArrayList<SelectOptionVo>();
		
		if(codeType != null && !"".equals(codeType.trim())){
			
			//TransCodeMongoDaoUtil transCodeMongoDaoUtil = new TransCodeMongoDaoUtil();
			List<TransCode> transCodes = null;
			transCodes = transCodeMongoDaoUtil.findTransCodeDocumentsByCodeTypeAndValidStatus(codeType, "1");
			
			TransCode transCode = null;
			List<CodeEntry> codeEntries = null;
			if(transCodes != null && transCodes.size() > 0) {
				transCode = transCodes.get(0);
				
				if(codeCode != null && !"".equals(codeCode.trim())){
					//如果codecode不为空，说明是代码翻译接口，需要找到对应codecode
					if(transCode.getTrans() != null) {
						for(CodeEntry code:transCode.getTrans()){
							if(code != null && code.getCodeCode() != null 
									&& codeCode.trim().equals(code.getCodeCode().trim())) {
								codeEntries = new ArrayList<>(1);
								codeEntries.add(code);
								break;
							}
						}
					}
				}else{
					//codecode为空，说明是代码选择接口
					codeEntries = transCode.getTrans();
				}
			}
			
			
			if(codeEntries != null && codeEntries.size() > 0){
				for(CodeEntry code:codeEntries){
					SelectOptionVo optionVo = new SelectOptionVo();
					optionVo.setValue(code.getCodeCode());
					optionVo.setLabel(code.getCodeValue());
					
					if(selected != null && code.getCodeCode() != null
							&& selected.trim().equals(code.getCodeCode().trim())){
						optionVo.setSelected(true);
					}
					
					list.add(optionVo);
				}
			}
			
			if(required != null && "true".equalsIgnoreCase(required.trim())){
				//required为true，表示必录，肯定不需要'--'
			}else{
				//添加一个"请选择"
				SelectOptionVo optionVo = new SelectOptionVo();
				optionVo.setLabel("--");
				optionVo.setValue("");
				
				//如果有默认选择的option
				if(selected != null && !"".equals(selected.trim())){
					
				}else{
					//如果没有默认选择的option，我们才选择一个--为默认
					optionVo.setSelected(true);
				}
				list.add(optionVo);
			}
				
			
		}
		
		//fastjson转换为json
		//返回json数据
		String json = JSON.toJSONString(list);
		
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.println(json);
	}
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//如果在dopost中不调用doget，那么通过post方式请求过来的连接将不会被做任何处理
		doGet(request, response);
	}
}
