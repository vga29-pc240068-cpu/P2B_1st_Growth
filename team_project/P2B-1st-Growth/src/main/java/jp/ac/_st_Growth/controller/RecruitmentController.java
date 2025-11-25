package jp.ac._st_Growth.controller;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

 // 入力画面
    @GetMapping("/input")
    public String showRecruitmentInputForm(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        userRepository.findById(userId).ifPresent(u -> model.addAttribute("user", u));

        // ★ ここ追加！クラブ一覧をHTMLへ渡す
        model.addAttribute("clubs", clubsRepository.findAll());

        return "user/input/recruit_input";
    }



    // 入力確認画面
    @PostMapping("/confirm")
    public String confirmRecruitment(
            @RequestParam("clubId") Integer clubId,
            @RequestParam(value = "matchDate", required = false) String matchDate,
            @RequestParam(value = "matchTime", required = false) String matchTime,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "remarks", required = false) String remarks,

            // ★ 新規追加項目
            @RequestParam(value = "purpose", required = false) String purpose,
            @RequestParam(value = "skill_level", required = false) String skillLevel,
            @RequestParam(value = "conditions", required = false) String conditions,

            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Club> clubOpt = clubsRepository.findByClubId(clubId);

        if (userOpt.isEmpty() || clubOpt.isEmpty()) {
            model.addAttribute("error", "ユーザーまたは部活動情報が見つかりません");
            return "user/input/recruit_input";
        }

        model.addAttribute("club", clubOpt.get());
        model.addAttribute("matchDate", matchDate != null ? matchDate : "未定");
        model.addAttribute("matchTime", matchTime != null ? matchTime : "未定");
        model.addAttribute("location", location != null ? location : "未定");
        model.addAttribute("remarks", remarks);

        // ★ 新項目をモデルに渡す
        model.addAttribute("purpose", purpose);
        model.addAttribute("skillLevel", skillLevel);
        model.addAttribute("conditions", conditions);

        // ★ セッションにも保存
        session.setAttribute("tempClubId", clubId);
        session.setAttribute("tempMatchDate", matchDate);
        session.setAttribute("tempMatchTime", matchTime);
        session.setAttribute("tempLocation", location);
        session.setAttribute("tempRemarks", remarks);

        session.setAttribute("tempPurpose", purpose);
        session.setAttribute("tempSkillLevel", skillLevel);
        session.setAttribute("tempConditions", conditions);

        return "user/input/recruit_input_check";
    }


    // 登録処理
    @PostMapping("/regist")
    public String registerRecruitment(HttpSession session, RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Integer clubId = (Integer) session.getAttribute("tempClubId");
        String matchDateStr = (String) session.getAttribute("tempMatchDate");
        String matchTimeStr = (String) session.getAttribute("tempMatchTime");
        String location = (String) session.getAttribute("tempLocation");
        String remarks = (String) session.getAttribute("tempRemarks");

        // ★ 新項目を受け取る
        String purpose = (String) session.getAttribute("tempPurpose");
        String skillLevel = (String) session.getAttribute("tempSkillLevel");
        String conditions = (String) session.getAttribute("tempConditions");

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Club> clubOpt = clubsRepository.findByClubId(clubId);

        if (userOpt.isEmpty() || clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "データ取得に失敗しました");
            return "redirect:/recruitment/input";
        }

        Recruitment recruitment = new Recruitment();
        recruitment.setUser(userOpt.get());
        recruitment.setClub(clubOpt.get());

        if (matchDateStr != null && !matchDateStr.isBlank()) {
            try {
                recruitment.setMatchDate(LocalDate.parse(matchDateStr));
            } catch (DateTimeParseException e) {}
        }

        recruitment.setMatchTime(matchTimeStr);
        recruitment.setLocation(location);
        recruitment.setRemarks(remarks);

        // ★ 新項目をセット
        recruitment.setPurpose(purpose);
        recruitment.setSkill_level(skillLevel);
        recruitment.setConditions(conditions);

        recruitmentRepository.save(recruitment);

        // セッション削除
        session.removeAttribute("tempClubId");
        session.removeAttribute("tempMatchDate");
        session.removeAttribute("tempMatchTime");
        session.removeAttribute("tempLocation");
        session.removeAttribute("tempRemarks");

        session.removeAttribute("tempPurpose");
        session.removeAttribute("tempSkillLevel");
        session.removeAttribute("tempConditions");

        return "user/input/recruit_input_complete";
    }

 //  自分の募集一覧表示
    @GetMapping("/list")
    public String MyRecruitList(HttpSession session, Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // ログインしているユーザーの募集だけ取得
        List<Recruitment> recruitments =
                recruitmentRepository.findByUserUserId(userId);

        model.addAttribute("recruitments", recruitments);
        return "user/input/recruit_list";  // ← 新しく作るHTMLへ
    }
    
    
 // ★ 募集の詳細表示
    @GetMapping("/detail")
    public String RecruitDetail(
            @RequestParam("id") Integer id,
            Model model
    ) {

        Optional<Recruitment> opt = recruitmentRepository.findById(id);

        if (opt.isEmpty()) {
            return "redirect:/recruitment/list";
        }

        model.addAttribute("recruitment", opt.get());
        return "user/input/recruit_detail"; 
    }


}
