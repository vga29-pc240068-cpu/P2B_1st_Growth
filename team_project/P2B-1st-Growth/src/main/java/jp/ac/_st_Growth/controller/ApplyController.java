package jp.ac._st_Growth.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import jp.ac._st_Growth.entity.Application;
import jp.ac._st_Growth.entity.Recruitment;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.repository.ApplicationsRepository;
import jp.ac._st_Growth.repository.RecruitmentsRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
public class ApplyController {

    @Autowired
    private ApplicationsRepository applicationsRepository;
    
    @Autowired
    private RecruitmentsRepository recruitmentRepository;
    
    @Autowired
    private UsersRepository userRepository;

    
    // 応募確認画面の表示
    
    @PostMapping("/user/apply/check")
    public String confirmApply(
            @RequestParam("recruitId") Long recruitId,
            Model model) {

        // DBから募集情報を1件取得
        Optional<Recruitment> recruitmentOpt = recruitmentRepository.findById(recruitId);
        
        if (recruitmentOpt.isPresent()) {
            model.addAttribute("recruitment", recruitmentOpt.get());
        } else {
            // エラーハンドリング
            model.addAttribute("error", "募集情報が見つかりませんでした");
            return "error";
        }

        return "user/apply/apply_check";
    }

    
    // 応募登録処理
    
    @PostMapping("/user/apply/regist")
    public String applyRecruitment(
            @RequestParam("recruitId") Long recruitId,
            HttpSession session,
            Model model) {

        try {
            // ログイン中ユーザーのIDを取得
            Long userId = (Long) session.getAttribute("user_id");
            
            if (userId == null) {
                model.addAttribute("error", "ログインしてください");
                return "login";
            }

            // ユーザー情報を取得
            List<User> userOpt = userRepository.findById(userId);
            Optional<Recruitment> recruitmentOpt = recruitmentRepository.findById(recruitId);
            
            if (userOpt.isEmpty() || recruitmentOpt.isEmpty()) {
                model.addAttribute("error", "ユーザーまたは募集情報が見つかりません");
                return "error";
            }

            // 応募情報を新規作成
            Application application = new Application();
            
            // DBへ保存
            applicationsRepository.save(application);

            // 応募完了ページへ遷移
            return "redirect:/user/apply/complete";
            
        } catch (Exception e) {
            model.addAttribute("error", "応募処理中にエラーが発生しました: " + e.getMessage());
            return "error";
        }
    }

    
    // 応募完了画面の表示
    
    @GetMapping("/user/apply/complete")
    public String showApplyComplete(Model model) {
        model.addAttribute("message", "応募が完了しました！");
        return "user/apply/apply_complete";
    }

   
    // 応募一覧表示
    
    @GetMapping("/user/apply/list")
    public String showApplyList(Model model, HttpSession session) {
        try {
            Long userId = (Long) session.getAttribute("user_id");
            
            if (userId == null) {
                return "redirect:/login";
            }
            
            List<Application> applications = applicationsRepository.findByUserId(userId);
            model.addAttribute("applications", applications);
            
            return "user/apply/apply_list";
            
        } catch (Exception e) {
            model.addAttribute("error", "応募一覧の取得中にエラーが発生しました: " + e.getMessage());
            return "error";
        }
    }
}