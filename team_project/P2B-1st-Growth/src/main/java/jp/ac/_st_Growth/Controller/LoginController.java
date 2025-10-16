package jp.ac._st_Growth.Controller;

import org.springframework.stereotype.Controller;

@Controller
public class LoginController {
	@RequestMapping(path="/login")
	public String login() {
		return "login";
	}
	
	@RequestMapping(path="/")
	public String login() {
		return "login";

}
