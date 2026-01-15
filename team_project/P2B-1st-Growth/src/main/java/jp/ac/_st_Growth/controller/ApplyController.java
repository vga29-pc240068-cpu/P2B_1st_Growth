package jp.ac._st_Growth.controller;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

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

    /**
     * コメント：応募系エラー画面のパスを統一いたします。
     */
    private static final String APPLY_ERROR_PAGE = "error/apply_error";

    /**
     * コメント：セッションからログインユーザーIDを取得いたします。
     * コメント：userId / loginUserId の両方に対応いたします。
     */
    private Integer getLoginUserId(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) return userId;
        return (Integer) session.getAttribute("loginUserId");
    }

    // =========================
    // ✅ 追加：現状のURL /application/apply?recruitId=○○ を受け取ります（404回避）
    // =========================
    @GetMapping("/application/apply")
    public String applyEntry(@RequestParam("recruitId") Integer recruitId,
                             HttpSession session,
                             Model model) {
        // コメント：既存の応募確認処理へ委譲いたします。
        return confirmApply(recruitId, session, model);
    }

    // =========================
    // 応募確認画面の表示（既存URLも残します）
    // =========================
    @GetMapping("/user/apply/check")
    public String confirmApply(@RequestParam("recruitId") Integer recruitId,
                               HttpSession session,
                               Model model) {

        Integer userId = getLoginUserId(session);
        if (userId == null) {
            // コメント：ログインしていない場合はログイン画面を表示いたします。
            model.addAttribute("error", "ログインしてからご利用ください。");
            return "common/login/login";
        }

        Optional<Recruitment> recruitmentOpt = recruitmentRepository.findByRecruitId(recruitId);
        if (recruitmentOpt.isEmpty()) {
            // コメント：募集情報が存在しない場合はエラー画面を表示いたします。
            model.addAttribute("error", "募集情報が見つかりませんでした。");
            return APPLY_ERROR_PAGE;
        }

        Recruitment r = recruitmentOpt.get();

        // コメント：自分の募集には応募できないようにいたします。
        Integer ownerId = (r.getUser() != null) ? r.getUser().getUserId() : null;
        if (ownerId != null && ownerId.equals(userId)) {
            model.addAttribute("error", "自分の募集には応募できません。");
            return APPLY_ERROR_PAGE;
        }

        // コメント：二重応募を防止いたします。
        boolean already = applicationsRepository
                .existsByUserUserIdAndRecruitmentRecruitId(userId, recruitId);
        if (already) {
            model.addAttribute("error", "すでに応募済みです。");
            return "redirect:/user/apply/list";
        }

        // コメント：確認画面で表示する募集情報を渡します。
        model.addAttribute("recruitment", r);
        return "user/apply/apply_check";
    }

    // =========================
    // 応募登録処理
    // =========================
    @PostMapping("/user/apply/regist")
    public String applyRecruitment(@RequestParam("recruitId") Integer recruitId,
                                   HttpSession session,
                                   Model model) {

        Integer userId = getLoginUserId(session);
        if (userId == null) {
            // コメント：ログインしていない場合はログイン画面を表示いたします。
            model.addAttribute("error", "ログインしてください。");
            return "common/login/login";
        }

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Recruitment> recruitmentOpt = recruitmentRepository.findByRecruitId(recruitId);

        if (userOpt.isEmpty() || recruitmentOpt.isEmpty()) {
            // コメント：必要な情報が取得できない場合はエラー画面を表示いたします。
            model.addAttribute("error", "ユーザーまたは募集情報が見つかりませんでした。");
            return APPLY_ERROR_PAGE;
        }

        Recruitment r = recruitmentOpt.get();

        // コメント：自分の募集には応募できないようにいたします。
        Integer ownerId = (r.getUser() != null) ? r.getUser().getUserId() : null;
        if (ownerId != null && ownerId.equals(userId)) {
            model.addAttribute("error", "自分の募集には応募できません。");
            return APPLY_ERROR_PAGE;
        }

        // コメント：二重応募を防止いたします。
        boolean already = applicationsRepository
                .existsByUserUserIdAndRecruitmentRecruitId(userId, recruitId);
        if (already) {
            model.addAttribute("error", "すでに応募済みです。");
            return "redirect:/user/apply/list";
        }

        Application application = new Application();
        application.setUser(userOpt.get());
        application.setRecruitment(r);
        application.setApplyDate(new Date(System.currentTimeMillis()));
        application.setStatus(0); // コメント：0=未承認として登録いたします。

        applicationsRepository.save(application);

        return "redirect:/user/apply/complete";
    }

    // =========================
    // 応募完了画面
    // =========================
    @GetMapping("/user/apply/complete")
    public String showApplyComplete(Model model) {
        // コメント：応募完了メッセージを表示いたします。
        model.addAttribute("message", "応募が完了しました！");
        return "user/apply/apply_complete";
    }

    // =========================
    // 応募一覧表示
    // =========================
    @GetMapping("/user/apply/list")
    public String applyList(Model model, HttpSession session) {

        Integer userId = getLoginUserId(session);
        if (userId == null) return "redirect:/login";

        // コメント：募集者に届いた応募を優先して取得いたします。
        List<Application> received =
                applicationsRepository.fetchReceivedApplicationsOrderByApplyIdDesc(userId);

        if (received != null && !received.isEmpty()) {
            model.addAttribute("applications", received);
            model.addAttribute("mode", "received");
            return "user/apply/apply_list";
        }

        // コメント：届いた応募が無い場合は、自分が応募した一覧を表示いたします。
        List<Application> mine =
                applicationsRepository.fetchMyApplicationsOrderByApplyIdDesc(userId);

        model.addAttribute("applications", mine);
        model.addAttribute("mode", "mine");
        return "user/apply/apply_list";
    }

    // =========================
    // 応募内容確認（承認・拒否画面）
    // =========================
    @GetMapping("/user/apply/possibility")
    public String applyPossibility(@RequestParam("applyId") Integer applyId,
                                   HttpSession session,
                                   Model model) {

        Integer loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";

        // コメント：画面側で使う可能性があるため、ログインIDを渡しておきます。
        model.addAttribute("loginUserId", loginUserId);

        Optional<Application> appOpt = applicationsRepository.fetchDetailByApplyId(applyId);
        if (appOpt.isEmpty()) {
            model.addAttribute("error", "応募情報が見つかりませんでした。");
            return APPLY_ERROR_PAGE;
        }

        model.addAttribute("application", appOpt.get());
        return "user/chat/apply_possibility";
    }

    // =========================
    // ✅ 応募詳細（一覧→詳細）※承認/拒否なし
    // =========================
    @GetMapping("/user/apply/detail")
    public String applyDetail(@RequestParam("applyId") Integer applyId,
                              HttpSession session,
                              Model model) {

        Integer loginUserId = getLoginUserId(session);
        if (loginUserId == null) return "redirect:/login";

        model.addAttribute("debugApplyId", applyId);
        model.addAttribute("loginUserId", loginUserId);

        // コメント：まず応募をJOIN FETCHで取得いたします。
        Optional<Application> appOpt = applicationsRepository.fetchDetailByApplyId(applyId);
        if (appOpt.isEmpty()) {
            model.addAttribute("application", null);
            model.addAttribute("recruitment", null);
            model.addAttribute("error", "応募情報が見つかりませんでした。applyIdをご確認ください。");
            return "user/apply/apply_detail";
        }

        Application app = appOpt.get();

        // ✅ 本命：recruitId から募集を取り直して、application.recruitment に詰め直します
        Recruitment rec = null;
        Integer recruitId = (app.getRecruitment() != null) ? app.getRecruitment().getRecruitId() : null;

        if (recruitId != null) {
            rec = recruitmentRepository.findByRecruitId(recruitId).orElse(null);
        }

        // ✅ これが無いとHTMLが application.recruitment を見に行って未定になる
        app.setRecruitment(rec);

        // コメント：Thymeleaf側は application を主に参照するため、application に確実に入れて渡します。
        model.addAttribute("application", app);

        // コメント：テンプレート側が recruitment を直接参照する場合にも備えて渡します。
        model.addAttribute("recruitment", rec);

        return "user/apply/apply_detail";
    }
}
