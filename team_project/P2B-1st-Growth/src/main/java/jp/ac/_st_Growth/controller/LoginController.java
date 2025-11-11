package jp.ac._st_Growth.Controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.form.Form;
import jp.ac._st_Growth.repository.UsersRepository;



@Controller
public class LoginController {
	@Autowired
	UsersRepository userRepository;

	@GetMapping("/login")
	public String showLoginForm() {

		return "common/login/login";
	}
	

	@PostMapping("/login")
	public String login(Form form, Model model) {
		String email = form.getEmail(); 
	    String password = form.getPassword();
		List<User> user = userRepository.findByEmailAndPassword(email,password);
        if (user !=null) {

            return "common/top";
        } else {
            model.addAttribute("error", "ユーザーがいません");

            return "common/login/login";
        }
    }
	
	@GetMapping("/newUser_regist")
	public String newUser_regist() {

		return"user/regist/newUser_regist";
	}

	
	//メニュー画面遷移

	//チョウ ウコウ‘


	

	
}