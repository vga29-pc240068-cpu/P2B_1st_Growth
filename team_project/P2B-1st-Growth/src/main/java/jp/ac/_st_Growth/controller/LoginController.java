package jp.ac._st_Growth.controller;

import java.text.Normalizer.Form;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.repository.UsersRepository;



@Controller
public class LoginController {
	@Autowired
	UsersRepository userRepository;
	
	@RequestMapping(path="/login")
	public String showLoginPage() {
		return "login";
	}
	
	@RequestMapping(path="/login",method = RequestMethod.POST)
	public String login(Form form, Model model) {
		String email = form.getEmail(); 
	    String password = form.getPassword();
		User user = userRepository.findByEmailAndPassword(email,password);
        if (user !=null) {
            return "top";
        } else {
            model.addAttribute("error", "ユーザーがいません");
            return "login";
        }
    }
	
	@RequestMapping(path="/newUser_regist")
	public String newUser_regist() {
		return"newUser_regist";
	}
	@RequestMapping(path="/forgot_password")
	public String forgot_password() {
		return"forgot_password";
	}
	
	//メニュー画面
	//松浦

	@RequestMapping(path="/menu",method =RequestMethod.GET)
	public String menu() {
		return"menu";
	}
	//
}