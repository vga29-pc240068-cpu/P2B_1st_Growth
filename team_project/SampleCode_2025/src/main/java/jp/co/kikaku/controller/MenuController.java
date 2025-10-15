package jp.co.kikaku.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * ユーザー登録・更新・削除ボタンを表示するメニュー画面を表示するコントローラー
 *
 */
@Controller
public class MenuController {
	/**
	 * Post送信されてきた場合(ログイン画面のみ)、Get送信で24行目のメソッドを実行するメソッド
	 * @return 28行目のメソッドへ処理を飛ばす
	 */
	@RequestMapping(path="/メニュー画面出力のパス",method=RequestMethod.POST)
    public String homeMenuPost() {
		return "redirect:/メニュー画面出力のパス";
    }

	/**
	 * ログイン画面以外の画面からアクセスされる場合に動くメニュー画面を表示するメソッド
	 * @return　メニュー画面
	 */
	 @RequestMapping("/メニュー画面出力のパス")
	    public String homeMenu() {
	        return "menu";
	    }


	 
	 @RequestMapping("/cancel")
	    public String menu() {
	        return "menu";
	    }

}
