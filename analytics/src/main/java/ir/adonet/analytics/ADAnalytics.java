package ir.adonet.analytics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

import static ir.adonet.analytics.Constants.ADO_NET;
import static ir.adonet.analytics.Constants.JSON_PARAM;

public class ADAnalytics {

    public static void initialize(Application application){
        Branch.setPlayStoreReferrerCheckTimeout(0);
        Branch.getAutoInstance(application);
    }

    @SuppressLint("LogNotTimber")
    public static void countSession(final Activity activity){
        Branch.getInstance().initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error != null){
                    Log.e("ADAnalytics", "error is not null");
                    Log.e("ADAnalytics", "error code: "+ error.getErrorCode());
                }else{
                    if (countSubmitted(activity)){
                        Log.d("ADAnalytics", "count already submitted");
                    }else{
                        Log.d("ADAnalytics", "begin to submit");
                        startIntentService(activity, referringParams);
                    }
                }
            }
        });
    }

    private static boolean countSubmitted(Context context){
        return context.getSharedPreferences(ADO_NET, Context.MODE_PRIVATE).getBoolean(Constants.IS_COUNT_SUBMITTED, false);
    }

    private static void startIntentService(Context context, JSONObject referring){
        Intent firstCheckDownload = new Intent(Intent.ACTION_SYNC, null, context, CounterService.class);
        Bundle bundle = new Bundle();

//        String fakeJson = "{\"test\": \"seyed\",\"test2\": 1,\"arr_map\": {\"simple\": false},\"arra\":[\"asdads\",\"asdasd\"]}";
//        try {
//            referring = new JSONObject(fakeJson);
//
//        } catch (JSONException e) {
//            Log.e("ADAnalytics", "failed to create the json");
//            e.printStackTrace();
//        }

        if (referring != null) {

            bundle.putString(JSON_PARAM, referring.toString());

            firstCheckDownload.putExtras(bundle);
            context.startService(firstCheckDownload);
        }
    }
}
