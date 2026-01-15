package jp.ac._st_Growth.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.ac._st_Growth.entity.ClubMaster;
import jp.ac._st_Growth.entity.Recruitment;
import jp.ac._st_Growth.entity.Recruitment.AltDateOption;
import jp.ac._st_Growth.entity.Recruitment.CapacityRange;
import jp.ac._st_Growth.entity.Recruitment.GenderPref;
import jp.ac._st_Growth.entity.Recruitment.MatchContent;
import jp.ac._st_Growth.entity.Recruitment.TargetTeam;
import jp.ac._st_Growth.entity.Recruitment.TeamLevel;
import jp.ac._st_Growth.entity.Recruitment.TravelOption;
import jp.ac._st_Growth.entity.User;
import jp.ac._st_Growth.repository.ClubMasterRepository;
import jp.ac._st_Growth.repository.RecruitmentsRepository;

@Controller
@RequestMapping({"/recruitment", "/user/recruitment"})
public class RecruitmentController {

    @Autowired
    private RecruitmentsRepository recruitmentRepository;

    @Autowired
    private ClubMasterRepository clubMasterRepository;

    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ✅ confirm→regist の引き継ぎ用（セッションキー）
    private static final String SESS_RECRUIT_DRAFT = "recruitmentDraft";
    private static final String SESS_RECRUIT_CLUBID = "recruitmentDraftClubId";

    // =========================
    // トップ画面：他人の募集だけ
    // =========================
    @GetMapping("/all")
    public String recruitAll(HttpSession session, Model model) {

        User loginUser = requireLogin(session, model);
        if (loginUser == null) return "common/login/login";

        List<Recruitment> recruitments =
                recruitmentRepository.findByUserUserIdNotOrderByRecruitIdDesc(loginUser.getUserId());

        initListPageModel(model, "", emptyParamValues(), "all");
        model.addAttribute("recruitments", recruitments);

        return "user/input/recruit_list";
    }

    // =========================
    // 募集メニュー：自分の募集だけ
    // =========================
    @GetMapping({"/list", "/mine"})
    public String recruitMine(HttpSession session, Model model) {

        User loginUser = requireLogin(session, model);
        if (loginUser == null) return "common/login/login";

        List<Recruitment> recruitments =
                recruitmentRepository.findByUserUserIdOrderByRecruitIdDesc(loginUser.getUserId());

        initListPageModel(model, "", emptyParamValues(), "mine");
        model.addAttribute("recruitments", recruitments);

        return "user/input/recruit_list";
    }

    // =========================
    // 🗑️ 削除（GET/POST 両対応：URLは /recruitment/delete のまま）
    // =========================

    @PostMapping("/delete")
    public String deletePost(
            @RequestParam("recruitId") Integer recruitId,
            @RequestParam(defaultValue = "mine") String mode,
            HttpSession session,
            Model model
    ) {

        User loginUser = requireLogin(session, model);
        if (loginUser == null) return "common/login/login";

        // ✅ 削除対象が存在するかを確認いたします
        Optional<Recruitment> opt = recruitmentRepository.findByRecruitId(recruitId);
        if (opt.isEmpty()) {
            model.addAttribute("message", "削除対象が見つからんかったばい");
            // ✅ 画面遷移は既存の一覧へ戻します
            return "redirect:/recruitment/list";
        }

        Recruitment r = opt.get();

        // ✅ 自分の募集のみ削除できるようにチェックいたします
        if (r.getUser() == null || r.getUser().getUserId() == null
                || !r.getUser().getUserId().equals(loginUser.getUserId())) {
            model.addAttribute("message", "自分の募集以外は削除できんばい");
            return "redirect:/recruitment/list";
        }

        // ✅ 削除を実行いたします
        recruitmentRepository.deleteById(recruitId);

        // ✅ 一覧へ戻します（modeは既存の導線を壊さないため保持いたします）
        if ("all".equalsIgnoreCase(mode)) {
            return "redirect:/recruitment/all";
        }
        return "redirect:/recruitment/list";
    }

