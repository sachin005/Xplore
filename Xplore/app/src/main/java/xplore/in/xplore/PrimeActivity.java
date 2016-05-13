package xplore.in.xplore;

import android.content.Context;
import android.content.Intent;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrimeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    private static final int NUMBER_OF_RESULTS = 40;
    private static final int RADIUS = 5000; // in meters
    private static final String FACEBOOK = "facebook";
    private static final String INSTAGRAM = "instagram";
    private static final String YELP = "yelp";
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private static Location mLocation;
    private static String searchText;
    private static final String TAG = "PrimeActivity";
    private static JSONArray resultJSONArray;
    private static String[] FB_PLACES_RESULT = {"not assigned"};
    private static String[] IG_RESULT = {"not assigned"};
    private static String[] YELP_RESULT = {"not assigned"};
    private static ProgressBar progressBar;
    private static Context context1;
    private static List<Long> iDs = new ArrayList<>();
    private static boolean backPressed = false; // flag to control updating of result in the tab
    private static boolean isPaused = false;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prime);

        FacebookSdk.sdkInitialize(getApplicationContext());

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
        Toast.makeText(this, "PrimeActivity resumed", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        mLocation = intent.getParcelableExtra("location");
        searchText = intent.getStringExtra("search text");
        context1 = PrimeActivity.this;

        Log.d(TAG, "on resume search text: " + searchText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if(mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);

            mViewPager.refreshDrawableState();
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if(tabLayout != null)
            tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backPressed = true;
    }

    // Todo: add spinner to show data is loading

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_prime, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static final String TITLE = "fragment_title";
        private static ArrayAdapter<String> listAdapter = null;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
//            args.putString(TITLE, title);
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_prime, container, false);

            // Todo: create a custom adapter to make the title bold in the listview and more

