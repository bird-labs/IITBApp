package com.gymkhana.iitbapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.gymkhana.iitbapp.MainActivity;
import com.gymkhana.iitbapp.R;
import com.gymkhana.iitbapp.feed.RSSFeedConstants;
import com.gymkhana.iitbapp.lvadapter.LVAdapterRSSSubscription;
import com.gymkhana.iitbapp.util.Functions;

@SuppressLint("NewApi")
public class RSSSubscriptionFragment extends Fragment {

    public String mTitle;
    public AdapterView.OnItemClickListener mOnItemClickListener;
    private Context mContext;
    private MainActivity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.listview_layout, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.list);
        listView.setOnItemClickListener(mOnItemClickListener);
        listView.setAdapter(new LVAdapterRSSSubscription(mContext, RSSFeedConstants.feeds));
        Functions.setActionBarTitle(mActivity, mContext.getString(R.string.drawer_feed));
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
        mActivity = (MainActivity) activity;
    }
}