    @GetMapping("/delete")
    public String deleteGet(
            @RequestParam(value = "recruitId", required = false) Integer recruitId,
            @RequestParam(defaultValue = "mine") String mode,
            HttpSession session,
            Model model
    ) {

        User loginUser = requireLogin(session, model);
        if (loginUser == null) return "common/login/login";

        // ✅ 直アクセス(GET)でIDが無い場合は一覧へ戻します
        if (recruitId == null) {
            if ("all".equalsIgnoreCase(mode)) {
                return "redirect:/recruitment/all";
            }
            return "redirect:/recruitment/list";
        }

        // ✅ GETでも同じ削除処理へ流し込みます（既存のアクセス先を変更しないためです）
        return deletePost(recruitId, mode, session, model);
    }

    // =========================
    // 🔍 検索（modeで切替）
    // =========================
    @GetMapping("/search")
    public String search(
            @RequestParam(required = false) String keyword,

            @RequestParam(required = false) List<Integer> clubId,
            @RequestParam(required = false) List<String> altDateOption,
            @RequestParam(required = false) List<String> capacityRange,
            @RequestParam(required = false) List<String> genderPref,
            @RequestParam(required = false) List<String> travelOption,

            @RequestParam(required = false) List<String> targetTeam,
            @RequestParam(required = false) List<String> matchContent,
            @RequestParam(required = false) List<String> teamLevel,
            @RequestParam(required = false) Integer noPreference,

            @RequestParam(defaultValue = "all") String mode,
            HttpSession session,
            Model model
    ) {

        User loginUser = requireLogin(session, model);
        if (loginUser == null) return "common/login/login";

        String k = (keyword == null) ? "" : keyword.trim();
        boolean noPref = (noPreference != null && noPreference == 1);

        // ✅ 受け取り安全化（“入力そのもの”は保持用に別で使います）
        List<Integer> clubIdsIn = safeIntList(clubId);

        List<AltDateOption> altList = toEnumList(AltDateOption.class, altDateOption);
        List<CapacityRange> capList = toEnumList(CapacityRange.class, capacityRange);
        List<GenderPref> genList = toEnumList(GenderPref.class, genderPref);
        List<TravelOption> trvList = toEnumList(TravelOption.class, travelOption);

        List<TargetTeam> tgtList = toEnumList(TargetTeam.class, targetTeam);
        List<MatchContent> mcList = toEnumList(MatchContent.class, matchContent);
        List<TeamLevel> lvList = toEnumList(TeamLevel.class, teamLevel);

        // ✅ 「こだわらない」ONのときは3つを無視いたします
        if (noPref) {
            tgtList.clear();
            mcList.clear();
            lvList.clear();
        }

        boolean clubEmpty = clubIdsIn.isEmpty();
        boolean altEmpty  = altList.isEmpty();
        boolean capEmpty  = capList.isEmpty();
        boolean genEmpty  = genList.isEmpty();
        boolean trvEmpty  = trvList.isEmpty();

        boolean tgtEmpty  = tgtList.isEmpty();
        boolean mcEmpty   = mcList.isEmpty();
        boolean lvEmpty   = lvList.isEmpty();

        // ✅ Repository(IN)用：空IN事故防止（emptyFlag=trueなら無視される想定です）
        List<Integer> clubIdsQ = new ArrayList<>(clubIdsIn);
        if (clubEmpty) clubIdsQ.add(-1);

        if (altEmpty) altList.add(AltDateOption.values()[0]);
        if (capEmpty) capList.add(CapacityRange.values()[0]);
        if (genEmpty) genList.add(GenderPref.values()[0]);
        if (trvEmpty) trvList.add(TravelOption.values()[0]);

        if (tgtEmpty) tgtList.add(TargetTeam.values()[0]);
        if (mcEmpty)  mcList.add(MatchContent.values()[0]);
        if (lvEmpty)  lvList.add(TeamLevel.values()[0]);

        // ✅ mode正規化（mine以外はallとして扱います）
        boolean isMine = "mine".equalsIgnoreCase(mode);
        mode = isMine ? "mine" : "all";

        List<Recruitment> recruitments;
        if (isMine) {
            // ✅ 自分の募集だけ検索します（filterSearchMineMulti が存在する前提です）
            recruitments =
                    recruitmentRepository.filterSearchMineMulti(
                            k,
                            clubEmpty, clubIdsQ,
                            altEmpty, altList,
                            capEmpty, capList,
                            genEmpty, genList,
                            trvEmpty, trvList,
                            tgtEmpty, tgtList,
                            mcEmpty, mcList,
                            lvEmpty, lvList,
                            noPref ? 1 : 0,
                            loginUser.getUserId()
                    );
        } else {
            // ✅ 他人の募集だけ検索します（既存：excludeMine）
            recruitments =
                    recruitmentRepository.filterSearchExcludeMineMulti(
                            k,
                            clubEmpty, clubIdsQ,
                            altEmpty, altList,
                            capEmpty, capList,
                            genEmpty, genList,
                            trvEmpty, trvList,
                            tgtEmpty, tgtList,
                            mcEmpty, mcList,
                            lvEmpty, lvList,
                            noPref ? 1 : 0,
                            loginUser.getUserId()
                    );
        }

        // ✅ チェック保持（HTMLが paramValues を参照しているため、必ず作成します）
        Map<String, List<String>> pv = buildParamValues(
                clubIdsIn,
                safeStrList(altDateOption),
                safeStrList(capacityRange),
                safeStrList(genderPref),
                safeStrList(travelOption),
                safeStrList(targetTeam),
                safeStrList(matchContent),
                safeStrList(teamLevel),
                noPref
        );

        initListPageModel(model, k, pv, mode);
        model.addAttribute("recruitments", recruitments);

        if (recruitments.isEmpty()) {
            model.addAttribute("message", "条件に一致する募集は見つからんかったばい");
        } else {
            model.addAttribute("message", "検索結果ば表示しとるよ");
        }

        return "user/input/recruit_list";
    }

