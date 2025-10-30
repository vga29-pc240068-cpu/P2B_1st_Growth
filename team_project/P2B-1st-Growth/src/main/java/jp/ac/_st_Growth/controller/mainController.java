package jp.ac._st_Growth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jp.ac._st_Growth.repository.RecruitmentsRepository;


@Controller
public class mainController {

    @Autowired
    RecruitmentsRepository recruitmentsRepository;

    // トップページで募集一覧を表示
    @GetMapping("common/top")
    public String showMain(Model model) {
        // DBから全件取得
        model.addAttribute("recruitments", recruitmentsRepository.findAll());
        return "common/top"; 
    }
}