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

import jp.ac._st_Growth.entity.ClubMaster;
import jp.ac._st_Growth.entity.Recruitment;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.repository.ClubMasterRepository;
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
    private ClubMasterRepository clubMasterRepository;

    // ★ 入力画面
    @GetMapping("/input")
    public String showRecruitmentInputForm(
            @RequestParam(value = "userId", required = false) Integer userIdParam,
            @RequestParam(value = "clubId", required = false) Integer clubId,
            @RequestParam(value = "matchDate", required = false) String matchDate,
            @RequestParam(value = "matchTime", required = false) String matchTime,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "purpose", required = false) String purpose,
            @RequestParam(value = "skillLevel", required = false) String skillLevel,
            @RequestParam(value = "conditions", required = false) String conditions,
            HttpSession session,
            Model model) {

        Integer userId = (userIdParam != null)
                ? userIdParam
                : (Integer) session.getAttribute("userId");

        if (userId == null) return "redirect:/login";

        userRepository.findById(userId).ifPresent(u -> model.addAttribute("user", u));

        List<ClubMaster> clubs = clubMasterRepository.findAll();
        model.addAttribute("clubs", clubs);

        model.addAttribute("clubId", clubId);
        model.addAttribute("matchDate", matchDate);
        model.addAttribute("matchTime", matchTime);
        model.addAttribute("location", location);
        model.addAttribute("remarks", remarks);
        model.addAttribute("purpose", purpose);
        model.addAttribute("skillLevel", skillLevel);
        model.addAttribute("conditions", conditions);

        return "user/input/recruit_input";
    }

    // ★ 確認画面
    @PostMapping("/confirm")
    public String confirmRecruitment(
            @RequestParam("clubId") Integer clubId,
            @RequestParam(value = "matchDate", required = false) String matchDate,
            @RequestParam(value = "matchTime", required = false) String matchTime,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "purpose", required = false) String purpose,
            @RequestParam(value = "skillLevel", required = false) String skillLevel,
            @RequestParam(value = "conditions", required = false) String conditions,
            HttpSession session,
            Model model) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        // ★ 未選択 clubId=0 の処理
        if (clubId == 0) {
            model.addAttribute("error", "部活動を選択してください");
            return "user/input/recruit_input";
        }

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<ClubMaster> clubOpt = clubMasterRepository.findByClubId(clubId);

        if (userOpt.isEmpty() || clubOpt.isEmpty()) {
            model.addAttribute("error", "ユーザーまたは部活動情報が見つかりません");
            return "user/input/recruit_input";
        }

        model.addAttribute("user", userOpt.get());
        model.addAttribute("club", clubOpt.get());
        model.addAttribute("matchDate", matchDate);
        model.addAttribute("matchTime", matchTime);
        model.addAttribute("location", location);
        model.addAttribute("remarks", remarks);
        model.addAttribute("purpose", purpose);
        model.addAttribute("skillLevel", skillLevel);
        model.addAttribute("conditions", conditions);

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

    // ★ 登録処理
    @PostMapping("/regist")
    public String registerRecruitment(
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        Integer clubId = (Integer) session.getAttribute("tempClubId");
        String matchDateStr = (String) session.getAttribute("tempMatchDate");
        String matchTimeStr = (String) session.getAttribute("tempMatchTime");
        String location = (String) session.getAttribute("tempLocation");
        String remarks = (String) session.getAttribute("tempRemarks");
        String purpose = (String) session.getAttribute("tempPurpose");
        String skillLevel = (String) session.getAttribute("tempSkillLevel");
        String conditions = (String) session.getAttribute("tempConditions");

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<ClubMaster> clubOpt = clubMasterRepository.findByClubId(clubId);

        if (userOpt.isEmpty() || clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "データ取得に失敗しました");
            return "redirect:/recruitment/input";
        }

        Recruitment recruitment = new Recruitment();
        recruitment.setUser(userOpt.get());
        recruitment.setClubMaster(clubOpt.get());

        if (matchDateStr != null && !matchDateStr.isBlank()) {
            try {
                recruitment.setMatchDate(LocalDate.parse(matchDateStr));
            } catch (DateTimeParseException e) {}
        }

        recruitment.setMatchTime(matchTimeStr);
        recruitment.setLocation(location);
        recruitment.setRemarks(remarks);
        recruitment.setPurpose(purpose);
        recruitment.setSkill_level(skillLevel);
        recruitment.setConditions(conditions);

        recruitmentRepository.save(recruitment);

        // ★ セッション削除
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

    // ★ 自分の募集一覧
    @GetMapping("/list")
    public String MyRecruitList(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) return "redirect:/login";

        List<Recruitment> recruitments = recruitmentRepository.findByUserUserId(userId);
        model.addAttribute("recruitments", recruitments);

        return "user/input/recruit_list";
    }

    // ★ 詳細画面
    @GetMapping("/detail")
    public String RecruitDetail(
            @RequestParam("id") Integer id,
            Model model) {

        Optional<Recruitment> opt = recruitmentRepository.findById(id);

        if (opt.isEmpty()) {
            return "redirect:/recruitment/list";
        }

        model.addAttribute("recruitment", opt.get());
        return "user/input/recruit_detail";
    }
}
