package xplore.in.xplore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sachin on 4/3/2016.
 *
 */
public class FriendList {
    private static String TAG = "FriendList J ";
    private static List<String> friendsList = new ArrayList<>();

    // not used
    public static void getFriendList1(LoginResult loginResult, final Context context) {

        Log.d(TAG, "FriendList.java");
        final List<String> friendList = new ArrayList<>();
        AccessToken token = AccessToken.getCurrentAccessToken();
        Log.d(TAG, token.getToken());

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
                            friendList.add("error retrieving friend list .. response error not null");
                        }
                        else {
                            JSONObject jsonObject = graphResponse.getJSONObject();

                            Log.d(TAG, "in onCompleted");
                            try {
                                if (jsonObject.isNull("data")) {
//                                    Log.d(TAG, "jsonArray length = " + jsonArray.length());
                                    friendList.add("No friends use this app");
                                    Log.d(TAG, "jsonArray null or an object");
                                }
                                else {
                                    Log.d(TAG, " Json object: " + jsonObject.toString(1));
                                    JSONArray jsonArray = jsonObject.getJSONObject("friends").getJSONArray("data");
                                    Log.d(TAG, " jsonarray length: " + jsonArray.length());


                                    for(int i = 0; i < jsonArray.length(); i++) {
                                        friendList.add(jsonArray.getJSONObject(i).get("name").toString());
                                    }
                                    Log.d(TAG, "JSON object not null");
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                                Log.d(TAG, "some error in graphrequest.callback oncompleted");
                                friendList.add("Exception occured in graph request");
                            }

                        }


                    }
                }
        ).executeAsync();

//        Bundle parameters = new Bundle();
        Log.d(TAG, "friendlist size: " + friendList.size());
//        return friendList;
    }

    public static List<String> getFriendListFJ() {
        AccessToken token = AccessToken.getCurrentAccessToken();

        GraphRequest graphMeRequest = GraphRequest.newMeRequest(token,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        FacebookRequestError error = response.getError();
                        if (error != null) {
                            Log.d(TAG, "get friendsList1 response has error " + error.getErrorMessage());
                        } else {
                            JSONObject jsonObject = response.getJSONObject();
                            try {
                                if (jsonObject != null) {
                                    Log.d(TAG, " json object " + jsonObject.toString(2));
                                    makeFriendListFJ(jsonObject);
                                }
                            } catch (Exception e) {
                                Log.d(TAG, "error in graphMeRequest catch");
                                e.printStackTrace();
                            }
                        }
                    }
                });

        Bundle parameters = new Bundle();
//        ArrayList<String> perString = new ArrayList<>();
//        perString.addAll(permissions);
//        permissions.to();
        String fields = "id,name,link,location,verified";
        parameters.putString("fields", fields);
        graphMeRequest.setParameters(parameters);
        graphMeRequest.executeAsync();

        return new ArrayList<>(friendsList);
    }

    private static void makeFriendListFJ(JSONObject jsonObject) {
        List<String> friendsList = new ArrayList<>();
        try {
            friendsList.add(jsonObject.get("id").toString() + "|");
            friendsList.add(jsonObject.getJSONObject("location").getString("name") + "|");
            friendsList.add(jsonObject.getString("verified"));
            setFriendsList(friendsList);
        }
        catch (Exception e) {
            Log.d(TAG, "some exception in make friends list");
        }
    }

    private static void setFriendsList(List<String> list) {
        friendsList = list;
    }
}
