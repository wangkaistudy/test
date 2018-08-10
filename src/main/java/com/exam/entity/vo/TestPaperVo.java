package com.exam.entity.vo;

import com.exam.entity.TestPaper;

public class TestPaperVo extends TestPaper {
	
	private Integer status;    //1为完成 2未完成

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
}