    // =========================
    // API 詳細（右側表示用：JSが期待するキーを全て返します）
    // =========================
    @GetMapping("/api/detail")
    @ResponseBody
    public Map<String, Object> apiDetail(@RequestParam("id") Integer id, HttpSession session) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return mapOf("error", "login_required");

        Optional<Recruitment> opt = recruitmentRepository.findByRecruitId(id);
        if (opt.isEmpty()) return mapOf("error", "not_found");

        Recruitment r = opt.get();

        Map<String, Object> res = new HashMap<>();
        res.put("recruitId", r.getRecruitId());

        res.put("clubName", (r.getClubMaster() != null) ? nz(r.getClubMaster().getClubName()) : "部活未設定");
        res.put("title", nz(r.getTitle()));

        // ✅ JS用：yyyy-MM-dd HH:mm 形式で返します
        res.put("startDateTime", fmtDateTime(r.getStartDateTime()));
        res.put("endDateTime", fmtDateTime(r.getEndDateTime()));

        // ✅ 右側チップ表示用（labelで返します）
        res.put("altDateOption", (r.getAltDateOption() != null) ? nz(r.getAltDateOption().getLabel()) : "");
        res.put("capacityRange", (r.getCapacityRange() != null) ? nz(r.getCapacityRange().getLabel()) : "");
        res.put("genderPref", (r.getGenderPref() != null) ? nz(r.getGenderPref().getLabel()) : "");
        res.put("travelOption", (r.getTravelOption() != null) ? nz(r.getTravelOption().getLabel()) : "");

        res.put("targetTeam", (r.getTargetTeam() != null) ? nz(r.getTargetTeam().getLabel()) : "");
        res.put("matchContent", (r.getMatchContent() != null) ? nz(r.getMatchContent().getLabel()) : "");
        res.put("teamLevel", (r.getTeamLevel() != null) ? nz(r.getTeamLevel().getLabel()) : "");

        // ✅ 右側本文（マップ/スケジュール/形式/備考）用です
        res.put("locationText", nz(r.getLocationText()));
        res.put("scheduleText", nz(r.getScheduleText()));
        res.put("matchFormat", nz(r.getMatchFormat()));
        res.put("notes", nz(r.getNotes()));

        res.put("noPreference", (r.getNoPreference() == null) ? 0 : r.getNoPreference());

        // ✅ 左カード表示で利用している場合があるため、文字列版も返します
        res.put("teamLevelText", nz(r.getTeamLevelText()));
        res.put("matchContentText", nz(r.getMatchContentText()));
        res.put("targetTeamText", nz(r.getTargetTeamText()));

