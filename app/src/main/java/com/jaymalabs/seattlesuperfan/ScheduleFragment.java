package com.jaymalabs.seattlesuperfan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ScheduleFragment extends Fragment {

    private final String LOG_TAG = FetchScheduleTask.class.getSimpleName();

    private ArrayAdapter<String> mScheduleAdapter;

    private boolean mNextGameFound;

    static public String mTeamWins;
    static public String mTeamLost;
    static public String mOppFirstName;
    static public String mOppLastName;
    static public String mEvenDate;
    static public String mLocation;
    static public String mOppWins;
    static public String mOppLost;

//    private boolean ROSTER_UPDATED = false;

    public ScheduleFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateSchedule();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        mScheduleAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_schedule, // The name of the layout ID.
                R.id.list_item_schedule_textview, // The ID of the textview to populate.
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_schedule);
        listView.setAdapter(mScheduleAdapter);

//        for (int x = 0; x < 25; x++) {
//            mRosterAdapter.add("Player " + (x+1));
//        }

        return rootView;
    }

    public void updateSchedule() {
        FetchScheduleTask ScheduleTask = new FetchScheduleTask();
        ScheduleTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();

//        if (!ROSTER_UPDATED) {
//            updateRoster();
//            ROSTER_UPDATED = true;
//            Log.v(LOG_TAG, "Updated Roster!");
//        }

    }

    public static String getTeamWins () {return mTeamWins;}

    public static String getTeamLost () {return mTeamLost;}

    public static String getOppFirstName () {return mOppFirstName;}

    public static String getOppLastName () {return mOppLastName;}

    public static String getOppWins () {return mOppWins;}

    public static String getOppLost () {return mOppLost;}

    public static String getLocation () {return mLocation;}

    public static String getDate () {return mEvenDate;}


    public class FetchScheduleTask extends AsyncTask<String, Void, String[]> {

        private final String LOG_TAG = FetchScheduleTask.class.getSimpleName();

        private String[] getScheduleDataFromJson(String scheduleJsonStr)
                throws JSONException{

            // These are the names of the JSON objects that need to be extracted.
            final String EVENTS = "events";
            final String EVENT_STATUS = "event_status";
            final String EVENT_DATE = "event_start_date_time";
            final String EVENT_LOCATION = "team_event_location_type";
            final String EVENT_RESULT = "team_event_result";
            final String EVENT_TEAM_WINS = "team_events_won";
            final String EVENT_TEAM_LOST = "team_events_lost";
            final String EVENT_TEAM_SCORE = "team_points_scored";
            final String EVENT_OPPONENT = "opponent";
            final String EVENT_OPP_SCORE = "opponent_points_scored";
            final String EVENT_OPP_FIRST_NAME = "first_name";
            final String EVENT_OPP_LAST_NAME = "last_name";
            final String EVENT_OPP_WINS = "opponent_events_won";
            final String EVENT_OPP_LOST = "opponent_events_lost";

//            JSONObject scheduleJson = new JSONObject(scheduleJsonStr);
            JSONArray scheduleArray = new JSONArray(scheduleJsonStr);

            String[] resultStrs = new String[scheduleArray.length()];
            for (int i = 0; i < scheduleArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String eStatus;
                String eResult;
                String eGameDate;
                Date eDate;
                int eTeamScore;
                String eOppName;
                int eOppScore = 0;
                String eLocation;
                String eCompString = "";

                // Get the JSON object representing the event
                JSONObject eventObject = scheduleArray.getJSONObject(i);
                // Get the JSON object representing the event opponent
                JSONObject opponentObject = eventObject.getJSONObject(EVENT_OPPONENT);

                eStatus = eventObject.getString(EVENT_STATUS);

                String dateStr = eventObject.getString(EVENT_DATE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                eDate = null;
                try {
                    eDate = sdf.parse(dateStr);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd  hh:mm");
                eGameDate = dateFormat.format(eDate);
                eLocation = eventObject.getString(EVENT_LOCATION);

                eOppName = opponentObject.getString(EVENT_OPP_LAST_NAME);

                if (eStatus.equals("completed")) {
                    eResult = eventObject.getString(EVENT_RESULT);
                    eTeamScore = eventObject.getInt(EVENT_TEAM_SCORE);
                    eOppScore = eventObject.getInt(EVENT_OPP_SCORE);

                    SimpleDateFormat compFormat = new SimpleDateFormat("EEEE, MMM dd");
                    eGameDate = compFormat.format(eDate);

                    if (eResult.equals("win")) {
                        eResult = "Win";
                    }
                    else {
                        eResult = "Loss";
                    }

                    eCompString = "\t" + eGameDate + "\t-\t" + eResult + "\t\tMariners "
                                + eTeamScore + " - " + eOppName + " " + eOppScore;
                }

                if (eLocation.equals("h")) {
                    eLocation = "vs.";
                }
                else {
                    eLocation = "@";
                }

                if (eStatus.equals("scheduled") && !mNextGameFound) {
                    mTeamWins = eventObject.getString(EVENT_TEAM_WINS);
                    mTeamLost = eventObject.getString(EVENT_TEAM_LOST);
                    mOppFirstName = opponentObject.getString(EVENT_OPP_FIRST_NAME);
                    mOppLastName = eOppName;
                    mOppWins = eventObject.getString(EVENT_OPP_WINS);
                    mOppLost = eventObject.getString(EVENT_OPP_LOST);
                    mLocation = eLocation;

                    SimpleDateFormat mainFormat = new SimpleDateFormat("EEEE, MMM dd");
                    mEvenDate = mainFormat.format(eDate);

                    mNextGameFound = true;
                }


                if (!eStatus.isEmpty()) {
                    if (eStatus.equals("completed")) {
                        resultStrs[i] = eCompString;
                    }
                    else {
                        resultStrs[i] = "\t" + eGameDate + "\t-\tMariners " + eLocation + " " + eOppName;
                    }
                }
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Events entry: " + s);
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
            String scheduleJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
//                String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=97850&mode=json&units=metric&cnt=7";
                final String XMLSTATS_ROSTER_BASE_URL =
                        "https://erikberg.com/mlb/results/seattle-mariners.json?season=2016&order=asc";
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

                scheduleJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Schedule JSON String: " + scheduleJsonStr);

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
                return getScheduleDataFromJson(scheduleJsonStr);
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
                mScheduleAdapter.clear();
                mScheduleAdapter.addAll(result);
            }
            else {
                mScheduleAdapter.clear();
                mScheduleAdapter.add("\t\tNo Internet Connection Detected");
            }
        }
    }
}
