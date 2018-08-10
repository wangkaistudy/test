package com.exam.service.impl;

import com.exam.dao.ExamMapper;
import com.exam.dao.QuestionBankMapper;
import com.exam.dao.ScoreMapper;
import com.exam.dao.TestPaperMapper;
import com.exam.entity.Score;
import com.exam.entity.TestPaper;
import com.exam.entity.Times;
import com.exam.entity.Users;
import com.exam.entity.vo.QuestionBankVo;
import com.exam.entity.vo.TestPaperVo;
import com.exam.service.ExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

	@Autowired
	private ExamMapper examMapper;
	@Autowired
	private ScoreMapper scoreMapper;
	@Autowired
	private TestPaperMapper testPaperMapper;

	@Autowired
	private QuestionBankMapper questionBankMapper;

	// 查询所有开始考试试卷到前端
	public List<TestPaper> findAllTestPaper() {

		List<TestPaper> allTestPaper = examMapper.findAllTestPaper_new();
		//通过试卷id 查询用户是否已考试完成
		/*List<Score> list = new ArrayList<>();
		for(TestPaper testPaper:allTestPaper){
			Integer testpaperId = testPaper.getTestpaperId();
			Score score = scoreMapper.selectByPrimaryKey(testpaperId);
			String usersId = score.getUsersId();
			if (usersId!=null);
			return null;
		}*/

		return allTestPaper;
	}
	public List<TestPaperVo> findAllTestPaper_new() {
		List<TestPaperVo> allTestPaperVo = examMapper.findAllTestPaper_new_Vo();
		return allTestPaperVo;
	}

	// 查询题目
	@Override
	public void findJudgmentQuestionAndChoiceQuestion(ModelAndView modelAndView, String id, HttpSession session) throws ParseException {

		/**
		 * 查询是否存在成绩
		 */
		Score score = new Score();
		score.setTestpaperId(Integer.parseInt(id));
		Users users = (Users) session.getAttribute("myUser");
		score.setUsersId(users.getUserId());
		Score ifExistenceScore = examMapper.findIfExistenceScore(score);
		if (ifExistenceScore != null) {
			modelAndView.addObject("Fraction", ifExistenceScore.getFraction());
			modelAndView.setViewName("_exam/score");
			return;
		}


		TestPaper tp = testPaperMapper.selectByPrimaryKey(Integer.valueOf(Integer.parseInt(id)));
		SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		/*
		* 查询试卷是否已经开始考试
		*/
		if(str1Ltstr2(sdf.format(new Date()),tp.getStartDate())) {
			modelAndView.setViewName("_exam/prematurity");
			return;
		}

		/*
		* 查询试卷是否过期
		*/
		if(str1Ltstr2(tp.getEndDate(),sdf.format(new Date()))) {
			modelAndView.setViewName("_exam/expire");
			return;
		}

		session.setAttribute("testpaperId", id);

		List<QuestionBankVo> findAllJudgmentQuestion = examMapper.findAllJudgmentQuestion(Integer.parseInt(id));// 判断题
		for (QuestionBankVo questionBankVo : findAllJudgmentQuestion) {
			session.setAttribute(questionBankVo.getQuestionBankId().toString(), questionBankVo.getAnswer());

		}

		List<QuestionBankVo> findAllChoiceQuestion = examMapper.findAllChoiceQuestion(Integer.parseInt(id));// 选择题
		for (QuestionBankVo questionBankVo : findAllChoiceQuestion) {
			session.setAttribute(questionBankVo.getQuestionBankId().toString(), questionBankVo.getAnswer());

		}


		/*
		* 查询试卷题目数量是否符合预期
		*/
		if(tp.getTopicAmount()!=(findAllJudgmentQuestion.size()+findAllChoiceQuestion.size())) {
			modelAndView.setViewName("_exam/error");
			return;
		}


		Times times = new Times();// 当前试卷-当前用户的试卷如果没时间设置时间
		times.setUserId(users.getUserId());
		times.setTestpaperId(Integer.parseInt(id));
		Times ifExamTimes = examMapper.findExamTimes(times);
		if (ifExamTimes == null) {
			times.setDataMin(tp.getTimeTemplate());
			examMapper.addExamTimes(times);
		}
			String endDate = tp.getEndDate();
			Date endTime = sdf.parse(endDate);
			long betwen = (endTime.getTime() - new Date().getTime())/1000;
			long minute = betwen / 60;
			long seconds = betwen % 60;
			session.setAttribute("min", minute);
			session.setAttribute("sec",seconds);



		modelAndView.addObject("tpName", tp.getTestpaperName());
		modelAndView.addObject("JudgmentQuestion", findAllJudgmentQuestion);
		modelAndView.addObject("ChoiceQuestion", findAllChoiceQuestion);
		modelAndView.setViewName("_exam/exam");
	}

	/**
	 * 判题方法
	 */
	@Override
	public List<QuestionBankVo> JudgmentSystem(List<QuestionBankVo> questionBankVos, HttpSession session) {
		double fraction = 0;
		//增加获取到试卷id，通过id查询题数amount--- tp
		TestPaper tp = testPaperMapper.selectByPrimaryKey(Integer.parseInt(session.getAttribute("testpaperId").toString()));
		Integer amount = tp.getTopicAmount();
		double temp=100/amount;
		for (QuestionBankVo questionBankVo : questionBankVos) {
			if (questionBankVo.getAnswer() != null) {
				if (session.getAttribute(questionBankVo.getQuestionBankId().toString())
						.equals(questionBankVo.getAnswer())) {
					
					fraction += temp;
					questionBankVo.setIfCorrect(true);
				} else {
					questionBankVo.setIfCorrect(false);
					questionBankVo
							.setAnswer(session.getAttribute(questionBankVo.getQuestionBankId().toString()).toString());
				}
			} else {
				questionBankVo.setIfCorrect(false);
				questionBankVo
						.setAnswer(session.getAttribute(questionBankVo.getQuestionBankId().toString()).toString());
			}
			/*答案解析了*/
			questionBankVo.setAnswerExt(questionBankMapper.selectAswerExtById(questionBankVo.getQuestionBankId()));
		}
		Score score = new Score();
		score.setTestpaperId(Integer.parseInt(session.getAttribute("testpaperId").toString()));
		score.setUsersId(((Users) session.getAttribute("myUser")).getUserId());
		score.setFraction(fraction);
		int insertSelective = scoreMapper.insertSelective(score);
		if (insertSelective == 0) {// 设置分数是否成功
			return null;
		}

		return questionBankVos;// 返回状态
	}

	/**
	 * 自动生成试卷
	 */
	@Override
	public boolean autoGenerate(HttpSession session, Integer id) {
		// 判断题
		List<QuestionBankVo> findAllJudgmentQuestion = examMapper.findAllJudgmentQuestion(id);

		// 选择题
		List<QuestionBankVo> findAllChoiceQuestion = examMapper.findAllChoiceQuestion(id);
		
		//获取到试卷id，通过id查询题数amount
		TestPaper tp = testPaperMapper.selectByPrimaryKey(id);
		Integer amount = tp.getTopicAmount();

		//if (findAllChoiceQuestion.size() >= 5 && findAllChoiceQuestion.size() >= 5) {
		//判断题数是否相同
		if (amount==findAllChoiceQuestion.size()+findAllJudgmentQuestion.size()) {
			HashMap<String, List<QuestionBankVo>> hashMap = new HashMap<String, List<QuestionBankVo>>();
			hashMap.put("JudgmentQuestion", findAllJudgmentQuestion);
			hashMap.put("ChoiceQuestion", findAllChoiceQuestion);
			session.setAttribute("question", hashMap);
			session.setAttribute("XiaoBing", "XiaoBingBy");
			session.setAttribute("JudgmentQuestion", findAllJudgmentQuestion);
			session.setAttribute("ChoiceQuestion", findAllChoiceQuestion);
			// 存入时间
			// 初始化成绩
			return true;
		}

		return false;
	}

	//查询考试是否完成
	@Override
	public Integer findUserPaper(Integer tpId, String uId) {
		/**
		 * 查询是否存在成绩
		 */
		Score score = new Score();

		score.setTestpaperId(tpId);
		score.setUsersId(uId);
		Score ifExistenceScore = examMapper.findIfExistenceScore(score);
		if (ifExistenceScore != null) {
			return 1;
		}
		return 2;
	}


	//比较时间
	private boolean str1Ltstr2(String str1, String str2) throws ParseException {
		SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date s1 = sdf.parse(str1);
		Date s2 = sdf.parse(str2);
		if(s1.before(s2)) {
			return true;
		}else {
			return false;
		}
	}

}
