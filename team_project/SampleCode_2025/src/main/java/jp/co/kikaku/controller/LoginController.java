package jp.co.kikaku.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.kikaku.form.UserForm;
import jp.co.kikaku.repository.UsersRepository;

/*
 * ログイン関連の処理を行うコントローラー
 * 
 * ログイン処理を行うタイミングで参考にしてください。
 */
@Controller
public class LoginController {
	@Autowired
	UsersRepository userRepository;

	/**
	 * 処理内容
	 * ログイン情報を入力するための画面を出力する
	 */
    @RequestMapping(path = "/login")
    public String showLoginPage() {
        return "login";
        }
    
    /*
     * 処理内容
     * ログイン情報入力後、ログインの処理を行う。
     * データベースに入力した内容と一致するユーザーを検索する
     * ・検索結果があればメニュー画面出力のパスへリダイレクト
     * ・検索結果が無ければエラーメッセージとともにログイン入力画面へ遷移させる
     */
    @RequestMapping(path = "/menu" ,method = RequestMethod.POST)
    public String login(UserForm form, Model model) {
    	User user = userRepository.findByEmailAndPassword(userId,password);

    	//検索結果があれば
        if (user !=null) {
            return "redirect:/メニュー画面出力のパス";
        } else {
            model.addAttribute("error", "ユーザーがいません");
            return "ログイン入力画面";
        }
    }
}
