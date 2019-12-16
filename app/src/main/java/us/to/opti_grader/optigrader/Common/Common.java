package us.to.opti_grader.optigrader.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import us.to.opti_grader.optigrader.Model.ExamList;
import us.to.opti_grader.optigrader.Model.Subject;
import us.to.opti_grader.optigrader.Model.SubjectScore;
import us.to.opti_grader.optigrader.Model.User;
import us.to.opti_grader.optigrader.Remote.APIService;
import us.to.opti_grader.optigrader.Remote.GoogleRetrofitClient;
import us.to.opti_grader.optigrader.Remote.IGoogleService;
import us.to.opti_grader.optigrader.Remote.RetrofitClient;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Common {
    public static final Object DISABLE_TAG = "DISABLE";
    public static String PHONE_TEXT = "userPhone";
    public static final String KEY_ENABLE_BUTTON_NEXT = "ENABLE_BUTTON_NEXT";
    public static final String KEY_TIME_SLOT = "TIME_SLOT";
    public static final String KEY_STEP = "STEP";
    public static final String KEY_CONFIRM_BOOKING = "CONFIRM_BOOKING";
    public static int currentTimeSlot = -1;
    public static User currentUser;
    public static Subject currentSubject;
    public static SubjectScore currentSubjectScore;
    public static ExamList currentExam;
    public static User currentCompany;
    public static Calendar currentDate = Calendar.getInstance();
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");

    public static int step = 0;
    public static final int TIME_SLOT_TOTAL = 10;

    public static final String INTENT_SUBJECT_ID = "subjectId";
    public static final String INTENT_SUBJECT_NAME = "subjectName";

    private static final String BASE_URL = "https://fcm.googleapis.com";
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com";

    public static APIService getFCMService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleMapAPI() {
        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }
}
