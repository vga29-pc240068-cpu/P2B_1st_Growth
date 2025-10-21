package jp.ac._st_Growth.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {
	@RequestMapping(path="/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping(path="/top")
	public String top() {
		return "top";

	}
	@RequestMapping(path="/newUser_regist")
	public String newUser_regist() {
		return"newUser_regist";
	}
	@RequestMapping(path="/forgot_password")
	public String forgot_password() {
		return"forgot_password";
	}
	
}