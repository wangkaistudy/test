package com.exam.controller.exam;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.exam.entity.Score;
import com.exam.entity.Users;
import com.exam.entity.vo.TestPaperVo;
import com.exam.service.TestPaperService;
import com.exam.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.exam.entity.TestPaper;
import com.exam.entity.vo.QuestionBankVo;
import com.exam.service.ExamService;

@Controller
@RequestMapping(value = "/exam")
public class ExamController {

	@Autowired
	private ExamService examService;
	@Autowired
	private TestPaperService testPaperService;
	@Autowired
	private UsersService userService;

	/**
	 * 试卷模板映射
	 * @return
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		Users user = (Users) request.getSession().getAttribute("myUser");

		List<TestPaperVo> allTestPaper = examService.findAllTestPaper_new();
		for (TestPaperVo tpv:allTestPaper) {
			tpv.setStatus(examService.findUserPaper(tpv.getTestpaperId(), user.getUserId()));
		}

		modelAndView.addObject("allTestPaper", allTestPaper);
		modelAndView.addObject("user", user);
		modelAndView.setViewName("_exam/index");
		return modelAndView;
	}
	
	/**
	 * 试卷模板映射
	 * @return
	 */
	@RequestMapping(value = "/exam-{id}.html", method = RequestMethod.GET)
	public ModelAndView exam(@PathVariable String id, HttpSession session) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			examService.findJudgmentQuestionAndChoiceQuestion(modelAndView, id, session);
		} catch (ParseException e) {
			e.printStackTrace();
		}
/*		List<TestPaper> allTestPaper = examService.findAllTestPaper();
		modelAndView.addObject("allTestPaper", allTestPaper);
*/		
		return modelAndView;
	}
	/**
	*提交试卷
	 */
	@ResponseBody
	@RequestMapping(value = "/submitpapers", method = RequestMethod.POST)
	public Map<String,Object> submitPapers(@RequestBody List<QuestionBankVo> questionBankVos,HttpSession session) {
		Map<String,Object> map = new HashMap<>();
		boolean flag = true;

		int testpaperId = Integer.valueOf(Integer.parseInt(session.getAttribute("testpaperId").toString()));
		TestPaper tp = testPaperService.selectByPrimaryKey(testpaperId);

		//判断答题卡与试卷题目数量是否一致
		if(tp.getTopicAmount()!=questionBankVos.size()) {
			flag = false;
		}

		List<QuestionBankVo> judgmentSystem = examService.JudgmentSystem(questionBankVos,session);

		map.put("flag",flag);
		map.put("topicAmount",tp.getTopicAmount());
		map.put("judgmentSystem",judgmentSystem);

		return map;
	}
	
	/**
	 * 试卷模板映射
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/autoGenerate", method = RequestMethod.POST)
	public boolean autoGenerate(HttpSession session, TestPaper testPaper) {
		
		boolean autoGenerate = examService.autoGenerate(session, testPaper.getTestpaperId());
		
		return autoGenerate;
	}

	//用户退出
	@RequestMapping(value = "/tuichu")
	public String tuichu(HttpSession session) {

		session.setAttribute("myUser", null);

		return "redirect:/index.html";
	}

	//修改密码页面
	@RequestMapping(value = "/xgxx")
	public ModelAndView xgxx(HttpSession session) {

		Users user = (Users) session.getAttribute("myUser");

		ModelAndView mv = new ModelAndView();
		mv.addObject("user",user);
		mv.setViewName("_exam/xgxx");
		return mv;
	}

	//修改密码
	@ResponseBody
	@RequestMapping(value = "/update_pass", method = RequestMethod.POST)
	public boolean update_pass(HttpSession session,String old_pass, String new_pass) {

		Users user = (Users) session.getAttribute("myUser");

		Users u = userService.findUserInfoByUserId(user.getUserId());

		if(!u.getUserPass().equals(old_pass)) {
			return false;
		}

		u.setUserPass(new_pass);

		int i = userService.updateUsersInfo(u);

		if(i<=0) {
			return false;
		}

		return true;
	}

	//跳转错误页面
	@RequestMapping(value = "/error")
	public ModelAndView tp_error() {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("_exam/error");
		return mv;
	}


}
