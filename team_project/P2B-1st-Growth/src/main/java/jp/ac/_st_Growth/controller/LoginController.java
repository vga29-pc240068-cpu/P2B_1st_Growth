package jp.ac._st_Growth.Controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import jp.ac._st_Growth.entity.Recruitment;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.form.Form;
import jp.ac._st_Growth.repository.RecruitmentRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
public class LoginController {

	@Autowired
	UsersRepository userRepository;

	@Autowired
	RecruitmentRepository recruitmentRepository;

	//ログイン画面表示
	@GetMapping("/login")
	public String showLoginForm() {

		return "common/login/login";
	}

	//ログイン処理
	@PostMapping("/login")
	public String login(Form form, Model model,HttpSession session) {
		String email = form.getEmail();
		String password = form.getPassword();
		List<User> user = userRepository.findByEmailAndPassword(email, password);
		
		
	if (user != null && !user.isEmpty()) {
		
		// ログインユーザーのIDをセッションに保存
		 session.setAttribute("userId", user.get(0).getUserId());

	//募集一覧を取得して画面へ渡す
			List<Recruitment> recruitments = recruitmentRepository.findAll();
			model.addAttribute("recruitments", recruitments);

			return "common/top";

	} else {
			model.addAttribute("error", "メールまたはパスワードが間違っています。");

			return "common/login/login";
		}
	}

	@GetMapping("/newUser_regist")
	public String newUser_regist() {

		return "user/regist/newUser_regist";
	}

	//メニュー画面遷移

	//チョウ ウコウ‘

}