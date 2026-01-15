package jp.ac._st_Growth.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "RECRUITMENTS")
public class Recruitment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RECRUIT_SEQ_GEN")
    @SequenceGenerator(name = "RECRUIT_SEQ_GEN", sequenceName = "RECRUIT_SEQ", allocationSize = 1)
    @Column(name = "RECRUIT_ID", nullable = false)
    private Integer recruitId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", referencedColumnName = "USER_ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CLUB_ID", referencedColumnName = "CLUB_ID", nullable = false)
    private ClubMaster clubMaster;

    // タイトル
    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    // 日程（開始・終了）
    // コメント：DB側がNULLを許容している/未設定があり得るなら nullable=true が安全です。
    @Column(name = "START_DATETIME")
    private LocalDateTime startDateTime;

    @Column(name = "END_DATETIME")
    private LocalDateTime endDateTime;

    // =========================
    // 別日程OKか
    // DB: SAME_DAY_ONLY / AROUND_OK / OTHER_DAY_OK
    // =========================
    @Enumerated(EnumType.STRING)
    @Column(name = "ALT_DATE_OPTION", length = 30)
    private AltDateOption altDateOption;

    public enum AltDateOption {
        SAME_DAY_ONLY("その日のみ"),
        AROUND_OK("前後日OK"),
        OTHER_DAY_OK("別日程も可");

        private final String label;
        AltDateOption(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // 募集人数レンジ
    @Enumerated(EnumType.STRING)
    @Column(name = "CAPACITY_RANGE", length = 30)
    private CapacityRange capacityRange;

    public enum CapacityRange {
        P1_5("1〜5人"),
        P6_10("6〜10人"),
        P11_15("11〜15人"),
        P16_20("16〜20人"),
        P21_30("21〜30人");

        private final String label;
        CapacityRange(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // =========================
    // 対象チーム / 試合内容 / チームレベル（enum）
    // =========================
    @Enumerated(EnumType.STRING)
    @Column(name = "TARGET_TEAM", length = 50)
    private TargetTeam targetTeam;

    public enum TargetTeam {
        JUNIOR_HIGH("中学生"),
        HIGH_SCHOOL("高校生"),
        UNIVERSITY("大学生"),
        ADULT("社会人");

        private final String label;
        TargetTeam(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "MATCH_CONTENT", length = 50)
    private MatchContent matchContent;

    public enum MatchContent {
        PRACTICE_MATCH("練習試合"),
        FRIENDLY_MATCH("交流試合"),
        JOINT_PRACTICE("合同練習"),
        INVITATIONAL("招待試合"),
        INTENSIVE_MATCH("強化試合"),
        TRAINING_MEET("練成会");

        private final String label;
        MatchContent(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TEAM_LEVEL", length = 50)
    private TeamLevel teamLevel;

    public enum TeamLevel {
        NATIONAL_BEST8("全国ベスト8"),
        PREF_BEST8("県大会ベスト8"),
        DISTRICT_BEST8("地区大会ベスト8"),
        NATIONAL_EXPERIENCE("全国大会出場経験あり"),
        PREF_EXPERIENCE("県大会出場経験あり"),
        DISTRICT_EXPERIENCE("地区大会出場経験あり"),
        MATCH_EXPERIENCE("試合経験あり");

        private final String label;
        TeamLevel(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // =========================
    // 場所（分割）
    // =========================
    @Column(name = "LOCATION_PREFECTURE", length = 20)
    private String locationPrefecture;

    @Column(name = "LOCATION_CITY", length = 100)
    private String locationCity;

    @Column(name = "LOCATION_ADDRESS_LINE", length = 200)
    private String locationAddressLine;

    @Column(name = "LOCATION_FACILITY", length = 200)
    private String locationFacility;

    @Column(name = "LOCATION_POSTAL_CODE", length = 8)
    private String locationPostalCode;

    // 表示用（DBは255）
    @Column(name = "LOCATION_TEXT", length = 255)
    private String locationText;

    @Column(name = "LATITUDE", precision = 10, scale = 7)
    private Double latitude;

    @Column(name = "LONGITUDE", precision = 10, scale = 7)
    private Double longitude;

    // スケジュール表示
    @Column(name = "SCHEDULE_TEXT", length = 4000)
    private String scheduleText;

    // 試合形式
    @Column(name = "MATCH_FORMAT", length = 255)
    private String matchFormat;

    // 注意事項
    @Column(name = "NOTES", length = 4000)
    private String notes;

    // こだわらない（DB: NOT NULL DEFAULT 0）
    @Column(name = "NO_PREFERENCE", nullable = false)
    private Integer noPreference = 0;

    // 性別条件
    @Enumerated(EnumType.STRING)
    @Column(name = "GENDER_PREF", length = 30)
    private GenderPref genderPref;

    public enum GenderPref {
        BOYS_ONLY("男子のみ"),
        GIRLS_ONLY("女子のみ"),
        MIXED("男女混合");

        private final String label;
        GenderPref(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // =========================
    // 既存TEXT（残してOK）
    // =========================
    @Column(name = "TEAM_LEVEL_TEXT", length = 1000)
    private String teamLevelText;

    @Column(name = "MATCH_CONTENT_TEXT", length = 1000)
    private String matchContentText;

    @Column(name = "TARGET_TEAM_TEXT", length = 200)
    private String targetTeamText;

    // 移動条件
    @Enumerated(EnumType.STRING)
    @Column(name = "TRAVEL_OPTION", length = 30)
    private TravelOption travelOption;

    public enum TravelOption {
        COME_ONLY("指定場所にこれる方のみ"),
        PROVIDE_ONLY("場所提供可能な方のみ"),
        EITHER_OK("どちらでも可能な方のみ");

        private final String label;
        TravelOption(String label) { this.label = label; }
        public String getLabel() { return label; }
    }

    // =========================
    // locationText 自動生成（255に収める）
    // =========================
    @PrePersist
    @PreUpdate
    private void buildLocationText() {
        // コメント：noPreference がnullのまま保存されるのを防ぎます。
        if (this.noPreference == null) this.noPreference = 0;

        String p = n(locationPrefecture);
        String c = n(locationCity);
        String a = n(locationAddressLine);
        String f = n(locationFacility);

        if (!isBlank(p) || !isBlank(c) || !isBlank(a) || !isBlank(f)) {
            StringBuilder sb = new StringBuilder();
            if (!isBlank(p)) sb.append(p);
            if (!isBlank(c)) sb.append(c);
            if (!isBlank(a)) sb.append(a);
            if (!isBlank(f)) sb.append(" ").append(f);

            String built = sb.toString().trim();
            if (built.length() > 255) built = built.substring(0, 255);
            this.locationText = built;
        } else {
            if (this.locationText != null) {
                this.locationText = this.locationText.trim();
                if (this.locationText.length() > 255) {
                    this.locationText = this.locationText.substring(0, 255);
                }
            }
        }
    }

    private String n(String s) { return s == null ? "" : s.trim(); }
    private boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }

    // ===== Getter / Setter =====
    public Integer getRecruitId() { return recruitId; }
    public void setRecruitId(Integer recruitId) { this.recruitId = recruitId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public ClubMaster getClubMaster() { return clubMaster; }
    public void setClubMaster(ClubMaster clubMaster) { this.clubMaster = clubMaster; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public LocalDateTime getStartDateTime() { return startDateTime; }
    public void setStartDateTime(LocalDateTime startDateTime) { this.startDateTime = startDateTime; }

    public LocalDateTime getEndDateTime() { return endDateTime; }
    public void setEndDateTime(LocalDateTime endDateTime) { this.endDateTime = endDateTime; }

    public AltDateOption getAltDateOption() { return altDateOption; }
    public void setAltDateOption(AltDateOption altDateOption) { this.altDateOption = altDateOption; }

    public CapacityRange getCapacityRange() { return capacityRange; }
    public void setCapacityRange(CapacityRange capacityRange) { this.capacityRange = capacityRange; }

    public TargetTeam getTargetTeam() { return targetTeam; }
    public void setTargetTeam(TargetTeam targetTeam) { this.targetTeam = targetTeam; }

    public MatchContent getMatchContent() { return matchContent; }
    public void setMatchContent(MatchContent matchContent) { this.matchContent = matchContent; }

    public TeamLevel getTeamLevel() { return teamLevel; }
    public void setTeamLevel(TeamLevel teamLevel) { this.teamLevel = teamLevel; }

    public String getLocationPrefecture() { return locationPrefecture; }
    public void setLocationPrefecture(String locationPrefecture) { this.locationPrefecture = locationPrefecture; }

    public String getLocationCity() { return locationCity; }
    public void setLocationCity(String locationCity) { this.locationCity = locationCity; }

    public String getLocationAddressLine() { return locationAddressLine; }
    public void setLocationAddressLine(String locationAddressLine) { this.locationAddressLine = locationAddressLine; }

    public String getLocationFacility() { return locationFacility; }
    public void setLocationFacility(String locationFacility) { this.locationFacility = locationFacility; }

    public String getLocationPostalCode() { return locationPostalCode; }
    public void setLocationPostalCode(String locationPostalCode) { this.locationPostalCode = locationPostalCode; }

    public String getLocationText() { return locationText; }
    public void setLocationText(String locationText) { this.locationText = locationText; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getScheduleText() { return scheduleText; }
    public void setScheduleText(String scheduleText) { this.scheduleText = scheduleText; }

    public String getMatchFormat() { return matchFormat; }
    public void setMatchFormat(String matchFormat) { this.matchFormat = matchFormat; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getNoPreference() { return noPreference; }
    public void setNoPreference(Integer noPreference) { this.noPreference = noPreference; }

    public GenderPref getGenderPref() { return genderPref; }
    public void setGenderPref(GenderPref genderPref) { this.genderPref = genderPref; }

    public String getTeamLevelText() { return teamLevelText; }
    public void setTeamLevelText(String teamLevelText) { this.teamLevelText = teamLevelText; }

    public String getMatchContentText() { return matchContentText; }
    public void setMatchContentText(String matchContentText) { this.matchContentText = matchContentText; }

    public String getTargetTeamText() { return targetTeamText; }
    public void setTargetTeamText(String targetTeamText) { this.targetTeamText = targetTeamText; }

    public TravelOption getTravelOption() { return travelOption; }
    public void setTravelOption(TravelOption travelOption) { this.travelOption = travelOption; }
}
