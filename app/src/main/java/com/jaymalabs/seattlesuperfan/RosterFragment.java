package com.jaymalabs.seattlesuperfan;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;


public class RosterFragment extends Fragment {

    private final String LOG_TAG = FetchRosterTask.class.getSimpleName();

    private ArrayAdapter<String> mRosterAdapter;

//    private boolean ROSTER_UPDATED = false;

    public RosterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        updateRoster();
        Log.v(LOG_TAG, "INSIDE ON CREATE");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(LOG_TAG, "INSIDE ON CREATE VIEW");
        // Inflate the layout for this fragment

        mRosterAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_roster, // The name of the layout ID.
                R.id.list_item_roster_textview, // The ID of the textview to populate.
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_roster, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_roster);
        listView.setAdapter(mRosterAdapter);

//        for (int x = 0; x < 25; x++) {
//            mRosterAdapter.add("Player " + (x+1));
//        }

        return rootView;
    }

    public void updateRoster() {
        FetchRosterTask RosterTask = new FetchRosterTask();
        RosterTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "INSIDE ON START");

//        if (!ROSTER_UPDATED) {
//            updateRoster();
//            ROSTER_UPDATED = true;
//            Log.v(LOG_TAG, "Updated Roster!");
//        }

    }

    public class FetchRosterTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchRosterTask.class.getSimpleName();

        private String[] getRosterDataFromJson(String rosterJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String ROS_PLAYERS = "players";
            final String ROS_FIRST_NAME = "first_name";
            final String ROS_LAST_NAME = "last_name";
            final String ROS_POSITION = "position";
            final String ROS_NUMBER = "uniform_number";
            final String ROS_AGE = "age";
            final String ROS_BATS = "bats";
            final String ROS_THROWS = "throws";

            JSONObject rosterJson = new JSONObject(rosterJsonStr);
            JSONArray rosterArray = rosterJson.getJSONArray(ROS_PLAYERS);

            String[] resultStrs = new String[rosterArray.length()];
            for (int i = 0; i < rosterArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String pFirstName;
                String pLastName;
                String pPosition;
                String pNumber;
                String pAge;
                String pBats;
                String pThrows;

                // Get the JSON object representing the player
                JSONObject playerObject = rosterArray.getJSONObject(i);

                pFirstName = playerObject.getString(ROS_FIRST_NAME);
                pLastName = playerObject.getString(ROS_LAST_NAME);
                pPosition = playerObject.getString(ROS_POSITION);
                pNumber = playerObject.getString(ROS_NUMBER);
                pAge = playerObject.getString(ROS_AGE);
                pBats = playerObject.getString(ROS_BATS);
                pThrows = playerObject.getString(ROS_THROWS);

                if ((i < 40) && (pNumber != null)) {
                    resultStrs[i] = "#" + pNumber + "  " + pFirstName+ " " + pLastName + "\t\tPos: " + pPosition
                    + "\t\tB/T: " + pBats + "/" + pThrows + "\t\tAge: " + pAge;
                }
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Roster entry: " + s);
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String rosterJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
//                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=97850&mode=json&units=metric&cnt=7";
                final String XMLSTATS_ROSTER_BASE_URL =
                        "https://erikberg.com/mlb/roster/seattle-mariners.json";
                final String AUTHORIZATION = "Authorization";
                final String BEARER_AUTH_TOKEN = "Bearer %s";
                final String USER_AGENT = "User-Agent";
                final String USER_AGENT_NAME = "Mariners App";
                final String ACCEPT_ENCODING = "Accept-encoding";
                final String GZIP = "gzip";

                String accessToken = String.format(BEARER_AUTH_TOKEN, BuildConfig.XMLSTATS_API_KEY);
                String userAgent = String.format(USER_AGENT_NAME, BuildConfig.VERSION, BuildConfig.USER_AGENT);
//                final String finalURL = AUTHORIZATION + BuildConfig.XMLSTATS_API_KEY + XMLSTATS_ROSTER_BASE_URL;
//                Uri builtUri = Uri.parse(XMLSTATS_ROSTER_BASE_URL).buildUpon()
//                        .authority(BuildConfig.XMLSTATS_API_KEY)
//                        .build();
//
//                Log.v(LOG_TAG, "AFTER BUILT URI");
////                String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                URL url = new URL(XMLSTATS_ROSTER_BASE_URL);

//                Log.v(LOG_TAG, "Built URI " + builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty(AUTHORIZATION, accessToken);
                urlConnection.setRequestProperty(USER_AGENT, userAgent);
//                urlConnection.setRequestProperty(ACCEPT_ENCODING, GZIP);

                urlConnection.connect();
                /*int statusCode = urlConnection.getResponseCode();
                String encoding = urlConnection.getContentEncoding();
                if (statusCode != HttpURLConnection.HTTP_OK) {
                    Log.e(LOG_TAG, "Server returned HTTP status: " +
                            statusCode + " URL Conn Resp: " + urlConnection.getResponseMessage());
                    return null;
                }*/

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    // Nothing to do.
                    Log.v(LOG_TAG, "NOTHING IN STREAM");
                    return null;
                }

//                if (GZIP.equals(encoding)) {
//                    inputStream = new GZIPInputStream(inputStream);
//                }
                Log.v(LOG_TAG, "AFTER STRINGBUILDER");
                reader = new BufferedReader(new InputStreamReader(inputStream));
                Log.v(LOG_TAG, "AFTER BUFFERREADER");
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    Log.v(LOG_TAG, "EMPTY STREAM");
                    return null;
                }

                rosterJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Roster JSON String: " + rosterJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error In FetchRoster", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            Log.v(LOG_TAG, "TRY TO GET ROSTER DATA");
            try {
                return getRosterDataFromJson(rosterJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                Log.v(LOG_TAG, "INSIDE ONPOSTEXECUTE");
                //clear roster and update
                mRosterAdapter.clear();
                mRosterAdapter.addAll(result);
            }
        }
    }
}
