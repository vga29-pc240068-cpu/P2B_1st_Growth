package com.example.controller;

import java.util.Date;
import java.util.List;

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

@Controller
public class ApplyController {

    @Autowired
    private ApplicationsRepository applicationRepository;
    
    @Autowired
    private RecruitmentsRepository recruitmentRepository;

    // ==============================
    // 応募一覧表示
    // ==============================
    @GetMapping("/user/apply/list")
    public String showApplyList(Model model, HttpSession session) {
        // ログイン中ユーザーIDを取得
        Long userId = (Long) session.getAttribute("user_id");

        // 応募履歴をDBから取得
        List<Application> applications = applicationRepository.findByUserId(userId);

        // 画面に渡す
        model.addAttribute("applications", applications);

        // 応募一覧画面へ遷移
        return "user/apply/apply_list";
    }

    // ==============================
    // 応募確認画面の表示
    // ==============================
    @PostMapping("/user/apply/check")
    public String confirmApply(
            @RequestParam("recruitId") Long recruitId,
            Model model) {

        // DBから募集情報を1件取得
        Recruitment recruitment = recruitmentRepository.findById(recruitId).orElse(null);

        // 確認画面に渡す
        model.addAttribute("recruitment", recruitment);

        // 応募確認ページへ遷移
        return "user/apply/apply_check";
    }

    // ==============================
    // 応募登録処理
    // ==============================
    @PostMapping("/user/apply/regist")
    public String applyRecruitment(
            @RequestParam("recruitId") Recruitment recruitId,
            HttpSession session,
            Model model) {

        // ログイン中ユーザーのIDを取得
        User userId = (User) session.getAttribute("user_id");

        // 応募情報を新規作成
        Application application = new Application();
        application.setRecruitment(recruitId);
        application.setUser(userId);
        application.setApplyDate(Date.now());

        // DBへ保存
        applicationRepository.save(application);

        // 完了メッセージを画面に表示
        model.addAttribute("message", "応募が完了しました！");

        // 応募完了ページへ遷移
        return "user/apply/apply_complete";
    }

    // ==============================
    // 応募完了画面の表示
    // ==============================
    @GetMapping("/user/apply/complete")
    public String showApplyComplete() {
        return "user/apply/apply_complete";
    }
}