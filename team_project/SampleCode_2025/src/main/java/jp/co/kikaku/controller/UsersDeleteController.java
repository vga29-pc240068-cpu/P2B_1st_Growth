package jp.co.kikaku.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jp.co.kikaku.repository.UsersRepository;

/*
 * 削除処理を行うコントローラー
 * 会員情報削除、商品情報削除、予約情報削除、マッチングを削除、といった
 * 何かしらの削除処理を行うタイミングで参考にしてください。
 */
@Controller
public class UsersDeleteController {

	@Autowired
	private UsersRepository usersRepository;

	/**
	 * 処理内容
	 * 削除する情報のidを入力する画面を出力する
	 */
	@RequestMapping("/削除入力画面へ遷移するパス")
	public String deleteInput() {
		return "削除入力画面";
	}

	/**
	 * 処理内容
	 * 本当に削除していいか確認するための画面を出力
	 */
	@RequestMapping(path = "/削除確認画面へ遷移するパス",method = RequestMethod.POST)
	public String deleteCheck(int userId,Model model) {
		model.addAttribute("userId",userId);
		return "削除確認画面";
	}

	/**
	 * 処理内容
	 * 削除確認画面でhiddenで取得したidの情報を削除し、削除完了画面を出力する
	 */
	@RequestMapping(path= "/削除完了画面へ遷移するパス",method = RequestMethod.POST)
	public String deleteComplete(Integer id) {
	
		usersRepository.deleteById(id);
		return "削除完了画面";
	}

}
