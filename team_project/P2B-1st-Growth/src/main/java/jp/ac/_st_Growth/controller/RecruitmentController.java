package jp.ac._st_Growth.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jp.ac._st_Growth.entity.Club;
import jp.ac._st_Growth.entity.Recruitment;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.repository.ClubsRepository;
import jp.ac._st_Growth.repository.RecruitmentsRepository;
import jp.ac._st_Growth.repository.UsersRepository;

@Controller
@RequestMapping("/recruitment")
public class RecruitmentController {

    @Autowired
    private RecruitmentsRepository recruitmentRepository;
    
    @Autowired
    private UsersRepository userRepository;
    
    @Autowired
    private ClubsRepository clubsRepository;

    // 募集項目選択画面 - Recruitment Menu Screen
    @GetMapping("/menu")
    public String showRecruitmentMenu() {
        return "user/recruitment/recruitment_menu";
    }

    // 募集入力画面 - Recruitment Input Form
    @GetMapping("/input")
    public String showRecruitmentInputForm(HttpSession session, Model model) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }

            // Get user info for display
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isPresent()) {
                model.addAttribute("user", userOpt.get());
            }

            return "user/recruitment/recruitment_input";
            
        } catch (Exception e) {
            model.addAttribute("error", "画面の読み込みに失敗しました");
            return "user/recruitment/recruitment_input";
        }
    }

    // 募集入力確認画面 - Recruitment Confirmation Screen
    @PostMapping("/confirm")
    public String confirmRecruitment(
            @RequestParam("clubId") Integer clubId,
            @RequestParam(value = "matchDate", required = false) String matchDate,  // required=false
            @RequestParam(value = "matchTime", required = false) String matchTime,  // required=false
            @RequestParam(value = "location", required = false) String location,    // required=false
            @RequestParam(value = "scale", required = false) String scale,          // required=false
            @RequestParam(value = "remarks", required = false) String remarks,
            HttpSession session,
            Model model) {
    	  
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }

            // Get user and club information for display
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Club> clubOpt = clubsRepository.findByClubId(clubId);
            
            if (userOpt.isEmpty() || clubOpt.isEmpty()) {
                model.addAttribute("error", "ユーザーまたは部活動情報が見つかりません");
                return "user/recruitment/recruitment_input";
            }
            
            	
            

            // Add data to model for confirmation page
            model.addAttribute("club", clubOpt.get());
            model.addAttribute("matchDate", matchDate != null ? matchDate : "未定");
            model.addAttribute("matchTime", matchTime != null ? matchTime : "未定");
            model.addAttribute("location", location != null ? location : "未定");
            model.addAttribute("scale", scale != null ? scale : "未定");
            model.addAttribute("remarks", remarks);
            
            // Store in session for registration
            session.setAttribute("tempClubId", clubId);
            session.setAttribute("tempMatchDate", matchDate);
            session.setAttribute("tempMatchTime", matchTime);
            session.setAttribute("tempLocation", location);
            session.setAttribute("tempScale", scale);
            session.setAttribute("tempRemarks", remarks);

            
            return "user/recruitment/recruitment_confirm";
            
        } catch (Exception e) {
            model.addAttribute("error", "確認画面の読み込みに失敗しました");
            return "user/recruitment/recruitment_input";
        }
    }

    @PostMapping("/regist")
    public String registerRecruitment(HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }

            // Get temporary data from session
            Integer clubId = (Integer) session.getAttribute("tempClubId");
            String matchDateStr = (String) session.getAttribute("tempMatchDate");
            String matchTimeStr = (String) session.getAttribute("tempMatchTime");
            String location = (String) session.getAttribute("tempLocation");
            String scaleStr = (String) session.getAttribute("tempScale");
            String remarks = (String) session.getAttribute("tempRemarks");

            // Get user and club
            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Club> clubOpt = clubsRepository.findByClubId(clubId);
            
            if (userOpt.isEmpty() || clubOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "ユーザーまたは部活動情報が見つかりません");
                return "redirect:/recruitment/input";
            }

            // String to Integer conversion (optional)
            Integer scale = null;
            if (scaleStr != null && !scaleStr.trim().isEmpty()) {
                try {
                    scale = Integer.parseInt(scaleStr.trim());
                } catch (NumberFormatException e) {
                    // scale က optional ဖြစ်တဲ့အတွက် error မပြတော့ပါ
                }
            }

            // Create new recruitment
            Recruitment recruitment = new Recruitment();
            recruitment.setUser(userOpt.get());
            recruitment.setClub(clubOpt.get());
            
            // Optional date parsing
            if (matchDateStr != null && !matchDateStr.trim().isEmpty()) {
                try {
                    recruitment.setMatchDate(LocalDate.parse(matchDateStr));
                } catch (DateTimeParseException e) {
                    // date က optional ဖြစ်တဲ့အတွက် error မပြတော့ပါ
                }
            }
            
            recruitment.setMatchTime(matchTimeStr);  // can be null
            recruitment.setLocation(location);       // can be null
            recruitment.setScale(scale);             // can be null
            recruitment.setRemarks(remarks);         // can be null

            // Save to database
            recruitmentRepository.save(recruitment);

            // Clear temporary session data
            session.removeAttribute("tempClubId");
            session.removeAttribute("tempMatchDate");
            session.removeAttribute("tempMatchTime");
            session.removeAttribute("tempLocation");
            session.removeAttribute("tempScale");
            session.removeAttribute("tempRemarks");

            redirectAttributes.addFlashAttribute("success", "募集情報が正常に登録されました");
            return "redirect:/recruitment/complete";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "募集登録に失敗しました: " + e.getMessage());
            return "redirect:/recruitment/input";
        }
    }

    // 募集登録完了画面 - Recruitment Completion Screen
    @GetMapping("/complete")
    public String showRecruitmentComplete() {
        return "user/recruitment/recruitment_complete";
    }

    // 募集一覧表示 - Recruitment List
    @GetMapping("/list")
    public String showRecruitmentList(Model model, HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }

            // Get all recruitments
            List<Recruitment> recruitments = recruitmentRepository.findAll();
            model.addAttribute("recruitments", recruitments);
            
            return "user/recruitment/recruitment_list";
            
        } catch (Exception e) {
            model.addAttribute("error", "募集一覧の読み込みに失敗しました");
            return "user/recruitment/recruitment_list";
        }
    }

    // 募集詳細表示 - Recruitment Details
    @GetMapping("/detail")
    public String showRecruitmentDetail(@RequestParam("recruitId") Integer recruitId, 
                                      Model model, 
                                      HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }

            Optional<Recruitment> recruitmentOpt = recruitmentRepository.findByRecruitId(recruitId);
            if (recruitmentOpt.isPresent()) {
                model.addAttribute("recruitment", recruitmentOpt.get());
                return "user/recruitment/recruitment_detail";
            }
            
            model.addAttribute("error", "募集情報が見つかりません");
            return "redirect:/recruitment/list";
            
        } catch (Exception e) {
            model.addAttribute("error", "詳細の読み込みに失敗しました");
            return "redirect:/recruitment/list";
        }
    }

    // 募集確認画面 (自分の募集) - My Recruitments
    @GetMapping("/my-recruitments")
    public String showMyRecruitments(HttpSession session, Model model) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return "redirect:/login";
            }

            List<Recruitment> myRecruitments = recruitmentRepository.findByUserUserId(userId);
            model.addAttribute("recruitments", myRecruitments);
            
            return "user/recruitment/my_recruitments";
            
        } catch (Exception e) {
            model.addAttribute("error", "募集情報の読み込みに失敗しました");
            return "user/recruitment/my_recruitments";
        }
    }
}