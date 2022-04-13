package am.datalogicbarcode.uri;

public class URL {
    public static final String BASEURL = "https://example.com:8000/";

    static final String DISPATCH_BARCODE = "dispatch/?bcode";
    static final String DISPATCH_VALIDATE_BARCODE = "dispatch_validate/?bcode=";
    static final String DISPATCH_PALLET_NO = "dispatch/?pno=";
    static final String DISPATCH_PALLET_COUNT = "dispatch_count/?pno=";
    static final String DISPATCH_DEVICE_ID = "dispatch_device/?device_id=";
    static final String DISPATCH_ROLL_NUMBER = "dispatch/?rollno=";

    public URL() {

    }

    public static String getBASEURL() {
        return BASEURL;
    }

    public static String getDispatchBarcode(String rollnum, String pno, String usid, String device_id) {
        return BASEURL + DISPATCH_BARCODE + rollnum + "&pno=" + pno + "&usid=" + usid + "&device_id=" + device_id;
    }

    public static String getDispatchPalletNo(String pno) {
        return BASEURL + DISPATCH_PALLET_NO + pno;
    }

    public static String getDispatchPalletCount(String pno) {
        return BASEURL + DISPATCH_PALLET_COUNT + pno;
    }

    public static String getDispatchValidateBarcode(String barcode) {
        return BASEURL + DISPATCH_VALIDATE_BARCODE + barcode;
    }

    public static String getDispatchDeviceId(String deivce_id) {
        return BASEURL + DISPATCH_DEVICE_ID + deivce_id;
    }

    public static String getDispatchRollNumber(String rollnum, String pno, String device_id) {
        return BASEURL + DISPATCH_ROLL_NUMBER + rollnum + "&pno=" + pno + "&device_id=" + device_id;
    }


}