//            setListAdapter(listAdapter);
            return rootView;
        }

        @Override
        public void onListItemClick(ListView l, final View v, final int position, long id) {
            super.onListItemClick(l, v, position, id);

            try {
                new GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/" + 101204446588199L,// + "/link",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
            /* handle the result */
                                if(response != null) {
                                    Log.d(TAG, "on list item click request complete");
                                    Log.d(TAG, response.toString());
                                    displayPage(position);
                                }
                                else { Log.d(TAG, "response is null");}
                            }
                        }
                ).executeAsync();
            }
            catch(NullPointerException e) {
                Log.d(TAG, "null pointer exception occurred in graph request");
            }
            catch(Exception e) {
                Log.d(TAG, "error occurred during listview onclick listener graph request");
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            Log.d(TAG, "fragment on resume");
            Toast.makeText(getActivity().getBaseContext(), "fragment on resume", Toast.LENGTH_SHORT).show();

            final String network = getArguments().getString("network");

            // Todo: remove getPlaces method and its uses
            // PrimeActivity.getPlaces(network); // {"one", "two"};
//            ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(getActivity(),
//                    android.R.layout.simple_list_item_1, getPlacesResult(network));

            getArguments();
            new AsyncTask<Void, Integer, String[]>() {
                @Override
                protected String[] doInBackground(Void... voids) {

                    // make a request if network is not null and if the result is not assigned yet
                    if(network != null && network.equalsIgnoreCase(FACEBOOK) &&
                            (getPlacesResult(network)[0].equalsIgnoreCase("not assigned") || backPressed || !isPaused))
                        try {
                            backPressed = false;
                            isPaused = false;
                            GraphRequest.newPlacesSearchRequest(AccessToken.getCurrentAccessToken(),
                                    mLocation, RADIUS, NUMBER_OF_RESULTS, searchText != null?searchText:"mall",
                                    new GraphRequest.GraphJSONArrayCallback() {
                                        @Override
                                        public void onCompleted(JSONArray objects, GraphResponse response) {
                                            try {
//                                Log.d(TAG, objects.toString(3));
                                                setResultJSONArray(objects);
                                                Log.d(TAG, "json array length = " + objects.length());
                                                // extract required information from objects and assign to global variable
                                                setPlacesResult(organizeResult(objects, network), network);
                                                onPostExecute(getPlaces(network));
                                                /* Todo: find a better, efficient, ideal way to deal
                                                   with request and display result in the onPostExecuteResult()

                                                 */
                                            }
                                            catch(Exception e) {
                                                Log.d(TAG, "error occurred in json object toString");
                                            }
                                        }
                                    }).executeAsync();
                            return new String[0];
                        }
                        catch(Exception e) {
                            Log.d(TAG, "some error occurred in places search request");
                            e.printStackTrace();
                        }

                    else if(network != null && network.equalsIgnoreCase(INSTAGRAM)) {
//                        setAdapter();
                        Log.d(TAG, "network = instagram");
                        return new String[0];
                    }

                    else { // if(network != null && network.equalsIgnoreCase(YELP)) {
                        Log.d(TAG, "network = yelp");
                        return new String[0];
//                        setAdapter();
                    }

                    return new String[0];
                }

                protected void setAdapter() {
                    listAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,
                            getPlacesResult(network));
                    setListAdapter(listAdapter);
                }

                @Override
                protected void onPostExecute(String[] places) {
                    super.onPostExecute(places);
                    setAdapter();
//                    listAdapter = new ArrayAdapter<>(getActivity(),
//                            android.R.layout.simple_list_item_1, getPlacesResult(network));
//                    setListAdapter(listAdapter);
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                    progressBar.setProgress(values[0]);
                }
            }.execute();
        }

        @Override
        public void onPause() {
            super.onPause();
            isPaused = true;
        }
    }

    public static void displayPage(int position) {
        Intent intent = new Intent(context1, WebActivity.class);
        intent.putExtra("id", iDs.get(position));
        context1.startActivity(intent);
    }

    public static String[] getPlaces(final String network) {
        // method now redundant
        String[] result = {"place1", "place2", "couldn't retrieve results"};

        new AsyncTask<Void, Integer, String[]>() {
            @Override
            protected String[] doInBackground(Void... voids) {

                if(network.equalsIgnoreCase(FACEBOOK))
                    try {

                    }
                    catch(Exception e) {
                        Log.d(TAG, "some error occurred in places search request");
                        e.printStackTrace();
                    }

                else if(network.equalsIgnoreCase(INSTAGRAM)) {
                    Log.d(TAG, "network = instagram");
                }

                else if(network.equalsIgnoreCase(YELP)) {
                    Log.d(TAG, "network = yelp");
                }

                return new String[0];
            }

            @Override
            protected void onPostExecute(String[] places) {
                super.onPostExecute(places);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                progressBar.setProgress(values[0]);
            }
        };


        return result;
    }

    /*
     * gets necessary information from the object and populates the list of ids
     *
     */
    public static String[] organizeResult(JSONArray result, String network) {
        if(network.equalsIgnoreCase("facebook")) {
            List<String> fbPlaces = new ArrayList<>();
            try {
                for(int i = 0; i < result.length(); i++) {
                    JSONObject object = result.getJSONObject(i);
                    String name = object.getString("name");
                    double latitude = object.getJSONObject("location").getDouble("latitude");
                    double longitude = object.getJSONObject("location").getDouble("longitude");
                    String id = object.getString("id");
                    String street = object.getJSONObject("location").getString("street");
                    street = (!street.equalsIgnoreCase("")) ? (street + ", ") : "";
                    String city = object.getJSONObject("location").getString("city");
                    Log.d(TAG, name + " lat: " + latitude + " long: " + longitude + " id: " + id + " street: " + street + i + "\n");
                    fbPlaces.add(name + "\n" + street + city);
                    iDs.add(Long.parseLong(id));
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return fbPlaces.toArray(new String[fbPlaces.size()]);
        }
        else {
            return new String[]{"place1", "place2", "couldn't retrieve results from " + network };
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            final PlaceholderFragment placeholderFragment = PlaceholderFragment.newInstance(position + 1);
            Bundle bundle = new Bundle();
            bundle.putString("network", getPageTitle(position).toString());
            placeholderFragment.setArguments(bundle);
            return placeholderFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "facebook";
                case 1:
                    return "instagram";
                case 2:
                    return "Yelp";
            }
            return null;
        }
    }

    public static void setResultJSONArray(JSONArray array) {
        resultJSONArray = array;
    }

    public JSONArray getResultJSONArray() {
        return resultJSONArray;
    }

    public static void setPlacesResult(String[] result, String network) {
        switch (network) {
            case "facebook":
                FB_PLACES_RESULT = result;
                break;
            case "instagram":
                IG_RESULT = result;
                Log.d(TAG, "setting IG_RESULT = " + result.length);
                break;
            case "yelp":
                YELP_RESULT = result;
                Log.d(TAG, "setting YELP_RESULT = " + result.length);
                break;
        }
    }

    public static String[] getPlacesResult(String network) {
        if(network.equalsIgnoreCase("facebook"))
            return FB_PLACES_RESULT;
        if(network.equalsIgnoreCase("instagram"))
            return IG_RESULT;
        return YELP_RESULT;
    }
}