        return res;
    }

    // =========================
    // 新規募集：入力
    // =========================
    @GetMapping("/input")
    public String inputForm(HttpSession session, Model model) {

        User loginUser = requireLogin(session, model);
        if (loginUser == null) return "common/login/login";

        // ✅ 画面側で使う選択肢をセットいたします
        setupInputOptions(model);

        // ✅ フォーム用のRecruitmentを用意いたします（th:object想定です）
        if (!model.containsAttribute("recruitment")) {
            model.addAttribute("recruitment", new Recruitment());
        }

        return "user/input/recruit_input";
    }

    // =========================
    // 新規募集：確認
    // =========================
    @PostMapping("/confirm")
    public String inputConfirm(
            @ModelAttribute("recruitment") Recruitment recruitment,
            @RequestParam(value = "clubId", required = false) Integer clubId,
            HttpSession session,
            Model model
    ) {

        User loginUser = requireLogin(session, model);
        if (loginUser == null) return "common/login/login";

        // ✅ 再描画に必要な選択肢をセットいたします
        setupInputOptions(model);

        // ✅ clubIdで送っている場合は、関連を補完いたします（既存のフォーム構造を壊さないためです）
        attachClubMasterIfNeeded(recruitment, clubId);

        // ✅ confirm→regist で値が落ちないよう、セッションに下書きを保存いたします
        session.setAttribute(SESS_RECRUIT_DRAFT, recruitment);
        session.setAttribute(SESS_RECRUIT_CLUBID, clubId);

        // ✅ 確認画面へ渡します
        model.addAttribute("recruitment", recruitment);

        return "user/input/recruit_input_check";
    }

    // =========================
    // 新規募集：登録→完了
    // =========================
    @PostMapping("/regist")
    public String regist(
            @ModelAttribute("recruitment") Recruitment recruitment,
            @RequestParam(value = "clubId", required = false) Integer clubId,
            HttpSession session,
            Model model
    ) {

        User loginUser = requireLogin(session, model);
        if (loginUser == null) return "common/login/login";

        // ✅ 確認画面からのPOSTでは、Recruitment本体が送られないケースがあるため、
        // ✅ セッションに保存した下書きを優先して復元いたします
        Recruitment draft = (Recruitment) session.getAttribute(SESS_RECRUIT_DRAFT);
        Integer draftClubId = (Integer) session.getAttribute(SESS_RECRUIT_CLUBID);

        if (draft != null) {
            recruitment = draft;
        }

        // ✅ clubId は request → session の順で補完いたします
        Integer cid = (clubId != null) ? clubId : draftClubId;

        // ✅ clubIdで送っている場合は、関連を補完いたします
        attachClubMasterIfNeeded(recruitment, cid);

        // ✅ 必須（DBでNOT NULL）のため、未設定なら入力へ戻します
        if (recruitment == null || recruitment.getClubMaster() == null) {
            // ✅ 入力画面再表示のため、選択肢をセットいたします
            setupInputOptions(model);
            model.addAttribute("recruitment", (recruitment == null) ? new Recruitment() : recruitment);
            model.addAttribute("message", "部活動が未選択のため登録できません。部活動を選択してください。");
            return "user/input/recruit_input";
        }

        // ✅ 登録者（ログインユーザー）をセットいたします
        recruitment.setUser(loginUser);

        // ✅ 登録を実行いたします
        recruitmentRepository.save(recruitment);

        // ✅ 下書きは完了後に破棄いたします
        session.removeAttribute(SESS_RECRUIT_DRAFT);
        session.removeAttribute(SESS_RECRUIT_CLUBID);

        model.addAttribute("message", "募集を登録しました");

        return "user/input/recruit_input_complete";
    }

    // =========================
    // 共通処理
    // =========================
    private User requireLogin(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loginUser");
        if (user == null) {
            model.addAttribute("error", "ログインしてください");
            return null;
        }
        return user;
    }

    private void initListPageModel(Model model, String keyword,
                                   Map<String, List<String>> pv, String mode) {
        model.addAttribute("mode", mode);
        model.addAttribute("keyword", (keyword == null) ? "" : keyword);
        model.addAttribute("paramValues", (pv == null) ? emptyParamValues() : pv);
        setupSearchOptions(model);
    }

    private void setupSearchOptions(Model model) {
        model.addAttribute("clubs", clubMasterRepository.findAll());
        model.addAttribute("altDateOptions", AltDateOption.values());
        model.addAttribute("capacityRanges", CapacityRange.values());
        model.addAttribute("genderPrefs", GenderPref.values());
        model.addAttribute("travelOptions", TravelOption.values());
        model.addAttribute("targetTeams", TargetTeam.values());
        model.addAttribute("matchContents", MatchContent.values());
        model.addAttribute("teamLevels", TeamLevel.values());
    }

    private void setupInputOptions(Model model) {
        // ✅ 入力画面で利用する選択肢をセットいたします
        model.addAttribute("clubs", clubMasterRepository.findAll());
        model.addAttribute("altDateOptions", AltDateOption.values());
        model.addAttribute("capacityRanges", CapacityRange.values());
        model.addAttribute("genderPrefs", GenderPref.values());
        model.addAttribute("travelOptions", TravelOption.values());
        model.addAttribute("targetTeams", TargetTeam.values());
        model.addAttribute("matchContents", MatchContent.values());
        model.addAttribute("teamLevels", TeamLevel.values());
    }

    private void attachClubMasterIfNeeded(Recruitment recruitment, Integer clubId) {
        // ✅ clubMaster が未設定で clubId がある場合は補完いたします
        if (recruitment == null) return;
        if (recruitment.getClubMaster() != null) return;
        if (clubId == null) return;

        Optional<ClubMaster> cm = clubMasterRepository.findById(clubId);
        cm.ifPresent(recruitment::setClubMaster);
    }

    private Map<String, List<String>> emptyParamValues() {
        Map<String, List<String>> m = new HashMap<>();
        m.put("clubId", new ArrayList<>());
        m.put("altDateOption", new ArrayList<>());
        m.put("capacityRange", new ArrayList<>());
        m.put("genderPref", new ArrayList<>());
        m.put("travelOption", new ArrayList<>());
        m.put("targetTeam", new ArrayList<>());
        m.put("matchContent", new ArrayList<>());
        m.put("teamLevel", new ArrayList<>());
        m.put("noPreference", new ArrayList<>());
        return m;
    }

    // ✅ チェック保持用（必ず“コピー”で返します）
    private Map<String, List<String>> buildParamValues(
            List<Integer> clubIds,
            List<String> altDateOption,
            List<String> capacityRange,
            List<String> genderPref,
            List<String> travelOption,
            List<String> targetTeam,
            List<String> matchContent,
            List<String> teamLevel,
            boolean noPref
    ) {
        Map<String, List<String>> pv = emptyParamValues();

        List<String> clubStr = new ArrayList<>();
        for (Integer cid : safeIntList(clubIds)) {
            if (cid != null) clubStr.add(String.valueOf(cid));
        }
        pv.put("clubId", clubStr);

        pv.put("altDateOption", copyList(altDateOption));
        pv.put("capacityRange", copyList(capacityRange));
        pv.put("genderPref", copyList(genderPref));
        pv.put("travelOption", copyList(travelOption));
        pv.put("targetTeam", copyList(targetTeam));
        pv.put("matchContent", copyList(matchContent));
        pv.put("teamLevel", copyList(teamLevel));

        List<String> np = new ArrayList<>();
        if (noPref) np.add("1");
        pv.put("noPreference", np);

        return pv;
    }

    private <E extends Enum<E>> List<E> toEnumList(Class<E> type, List<String> raws) {
        List<E> list = new ArrayList<>();
        if (raws == null) return list;

        for (String r : raws) {
            if (r == null) continue;
            String v = r.trim();
            if (v.isEmpty()) continue;

            v = v.toUpperCase().replace(" ", "").replace("　", "");
            try {
                list.add(Enum.valueOf(type, v));
            } catch (Exception ignore) {}
        }
        return list;
    }

    private List<Integer> safeIntList(List<Integer> l) {
        return (l == null) ? new ArrayList<>() : new ArrayList<>(l);
    }

    private List<String> safeStrList(List<String> l) {
        return (l == null) ? new ArrayList<>() : new ArrayList<>(l);
    }

    private List<String> copyList(List<String> src) {
        return (src == null) ? new ArrayList<>() : new ArrayList<>(src);
    }

    private String fmtDateTime(LocalDateTime dt) {
        return (dt == null) ? "" : dt.format(DT_FMT);
    }

    private String nz(String s) {
        return (s == null) ? "" : s;
    }

    private Map<String, Object> mapOf(String k, Object v) {
        Map<String, Object> m = new HashMap<>();
        m.put(k, v);
        return m;
    }
}
