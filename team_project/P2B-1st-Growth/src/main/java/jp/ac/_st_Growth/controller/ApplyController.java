package jp.ac._st_Growth.Controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @GetMapping("/user/apply/check")
    public String confirmApply(
            @RequestParam("recruitId") Integer recruitId,Model model) {

       
        List<Recruitment> recruitmentList = recruitmentRepository.findByRecruitId(recruitId);

        
        if (!recruitmentList.isEmpty()) {
            model.addAttribute("recruitment", recruitmentList.get(0));
        } else {
            
            model.addAttribute("error", "募集情報が見つかりませんでした");
            return "error";
        }

        return "user/apply/apply_check";
    }

    
    // 応募登録処理
    
    @PostMapping("/user/apply/regist")
    public String applyRecruitment(
            @RequestParam("recruitId") Integer recruitId,
            HttpSession session,
            Model model) {

        try {
            // ログイン中ユーザーのIDを取得
        	Integer userId = (Integer) session.getAttribute("userId");
            
            if (userId == null) {
                model.addAttribute("error", "ログインしてください");
                return "login";
            }

            // ユーザー情報を取得
            List<User> userList = userRepository.findByUserId(userId);
            List<Recruitment> recruitmentList = recruitmentRepository.findByRecruitId(recruitId);
            
            if (userList.isEmpty() || recruitmentList.isEmpty()) {
                model.addAttribute("error", "ユーザーまたは募集情報が見つかりません");
                return "error";
            }

            // 応募情報を新規作成
            Application application = new Application();
            application.setUser(userList.get(0));
            application.setRecruitment(recruitmentList.get(0));
            application.setApplyDate(new java.sql.Date(System.currentTimeMillis()));
            
            
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
        	Integer userId = (Integer) session.getAttribute("userId");
            
            if (userId == null) {
                return "redirect:/login";
            }
            
            List<Application> applications = applicationsRepository.findByUserUserId(userId);
            model.addAttribute("applications", applications);
            
            return "user/apply/apply_list";
            
        } catch (Exception e) {
            model.addAttribute("error", "応募一覧の取得中にエラーが発生しました: " + e.getMessage());
            return "error";
        }
    }
}