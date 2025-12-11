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
    private UsersRepository userRepository;   // 使っても使わんでもOK、残しとく

    @Autowired
    private ClubMasterRepository clubMasterRepository;

    // ===== 募集入力画面 =====
    @GetMapping("/input")
    public String showRecruitmentInputForm(HttpSession session, Model model) {

        // ★ ログインチェック（loginUser を見る）
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            model.addAttribute("error", "ログインしてから使ってね");
            return "common/login/login";
        }

        // 画面でユーザー使いたかったら渡しとく
        model.addAttribute("user", loginUser);

        // 部活プルダウン
        List<ClubMaster> clubs = clubMasterRepository.findAll();
        model.addAttribute("clubs", clubs);

        return "user/input/recruit_input";
    }

    // ===== 確認画面 =====
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

        // ログイン確認（統一して loginUser で見る）
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 部活未選択チェック
        if (clubId == null || clubId == 0) {
            model.addAttribute("error", "部活動を選んでね");

            // 入力値戻す
            model.addAttribute("clubId", clubId);
            model.addAttribute("matchDate", matchDate);
            model.addAttribute("matchTime", matchTime);
            model.addAttribute("location", location);
            model.addAttribute("remarks", remarks);
            model.addAttribute("purpose", purpose);
            model.addAttribute("skillLevel", skillLevel);
            model.addAttribute("conditions", conditions);

            // プルダウン再セット
            model.addAttribute("clubs", clubMasterRepository.findAll());

            return "user/input/recruit_input";
        }

        // 部活マスタ取得
        Optional<ClubMaster> clubOpt = clubMasterRepository.findByClubId(clubId);
        if (clubOpt.isEmpty()) {
            model.addAttribute("error", "部活動が見つからんよ");

            model.addAttribute("clubId", clubId);
            model.addAttribute("matchDate", matchDate);
            model.addAttribute("matchTime", matchTime);
            model.addAttribute("location", location);
            model.addAttribute("remarks", remarks);
            model.addAttribute("purpose", purpose);
            model.addAttribute("skillLevel", skillLevel);
            model.addAttribute("conditions", conditions);

            model.addAttribute("clubs", clubMasterRepository.findAll());
            return "user/input/recruit_input";
        }

        // 確認画面に表示する値
        model.addAttribute("club", clubOpt.get());
        model.addAttribute("matchDate",
                (matchDate != null && !matchDate.isBlank()) ? matchDate : "未定");
        model.addAttribute("matchTime",
                (matchTime != null && !matchTime.isBlank()) ? matchTime : "未定");
        model.addAttribute("location",
                (location != null && !location.isBlank()) ? location : "未定");
        model.addAttribute("remarks", remarks);
        model.addAttribute("purpose", purpose);
        model.addAttribute("skillLevel", skillLevel);
        model.addAttribute("conditions", conditions);

        return "user/input/recruit_input_check";
    }

    // ===== 登録処理 =====
    @PostMapping("/regist")
    public String registerRecruitment(
            @RequestParam("clubId") Integer clubId,
            @RequestParam(value = "matchDate", required = false) String matchDateStr,
            @RequestParam(value = "matchTime", required = false) String matchTimeStr,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "remarks", required = false) String remarks,
            @RequestParam(value = "purpose", required = false) String purpose,
            @RequestParam(value = "skillLevel", required = false) String skillLevel,
            @RequestParam(value = "conditions", required = false) String conditions,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // ログイン確認（ここも loginUser 統一）
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        // 部活だけマスタから取る
        Optional<ClubMaster> clubOpt = clubMasterRepository.findByClubId(clubId);
        if (clubOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "データ取得失敗した…");
            return "redirect:/recruitment/input";
        }

        Recruitment recruitment = new Recruitment();
        // ユーザーはそのままセッションの loginUser をセット
        recruitment.setUser(loginUser);
        recruitment.setClubMaster(clubOpt.get());

        if (matchDateStr != null && !matchDateStr.isBlank()) {
            try {
                recruitment.setMatchDate(LocalDate.parse(matchDateStr));
            } catch (DateTimeParseException e) {
                // パース失敗時は何もせん（null のまま）
            }
        }

        recruitment.setMatchTime(matchTimeStr);
        recruitment.setLocation(location);
        recruitment.setRemarks(remarks);
        recruitment.setPurpose(purpose);
        recruitment.setSkill_level(skillLevel);
        recruitment.setConditions(conditions);

        recruitmentRepository.save(recruitment);

        return "user/input/recruit_input_complete";
    }

    // ===== 自分の募集一覧 =====
    @GetMapping("/list")
    public String MyRecruitList(HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Integer userId = loginUser.getUserId();

        List<Recruitment> recruitments =
                recruitmentRepository.findByUserUserId(userId);

        model.addAttribute("recruitments", recruitments);
        return "user/input/recruit_list";
    }

    // ===== 詳細画面 =====
    @GetMapping("/detail")
    public String RecruitDetail(@RequestParam("id") Integer id,
                                HttpSession session,
                                Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }

        Optional<Recruitment> opt = recruitmentRepository.findById(id);
        if (opt.isEmpty()) {
            return "redirect:/recruitment/list";
        }

        model.addAttribute("recruitment", opt.get());
        return "user/input/recruit_detail";
    }
}
