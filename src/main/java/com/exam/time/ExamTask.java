package com.exam.time;

import com.exam.dao.ExamMapper;
import com.exam.dao.ScoreMapper;
import com.exam.dao.TestPaperMapper;
import com.exam.entity.Score;
import com.exam.entity.TestPaper;
import com.exam.entity.Times;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 定时器
 */
public class ExamTask {

	@Autowired
	private ExamMapper examMapper;
	@Autowired
	private ScoreMapper scoreMapper;

	@Autowired
	private TestPaperMapper testPaperMapper;
	
	/**
	 * 更新考试时间
	 */
/*	public void updateTime() {
		//所有用户时间
		List<Times> allExamTimes = examMapper.findAllExamTimes();
		//更新用户时间-1分钟
		for (Times times : allExamTimes) {
			Double dataMin = times.getDataMin();
			dataMin--;
			times.setDataMin(dataMin);
			examMapper.updateExamTimes(times);
			*//**
			 * 时间到。更新时间如果没成绩设置0分
			 *//*
			if (dataMin == -1) {
				Score score = new Score();
				score.setUsersId(times.getUserId());
				score.setTestpaperId(times.getTestpaperId());
				Score ifExistenceScore = examMapper.findIfExistenceScore(score);
				if (ifExistenceScore == null) {//如果空 设置成绩0分
					Score record = new Score();
					record.setTestpaperId(times.getTestpaperId());
					record.setUsersId(times.getUserId());
					record.setFraction(0.0);
					scoreMapper.insertSelective(record);
				}
				//更新考试时间无效
				times.setTimesState(0);
				examMapper.updateExamTimes(times);
			}
			
			
			
		}
		
	}*/

	public void updateTime() {
		//查询所有参与考试的用户
		List<Times> allExamTimes = examMapper.findAllExamTimes();
		//更新用户时间-1分钟
		for (Times times : allExamTimes) {
			TestPaper testPaper = testPaperMapper.selectByPrimaryKey(times.getTestpaperId());

			SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//判断在考试结束时间和当前时间进行比较 如果考试结束时间小于现在时间则给0分

			Calendar instance = Calendar.getInstance();
			try {
				instance.setTime(sdf.parse(testPaper.getEndDate()));
				instance.add(Calendar.SECOND,5);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (instance.before(new Date())) {
				Score score = new Score();
				score.setUsersId(times.getUserId());
				score.setTestpaperId(times.getTestpaperId());
				Score ifExistenceScore = examMapper.findIfExistenceScore(score);
				if (ifExistenceScore == null) {//如果空 设置成绩0分
					Score record = new Score();
					record.setTestpaperId(times.getTestpaperId());
					record.setUsersId(times.getUserId());
					record.setFraction(0.0);
					scoreMapper.insertSelective(record);
				}
				//更新考试时间无效
				times.setTimesState(0);
				examMapper.updateExamTimes(times);
			}



		}

	}

}
