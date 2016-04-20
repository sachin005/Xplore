package xplore.in.xplore;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FriendListActivity extends AppCompatActivity {

    private final String TAG = "friendsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "Started friendListActivity");

//        List<String> friendsList = getFriendsList();

//        if(friendsFromBundle == null) {
//            friendsFromBundle = {{"Null reply"}};
//        }

        Intent intent = getIntent();
//        String[] friendsFromBundle = intent.getStringArrayExtra("friends_list");
//        Log.d(TAG, "friends from bundle array length: " + friendsFromBundle.length);

        // Todo: find a better way to pass list to another activity
        ArrayList<String> friendsList = (ArrayList<String>) intent.getSerializableExtra("fbdata"); // FriendList.getFriendList(); // Arrays.asList(friendsFromBundle);
        Location location = intent.getParcelableExtra("location");
//        String latNLong = intent.getStringExtra("latitude") + " " + intent.getStringExtra("longitude");
//        if(latNLong.isEmpty()) {
//            friendsList.add("latNLong is empty");
//        }
//        else
//            friendsList.add(latNLong);

        if(location != null)
            friendsList.add(location.getLatitude() + "\nlong: " + location.getLongitude());
        else
            friendsList.add("location is null");
        Log.d(TAG, "friendList length: " + friendsList.size());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.friends_listview, friendsList);
        ListView listView = (ListView) findViewById(R.id.friendslistView);

        if(listView != null)
            listView.setAdapter(arrayAdapter);

//        listView.setOnClickListener();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    protected List<String> getFriendsList() {

        final List<String> friendList = new ArrayList<>();
        Log.d(TAG, "in getFriendsList");
        // make API call
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONArray jsonArray = response.getJSONArray();
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
        );
        return friendList;
    }

}