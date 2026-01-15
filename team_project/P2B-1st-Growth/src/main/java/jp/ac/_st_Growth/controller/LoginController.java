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
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.form.Form;
import jp.ac._st_Growth.repository.ClubMasterRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
public class LoginController {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private ClubMasterRepository clubMasterRepository;

    // ==== ログイン画面表示 ====
    @GetMapping("/login")
    public String loginForm() {
        return "common/login/login";
    }

    // ==== ログイン処理 ====
    @PostMapping("/login")
    public String login(Form form, Model model, HttpSession session) {

        String email = form.getEmail();
        String password = form.getPassword();

        List<User> users = userRepository.findByEmailAndPassword(email, password);

        if (users == null || users.isEmpty()) {
            model.addAttribute("error", "メールまたはパスワードが間違っています。");
            return "common/login/login";
        }

        User loginUser = users.get(0);

        // ✅ セッション保存（統一）
        session.setAttribute("userId", loginUser.getUserId());
        session.setAttribute("loginUser", loginUser);

        // ✅ ここが肝：一覧はRecruitmentController(/recruitment/all)に任せる
        return "redirect:/recruitment/all";
    }

    // ==== トップ（古い導線が残っててもOKにする）====
    @GetMapping("/common/top")
    public String top(HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        // ✅ ここも /recruitment/all に寄せる
        return "redirect:/recruitment/all";
    }

    // ==== 新規登録画面表示へのリンク（古いパス）====
    @GetMapping("/newUser_regist")
    public String newUserRegist() {
        return "redirect:/register";
    }

    // ==== 新規登録画面表示 ====
    @GetMapping("/register")
    public String showRegister(Model model) {
        model.addAttribute("clubMasterList", clubMasterRepository.findAll());
        return "user/regist/user_regist";
    }

    // ==== 新規登録処理 ====
    @PostMapping("/register")
    public String registerUser(
            @RequestParam("name") String name,
            @RequestParam("school") String school,
            @RequestParam("clubId") Integer clubId,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        Optional<User> existUser = userRepository.findByEmail(email);
        if (existUser.isPresent()) {
            model.addAttribute("error", "このメールアドレスは既に登録されています。");
            model.addAttribute("clubMasterList", clubMasterRepository.findAll());
            return "user/regist/user_regist";
        }

        User user = new User();
        user.setName(name);
        user.setSchool(school);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        user.setPassword(password);

        // ✅ club はIDだけセットでOK
        ClubMaster club = new ClubMaster();
        club.setClubId(clubId);
        user.setClub(club);

        userRepository.save(user);

        model.addAttribute("message", "登録が完了しました！ログインしてください");
        return "common/login/login";
    }
}
