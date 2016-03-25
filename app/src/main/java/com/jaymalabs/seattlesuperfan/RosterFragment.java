package com.jaymalabs.seattlesuperfan;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class RosterFragment extends Fragment {

    private ArrayAdapter<String> mRosterAdapter;

    public RosterFragment() {
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

        mRosterAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this activity)
                R.layout.list_item_roster, // The name of the layout ID.
                R.id.list_item_roster_textview, // The ID of the textview to populate.
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_roster, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.listview_roster);
        listView.setAdapter(mRosterAdapter);

        for (int x = 0; x < 25; x++) {
            mRosterAdapter.add("Player " + (x+1));
        }

        return rootView;
    }
}
