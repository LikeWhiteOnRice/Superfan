package com.jaymalabs.seattlesuperfan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainFragment extends Fragment {

    private TextView mSeattleMariners;
    private TextView mOpponent;

    private String teamWon;
    private String teamLost;
    private String oppWon;
    private String oppLost;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mSeattleMariners = (TextView) findViewById(R.id.tv_seattleMariners);
//    }

    public void updateTeamData() {
        FetchTeamTask TeamTask = new FetchTeamTask();
        TeamTask.execute();
    }

    @Override
    public void onStart () {
        super.onStart();

        if (getView() != null) {
            mSeattleMariners = (TextView) getView().findViewById(R.id.tv_seattleMariners);
            mOpponent = (TextView) getView().findViewById(R.id.tv_opponent);
        }

        updateTeamData();
    }

    public void setTeamData(String teamWon, String teamLost) {
//        mSeattleMariners.append("  (" + teamWon + "-" + teamLost + ")");
    }


    public class FetchTeamTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchTeamTask.class.getSimpleName();

        private void getTeamDataFromJson(String teamJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TEAM_ARRAY = "standing";
            final String TEAM_ID = "team_id";
            final String TEAM_WON = "won";
            final String TEAM_LOST = "lost";
            final String OPP_FIRST_NAME = "first_name";
            final String OPP_LAST_NAME = "last_name";
            final String OPP_WON = "won";
            final String OPP_LOST = "lost";

            final String MARINERS_ID = "seattle-mariners";

            JSONObject teamJson = new JSONObject(teamJsonStr);
            JSONArray teamArray = teamJson.getJSONArray(TEAM_ARRAY);

//            String teamWon = "0";
//            String teamLost = "0";
            String oppFirstName = "Unavailable";
            String oppLastNam = "";
//            String oppWon = "0";
//            String oppLost = "0";

            String[] resultStrs = new String[teamArray.length()];
            for (int i = 0; i < teamArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"


                // Get the JSON object representing the player
                JSONObject teamObject = teamArray.getJSONObject(i);

                if (teamObject.getString(TEAM_ID).equals(MARINERS_ID)) {
                    teamWon = teamObject.getString(TEAM_WON);
                    teamLost = teamObject.getString(TEAM_LOST);
                    Log.v(LOG_TAG, "FOUND MARINERS");
                }

                if (teamObject.getString(TEAM_ID).equals("texas-rangers")) {
                    oppWon = teamObject.getString(OPP_WON);
                    oppLost = teamObject.getString(OPP_LOST);
                }

//                oppFirstName = teamObject.getString(OPP_FIRST_NAME);
//                oppLastName = teamObject.getString(OPP_LAST_NAME);

            }

            Log.v(LOG_TAG, "MARINERS APPEND");
//            setTeamData(teamWon, teamLost);



        }

        @Override
        protected String[] doInBackground(String... params) {

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String teamJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
//                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=97850&mode=json&units=metric&cnt=7";
                final String XMLSTATS_ROSTER_BASE_URL =
                        "https://erikberg.com/mlb/standings.json";
//                final String AUTHORIZATION = "Authorization";
//                final String BEARER_AUTH_TOKEN = "Bearer %s";
//                final String USER_AGENT = "User-Agent";
//                final String USER_AGENT_NAME = "Mariners App";
//                final String ACCEPT_ENCODING = "Accept-encoding";
//                final String GZIP = "gzip";

//                String accessToken = String.format(BEARER_AUTH_TOKEN, BuildConfig.XMLSTATS_API_KEY);
//                String userAgent = String.format(USER_AGENT_NAME, BuildConfig.VERSION, BuildConfig.USER_AGENT);
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
//                urlConnection.setRequestProperty(AUTHORIZATION, accessToken);
//                urlConnection.setRequestProperty(USER_AGENT, userAgent);
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
                reader = new BufferedReader(new InputStreamReader(inputStream));

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

                teamJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Roster JSON String: " + teamJsonStr);

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
            try {
                getTeamDataFromJson(teamJsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                mSeattleMariners.append("  (" + teamWon + "-" + teamLost + ")");
                mOpponent.append("  (" + oppWon + "-" + oppLost + ")");
            }
        }
    }
}
