package jp.ac._st_Growth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RecruitmentController {
	//募集要項メニューを表示
	@RequestMapping(path = "/recruit/select")
	public String recruitmentMenu() {
		return "/user/select/recrute_select";
	}

	//募集入力を表示
	@RequestMapping(path = "/recriut/input")
	public String recruitmentInput() {
		return "/user/input/recrute_input";
	}
	
	
}