package com.exam.controller.admin;


import java.util.List;

import com.exam.entity.TestPaper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.exam.entity.Users;
import com.exam.service.UsersService;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class UsersController {
	
	@Autowired
	private UsersService usersService;
	
	/**
	 * 1.首页访问用户界面 对应下面  /user.html
	 * 2.查询页面用户数据
	 * @return
	 */
	@RequestMapping(value="/user.html",method=RequestMethod.GET)
	public ModelAndView AllUsers(){
		List<Users> allUsers=usersService.findUserInfo();	//2.查询页面用户数据

		ModelAndView modelAndView = new ModelAndView();
    	modelAndView.addObject("allUsers", allUsers);
    	modelAndView.setViewName("_admin/user"); 
		return modelAndView;
	}

	@RequestMapping(value="/userDelete",method=RequestMethod.GET)
	@ResponseBody
	public String deleteUsers(String userId){
		int i=usersService.deleteByPrimaryKey(userId);
		System.out.println(i);
		if(i>=1){
			return "1";
		}else{
			return "0";
		}

	}

	@RequestMapping(value="/addUser",method=RequestMethod.POST)
	@ResponseBody
	public boolean addUser(Users user){
		boolean flag = true;
		user.setPermission(0);
		flag = usersService.addUser(user);
		return flag;
	}

	//编辑用户信息(回显)
	@RequestMapping(value="/editUserInfo.html")
	@ResponseBody
	public ModelAndView editTestPaperInfo(HttpServletRequest request, HttpServletResponse response){
		String userId = request.getParameter("userId");

		ModelAndView modelAndView = new ModelAndView();

		if(!StringUtils.isEmpty(userId)) {
			Users user=usersService.findUserInfoByUserId(userId);
			modelAndView.addObject("user", user);
			modelAndView.setViewName("_admin/editUserInfo");
		}else {
			modelAndView.setViewName("_admin/user");
		}

		return modelAndView;
	}

	//编辑用户信息(修改)
	@RequestMapping(value = "/userEdit")
	@ResponseBody
	public String userEdit(Users user)throws Exception{

		int j=usersService.updateUsersInfo(user);
		if (j >= 1) {
			return "1";
		}
		return "0";

	}

}
