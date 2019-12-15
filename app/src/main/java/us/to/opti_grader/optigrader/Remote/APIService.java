package us.to.opti_grader.optigrader.Remote;


import us.to.opti_grader.optigrader.Model.DataMessage;
import us.to.opti_grader.optigrader.Model.MyResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAMo5oh8E:APA91bF2RFUXOOEZx7T05LCKl8YzLe6IYjbtrfWs3w6jP6sRuxScoeF_Dd2SX0GIe0sR7Odm8nccs3JtCeLUJy16WdQiCOUGsEeFpzum5JFgJizr3dW_ZBCTD4T7NeP_XhAbk1MnEa9e"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body DataMessage body);

}
