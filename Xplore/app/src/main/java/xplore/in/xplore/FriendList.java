package xplore.in.xplore;

import android.content.Intent;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sachin on 4/3/2016.
 *
 */
public class FriendList {
    private static String TAG = "FriendList J ";
    public static List<String> getFriendList(LoginResult loginResult) {

        Log.d(TAG, "FriendList.java");
        final List<String> friendList = new ArrayList<>();
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse graphResponse) {
//                        graphResponse.getJSONArray().getJSONObject(0);
                        FacebookRequestError error = graphResponse.getError();
                        if(error != null) {
                            String errorMessage = error.getErrorMessage();
                            Log.d(TAG, errorMessage + " -- error message");
                        }
                        else {
                            JSONArray jsonArray = graphResponse.getJSONArray();
                            Log.d(TAG, "jsonArray length = " + jsonArray.length());
                            Log.d(TAG, "in onCompleted");
                            try {
                                if (jsonArray.length() == 0 || jsonArray.isNull(0)) {
                                    friendList.add("No friends use this app");
                                    Log.d(TAG, "jsonArray 0 or null");
                                }
                                else {
                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        friendList.add(jsonArray.getJSONObject(i).get("name").toString());
                                    }
                                    Log.d(TAG, "json -- for" + jsonArray.length());
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "some error in graphrequest.callback oncompleted");
                            }
                        }

                    }
                }
        ).executeAsync();
        return friendList;
    }

}
