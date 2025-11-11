package jp.ac._st_Growth.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 募集関連の機能を担当するコントローラー
 * 
 * ・募集項目選択画面の表示
 * ・募集要項入力画面の表示
 * ・入力データの確認処理
 */
@Controller
public class RecruitmentController {

    // ============================
    // 募集項目選択画面を表示
    // ============================
    @GetMapping("/user/input/select")
    public String showRecruitmentSelect() {
        // /templates/user/input/recrute_select.html を表示
        return "user/input/recrute_select";
    }

    // ============================
    // 募集要項入力画面を表示
    // ============================
    @GetMapping("/user/input")
    public String showRecruitmentInput() {
        // /templates/user/input/recrute_input.html を表示
        return "user/input/recrute_input";
    }

    // ============================
    // 募集要項確認画面への遷移処理
    // ============================
    @PostMapping("/user/input/check")
    public String checkRecruitment(
        @RequestParam("matchDate") String matchDate,
        @RequestParam("matchTime") String matchTime,
        @RequestParam("location") String location,
        @RequestParam(value = "description", required = false) String description, // 備考は任意
        Model model) {

        // 💡 Modelに入力データを格納（確認画面に渡す）
        model.addAttribute("matchDate", matchDate);
        model.addAttribute("matchTime", matchTime);
        model.addAttribute("location", location);
        model.addAttribute("description", description);

        // 🪄 確認画面（recrute_input_check.html）を表示
        return "user/input/recrute_input_check";
    }
    
}