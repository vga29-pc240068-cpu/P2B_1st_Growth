package jp.ac._st_Growth.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jp.ac._st_Growth.entity.CompetitionRecord;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.repository.CompetitionRecordRepository;

@Controller
public class ProfileController {

    @Autowired
    private CompetitionRecordRepository recordRepo;

    // アップロード保存先（プロジェクト直下に uploads/records）
    private static final String UPLOAD_DIR = "uploads/records";

    // =========================
    // プロフィール表示（大会記録一覧も表示）
    // =========================
    @GetMapping("/user/profile")
    public String profile(HttpSession session, Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        model.addAttribute("user", loginUser);

        if (loginUser != null) {
            model.addAttribute(
                "records",
                recordRepo.findByUserIdOrderByUploadedAtDesc(loginUser.getUserId())
            );
        } else {
            model.addAttribute("records", List.of());
        }

        return "user/profile/profile";
    }

    // =========================
    // 大会記録アップロード（jpg/png/pdf）
    // =========================
    @PostMapping("/user/profile/record/upload")
    public String uploadRecord(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title,
            HttpSession session
    ) throws IOException {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        String originalName = file.getOriginalFilename();
        String lower = (originalName == null) ? "" : originalName.toLowerCase();

        boolean allowed =
                lower.endsWith(".jpg") ||
                lower.endsWith(".jpeg") ||
                lower.endsWith(".png") ||
                lower.endsWith(".pdf");

        if (file.isEmpty() || !allowed) return "redirect:/user/profile";

        Files.createDirectories(Paths.get(UPLOAD_DIR));

        String storedName = UUID.randomUUID() + "_" + originalName.replaceAll("[\\\\/]", "_");
        Path savePath = Paths.get(UPLOAD_DIR, storedName);
        Files.copy(file.getInputStream(), savePath, StandardCopyOption.REPLACE_EXISTING);

        CompetitionRecord r = new CompetitionRecord();
        r.setUserId(loginUser.getUserId());
        r.setTitle(title == null ? "" : title);
        r.setOriginalName(originalName);
        r.setStoredName(storedName);
        r.setContentType(file.getContentType());
        r.setUploadedAt(new Timestamp(System.currentTimeMillis()));

        recordRepo.save(r);

        return "redirect:/user/profile";
    }

    // =========================
    // 記録ファイル表示（画像は<img>で表示、PDFは別タブで開ける）
    // =========================
    @GetMapping("/user/profile/record/file/{id}")
    public void recordFile(
            @PathVariable("id") Long id,
            HttpSession session,
            HttpServletResponse res
    ) throws IOException {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        CompetitionRecord r = recordRepo.findById(id).orElse(null);
        if (r == null || !r.getUserId().equals(loginUser.getUserId())) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        Path p = Paths.get(UPLOAD_DIR, r.getStoredName());
        if (!Files.exists(p)) {
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String ct = (r.getContentType() != null) ? r.getContentType() : "application/octet-stream";
        res.setContentType(ct);

        // 画面表示（PDFもブラウザで開く）
        res.setHeader("Content-Disposition", "inline; filename=\"" + r.getOriginalName() + "\"");

        Files.copy(p, res.getOutputStream());
    }
}
