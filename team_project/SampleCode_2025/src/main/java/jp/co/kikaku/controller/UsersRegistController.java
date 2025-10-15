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
 * 登録処理を行うコントローラー
 * 会員登録、商品登録、連絡帳記入、お問い合わせメッセージ、予約日決定といった
 * 何かしらの登録処理を行うタイミングで参考にしてください。
 */
@Controller
public class UsersRegistController {
    
	   @Autowired
	    private UsersRepository usersRepository;

	   /**
		 * 処理内容
		 * 登録情報を入力する画面を出力する
		 */
	    @RequestMapping("/登録入力画面へ遷移するパス")
	    public String registInput() {
	        return "登録入力画面";
	    }
	    
	    /**
	     * 処理内容
	     * 登録入力画面で入力した内容を”確認”するために、データを保持し登録確認画面を出力する
	     */
	    @RequestMapping(path="/登録確認画面へ遷移するパス",method = RequestMethod.POST)
	    public String registCheck("チームで作成したFormクラス" form,Model model) {
	    	model.addAttribute("userData","チームで作成したFormクラス");
	        return "登録確認画面";
	    }
	    
	    /**
	     * 処理内容
	     * 確認画面のデータを隠しパラメーター(hidden)で取得し、そのデータをEntityに移す
	     * その後、登録処理"save"を行い、登録完了画面を出力する
	     */
	    @RequestMapping(path="/登録完了画面へ遷移するパス",method = RequestMethod.POST)
	    public String registComplete("チームで作成したFormクラス" form,Model model) {
	    	
	    	//入力フォームに入っている情報をEntityに移す
	    	"User系のEntity名" user = new "User系のEntity名"();
	    	user.setUserName("Formクラス内のname");
	    	user.setPassword("Formクラス内のpassword");
	    	user.setEmail("Formクラス内のemail");
	    	//DBに登録
	    	usersRepository.save(user);
	    	
	        return "登録完了画面";
	    }
}
