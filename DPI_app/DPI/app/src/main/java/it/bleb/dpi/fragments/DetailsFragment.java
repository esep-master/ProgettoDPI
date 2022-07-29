package it.bleb.dpi.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.bleb.dpi.R;
import it.bleb.dpi.utils.DpiDetails;
import it.bleb.dpi.utils.RecyclerViewAdapter;

public class DetailsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private TextView emptyList;
    private ArrayList<DpiDetails> dpiDetailsList = new ArrayList<>();
    private static final String TAG = "DetailsActivity";

    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dpiDetailsList = (ArrayList<DpiDetails>) getArguments().getSerializable("beaconList");
            Log.d(TAG, "onCreate: " + dpiDetailsList.size());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        adapter = new RecyclerViewAdapter(dpiDetailsList, getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        emptyList = view.findViewById(R.id.empty_list);

        //Controllo presenza di DPI
        if (!dpiDetailsList.isEmpty()) {
            emptyList.setVisibility(View.GONE);
        } else {
            emptyList.setVisibility(View.VISIBLE);
        }
        return view;
    }
}