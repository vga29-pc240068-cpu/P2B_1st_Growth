package jp.co.kikaku.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.kikaku.entity.User;
import jp.co.kikaku.form.UserForm;
import jp.co.kikaku.repository.UsersRepository;

/*
 * 更新処理を行うコントローラー
 * 会員情報更新、商品情報変更、予約情報を変更、クイズの内容を変更、といった
 * 何かしらの更新処理を行うタイミングで参考にしてください。
 */
@Controller
public class UsersUpdateController {

    @Autowired
    private UsersRepository usersRepository;
    
    /**
	 * 処理内容
	 * 更新する情報のidを入力する画面を出力する
	 */
    @RequestMapping("/更新入力画面へ遷移するパス")
    public String updateInput() {
        return "更新入力画面";
    }

    /**
     * 処理内容
     * 更新入力画面で入力したidをもとに検索を行い、更新前の情報を画面に出力する
     * また出力した更新前の情報は修正可能である
     */
    @RequestMapping(path="/更新確認画面へ遷移するパス",method = RequestMethod.POST)
    public String updateCheck("チームで作成したFormクラス" form,Model model) {
    	model.addAttribute("userData", usersRepository.getReferenceById("Formクラス内のid");
        return "更新確認画面";
    }

    /**
     * 処理内容
     * 一旦更新前の情報を検索し、Entityとしてデータを取得しておく。
     * 更新確認画面で入力したデータを取得し、そのデータを先ほど取得したEntityに上書きする
     * 更新処理"save"を行い、更新完了画面を出力する
     */
    @RequestMapping(path="/更新完了画面へ遷移するパス",method = RequestMethod.POST)
    public String updateConplete("チームで作成したFormクラス" form,Integer userId) {
    	"User系のEntity名" users=usersRepository.getReferenceById(userId);
    	users.setUserName("Formクラス内のname");
    	users.setPassword("Formクラス内のpassword");
    	users.setEmail("Formクラス内のemail");
    	//DBに登録
    	usersRepository.save(users);
        return "更新完了画面";
    }

}
