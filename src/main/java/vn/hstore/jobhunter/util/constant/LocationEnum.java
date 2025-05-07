package vn.hstore.jobhunter.util.constant;

public enum LocationEnum {
    HA_NOI("Hà Nội"),
    HO_CHI_MINH("TP HCM"),
    DA_NANG("Đà Nẵng"),
    HAI_PHONG("Hải Phòng"),
    CAN_THO("Cần Thơ"),
    AN_GIANG("An Giang"),
    BA_RIA_VUNG_TAU("Bà Rịa - Vũng Tàu"),
    BAC_GIANG("Bắc Giang"),
    BAC_KAN("Bắc Kạn"),
    BAC_LIEU("Bạc Liêu"),
    BAC_NINH("Bắc Ninh"),
    BEN_TRE("Bến Tre"),
    BINH_DINH("Bình Định"),
    BINH_DUONG("Bình Dương"),
    BINH_PHUOC("Bình Phước"),
    BINH_THUAN("Bình Thuận"),
    CA_MAU("Cà Mau"),
    CAO_BANG("Cao Bằng"),
    DAK_LAK("Đắk Lắk"),
    DAK_NONG("Đắk Nông"),
    DIEN_BIEN("Điện Biên"),
    DONG_NAI("Đồng Nai"),
    DONG_THAP("Đồng Tháp"),
    GIA_LAI("Gia Lai"),
    HA_GIANG("Hà Giang"),
    HA_NAM("Hà Nam"),
    HA_TINH("Hà Tĩnh"),
    HAI_DUONG("Hải Dương"),
    HAU_GIANG("Hậu Giang"),
    HOA_BINH("Hòa Bình"),
    HUNG_YEN("Hưng Yên"),
    KHANH_HOA("Khánh Hòa"),
    KIEN_GIANG("Kiên Giang"),
    KON_TUM("Kon Tum"),
    LAI_CHAU("Lai Châu"),
    LAM_DONG("Lâm Đồng"),
    LANG_SON("Lạng Sơn"),
    LAO_CAI("Lào Cai"),
    LONG_AN("Long An"),
    NAM_DINH("Nam Định"),
    NGHE_AN("Nghệ An"),
    NINH_BINH("Ninh Bình"),
    NINH_THUAN("Ninh Thuận"),
    PHU_THO("Phú Thọ"),
    PHU_YEN("Phú Yên"),
    QUANG_BINH("Quảng Bình"),
    QUANG_NAM("Quảng Nam"),
    QUANG_NGAI("Quảng Ngãi"),
    QUANG_NINH("Quảng Ninh"),
    QUANG_TRI("Quảng Trị"),
    SOC_TRANG("Sóc Trăng"),
    SON_LA("Sơn La"),
    TAY_NINH("Tây Ninh"),
    THAI_BINH("Thái Bình"),
    THAI_NGUYEN("Thái Nguyên"),
    THANH_HOA("Thanh Hóa"),
    THUA_THIEN_HUE("Thừa Thiên Huế"),
    TIEN_GIANG("Tiền Giang"),
    TRA_VINH("Trà Vinh"),
    TUYEN_QUANG("Tuyên Quang"),
    VINH_LONG("Vĩnh Long"),
    VINH_PHUC("Vĩnh Phúc"),
    YEN_BAI("Yên Bái");

    private final String displayName;

    LocationEnum(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static LocationEnum fromDisplayName(String displayName) {
        for (LocationEnum location : values()) {
            if (location.displayName.equalsIgnoreCase(displayName)) {
                return location;
            }
        }
        return null;
    }

    public static boolean isMajorCity(String location) {
        return location.equalsIgnoreCase(HA_NOI.displayName) ||
               location.equalsIgnoreCase(HO_CHI_MINH.displayName) ||
               location.equalsIgnoreCase(DA_NANG.displayName);
    }
} 