package com.exam.dao;

import com.exam.entity.Options;
import com.exam.entity.vo.QuestionBankVo;

import java.util.List;

/**
 *	题库Mapper.Java
 */
public interface QuestionBankMapper {
	
	//查询所有题库
	public List<QuestionBankVo> findAllQuestionBank();

	public List<QuestionBankVo> findAllQuestionBank_new();
	public List<Options> findAllOptions(Integer questionBankId);

	//添加题目
	public int addQuestionBank(QuestionBankVo questionBankVo);
	
	//添加题目选项
	public int addOptions(List<Options> options);
	
	public int updateQuestionBankState(String value);
	
	//更新题目
	public int updateQuestionBank(QuestionBankVo questionBankVo);
	
	//删除题目的选项
	public int deleteQuestionBankOptions(String value);

	public String selectAswerExtById(Integer questionBankId);
}