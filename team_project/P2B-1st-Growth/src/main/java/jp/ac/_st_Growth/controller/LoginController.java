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
import jp.ac._st_Growth.entity.Recruitment;   // ★ 追加
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.form.Form;
import jp.ac._st_Growth.repository.ClubMasterRepository;
import jp.ac._st_Growth.repository.RecruitmentsRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
public class LoginController {

    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private RecruitmentsRepository recruitmentRepository;

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

        // メール＆パスワードで検索
        List<User> users = userRepository.findByEmailAndPassword(email, password);

        if (users == null || users.isEmpty()) {
            // 認証失敗
            model.addAttribute("error", "メールまたはパスワードが間違っています。");
            return "common/login/login";
        }

        // ログインユーザー
        User loginUser = users.get(0);

        // ★ セッションにユーザーID＆ユーザー本体を保存
        session.setAttribute("userId", loginUser.getUserId());
        session.setAttribute("loginUser", loginUser);

        // ★ 募集一覧を取得してトップ画面へ渡す
        List<Recruitment> recruitments = recruitmentRepository.findAll();
        model.addAttribute("recruitments", recruitments);

        // ★ ホーム画面を表示する
        return "common/top";
    }

    // ==== 新規登録画面表示へのリンク（古いパス）====
    @GetMapping("/newUser_regist")
    public String newUserRegist() {
        return "user/regist/user_regist";
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

}
