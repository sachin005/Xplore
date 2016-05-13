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
import android.widget.AdapterView;
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

    private Intent intent;
    private final String TAG = "friendsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "Started friendListActivity");

        intent = getIntent();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Todo: find a better way to pass/ receive list to/ from another activity
        final ArrayList<String> searchText = (ArrayList<String>) intent.getSerializableExtra("search text");
        final Location location = intent.getParcelableExtra("location");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.friends_listview, searchText);
        ListView listView = (ListView) findViewById(R.id.friendslistView);

        if(listView != null) {
            listView.setAdapter(arrayAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                    Intent intent = new Intent(FriendListActivity.this, PrimeActivity.class);
                    intent.putExtra("location", location);
                    intent.putExtra("search text", searchText.get(position));
                    startActivity(intent);
                }
            });
            Toast.makeText(this, "FriendListActivity resumed", Toast.LENGTH_SHORT).show();
        }
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