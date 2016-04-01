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

    private TextView tvSeattleMariners;
    private TextView tvOpponent;
    private TextView tvGameDate;
    private TextView tvLocation;
    private TextView tvRank;
    private TextView tvGamesBack;
    private TextView tvWinPercent;
    private TextView tvWinStreak;

    private String mTeamWon;
    private String mTeamLost;
    private String mOppWon;
    private String mOppLost;
    private String mOppFirstName;
    private String mOppLastName;
    private String mLocation;
    private String mEventDate;

    private String mTeamRank;
    private String mTeamGamesBack;
    private String mTeamWinPercentage;
    private String mTeamStreakType;


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
            tvSeattleMariners = (TextView) getView().findViewById(R.id.tv_seattleMariners);
            tvOpponent = (TextView) getView().findViewById(R.id.tv_opponent);
            tvGameDate = (TextView) getView().findViewById(R.id.tv_gameDate);
            tvLocation = (TextView) getView().findViewById(R.id.tv_location);
            tvRank = (TextView) getView().findViewById(R.id.tv_rank);
            tvGamesBack = (TextView) getView().findViewById(R.id.tv_gamesBack);
            tvWinPercent = (TextView) getView().findViewById(R.id.tv_winPercent);
            tvWinStreak = (TextView) getView().findViewById(R.id.tv_winStreak);
        }

        updateTeamData();
    }

    public void getGameData() {
        mTeamWon = ScheduleFragment.getTeamWins();
        mTeamLost = ScheduleFragment.getTeamLost();
        mOppWon = ScheduleFragment.getOppWins();
        mOppLost = ScheduleFragment.getOppLost();
        mOppFirstName = ScheduleFragment.getOppFirstName();
        mOppLastName = ScheduleFragment.getOppLastName();
        mLocation = ScheduleFragment.getLocation();
        mEventDate = ScheduleFragment.getDate();
    }


    public class FetchTeamTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchTeamTask.class.getSimpleName();

        private void getTeamDataFromJson(String teamJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String TEAM_ARRAY = "standing";
            final String TEAM_ID = "team_id";
            final String TEAM_RANK = "ordinal_rank";
            final String TEAM_GAMES_BACK = "games_back";
            final String TEAM_WIN_PERCENTAGE = "win_percentage";
            final String TEAM_STREAK_TYPE = "streak_type";
            final String TEAM_STREAK_TOTAL = "streak_total";
            final String MARINERS_ID = "seattle-mariners";

            double tGamesBack = 999;
            int tStreakTotal = -1;

            JSONObject teamJson = new JSONObject(teamJsonStr);
            JSONArray teamArray = teamJson.getJSONArray(TEAM_ARRAY);

            for (int i = 0; i < teamArray.length(); i++) {

                // Get the JSON object representing the team
                JSONObject teamObject = teamArray.getJSONObject(i);

                if (teamObject.getString(TEAM_ID).equals(MARINERS_ID)) {
                    mTeamRank = teamObject.getString(TEAM_RANK);
                    tGamesBack = teamObject.getDouble(TEAM_GAMES_BACK);
                    mTeamWinPercentage = teamObject.getString(TEAM_WIN_PERCENTAGE);
                    mTeamStreakType = teamObject.getString(TEAM_STREAK_TYPE);
                    tStreakTotal = teamObject.getInt(TEAM_STREAK_TOTAL);
                }
            }

            if (mTeamStreakType.equals("win")) {
                mTeamStreakType = tStreakTotal + " Game Winning Streak";
            }
//            else if (mTeamStreakType.equals("win") && tStreakTotal == 1) {
//                mTeamStreakType = tStreakTotal + " Game Win Streak";
//            }
            else if (mTeamStreakType.equals("loss")) {
                mTeamStreakType = tStreakTotal + " Game Losing Streak";
            }
//            else if (mTeamStreakType.equals("loss") && tStreakTotal == 1) {
//                mTeamStreakType = tStreakTotal + " Game Loss Streak";
//            }

            if (tGamesBack != 999) {
                mTeamGamesBack = String.valueOf(tGamesBack);
            }

            Log.v(LOG_TAG, "MARINERS APPEND");

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
                Log.v(LOG_TAG, "Standings JSON String: " + teamJsonStr);

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

            getGameData();

            if (mEventDate != null) {
                tvGameDate.setText(mEventDate);
                tvSeattleMariners.append("  (" + mTeamWon + "-" + mTeamLost + ")");
                tvLocation.setText(mLocation);
                tvOpponent.setText(mOppFirstName + " " + mOppLastName + "  (" + mOppWon + "-" + mOppLost + ")");
                tvRank.setText(mTeamRank + " in the " + tvRank.getText());
                tvGamesBack.setText(mTeamGamesBack + " " + tvGamesBack.getText());
                tvWinPercent.setText(mTeamWinPercentage + " " + tvWinPercent.getText());
                tvWinStreak.setText(mTeamStreakType);
            }
        }
    }
}