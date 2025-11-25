package jp.ac._st_Growth.controller;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jp.ac._st_Growth.entity.ClubMaster;
import jp.ac._st_Growth.entity.Recruitment;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.form.Form;
import jp.ac._st_Growth.repository.ClubMasterRepository;
import jp.ac._st_Growth.repository.RecruitmentsRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
public class LoginController {

	@Autowired
	UsersRepository userRepository;

	@Autowired
	RecruitmentsRepository recruitmentRepository;
	
	@Autowired
	private ClubMasterRepository clubMasterRepository;

	//ログイン画面表示
	@GetMapping("/login")
	public String LoginForm() {

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

		return "user/regist/user_regist";
	}

	//メニュー画面遷移
	
	
	// ==== 新規登録画面表示 ====
	@GetMapping("/register")
	public String showRegister(Model model) {
	   model.addAttribute("clubMasterList", clubMasterRepository.findAll());
	   return "user/regist/user_regist";
	}

	// ==== 新規登録画面表示(デバック用） ====
/*	@GetMapping("/register")
	public String showRegister(Model model) {

	    var list = clubMasterRepository.findAll();
	    System.out.println("clubMasterList size = " + list.size());  // ← 追加（デバッグ用）

	    model.addAttribute("clubMasterList", list);
	    return "user/regist/user_regist";
	}*/

	

	// ==== 新規登録処理 ====
	@PostMapping("/register")
	public String registerUser(
	        @RequestParam("name") String name,
	        @RequestParam("school") String school,
	        @RequestParam("clubId") Integer clubId,
	        @RequestParam("phoneNumber") String phoneNumber,
	        @RequestParam("email") String email,
	        @RequestParam("password") String password,
	        Model model
	) {

	    // メール重複チェック
	    Optional<User> existUser = userRepository.findByEmail(email);
	    if (existUser.isPresent()) {
	        model.addAttribute("error", "このメールアドレスは既に登録されています。");
	        model.addAttribute("clubMasterList", clubMasterRepository.findAll());
	        return "user/regist/user_regist";
	    }

	    // 新規ユーザー作成
	    User user = new User();
	    user.setName(name);
	    user.setSchool(school);
	    user.setPhoneNumber(phoneNumber);
	    user.setEmail(email);
	    user.setPassword(password);

	    // 部活動セット（ManyToOne）
	    ClubMaster club = new ClubMaster();
	    club.setClubId(clubId);
	    user.setClub(club);

	    userRepository.save(user);

	    model.addAttribute("message", "登録が完了しました！ログインしてください");

	    return "common/login/login";
	}






	//チョウ ウコウ‘

}