package com.projectsexception.mzdroid.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projectsexception.mz.htmlapi.model.UserData;
import com.projectsexception.mzdroid.R;
import com.projectsexception.mzdroid.db.DBAdapter;
import com.projectsexception.mzdroid.util.ImageLoader;

public class MainFragment extends Fragment {
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_user, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DBAdapter adapter = DBAdapter.getInstance(getActivity());
        adapter.upgradeDatabase();
        populateUserView(getActivity());
    }
    
    public void populateUserView(Activity activity) {
        View userView = activity.findViewById(R.id.user_data);
        DBAdapter dbAdapter = DBAdapter.getInstance(activity);
        UserData userData = dbAdapter.readUserData();
        if (userData == null || userData.getTeam() == null) {
            activity.findViewById(R.id.user_no_data).setVisibility(View.VISIBLE);
            userView.setVisibility(View.GONE);
        } else {
            activity.findViewById(R.id.user_no_data).setVisibility(View.GONE);
            userView.setVisibility(View.VISIBLE);
            TextView textView = (TextView) userView.findViewById(R.id.team_name);
            ImageView imageView = (ImageView) userView.findViewById(R.id.user_image);
            imageView.setTag(userData.getUserImage());
            ImageLoader.requestPhoto(activity, imageView);
            textView.setText(userData.getTeam().getTeamName());
            textView = (TextView) userView.findViewById(R.id.user);
            textView.setText(createSpannable(activity, R.string.main_user, userData.getUsername()));
            textView = (TextView) userView.findViewById(R.id.series);
            textView.setText(createSpannable(activity, R.string.main_series, userData.getTeam().getSeriesName()));
            textView = (TextView) userView.findViewById(R.id.sponsor);
            String sponsor = userData.getTeam().getSponsor();
            if (sponsor == null || sponsor.length() == 0) {
                sponsor = activity.getString(R.string.main_no_sponsor);
            }
            textView.setText(createSpannable(activity, R.string.main_sponsor, sponsor));
            textView = (TextView) userView.findViewById(R.id.rank_pos);
            textView.setText(createSpannable(activity, R.string.main_rank_pos, Integer.toString(userData.getTeam().getRankPos())));
            textView = (TextView) userView.findViewById(R.id.rank_points);
            textView.setText(createSpannable(activity, R.string.main_rank_points, Integer.toString(userData.getTeam().getRankPoints())));
        }
    }
    
    protected SpannableString createSpannable(Context ctx, int title, String text) {
        String titleS = ctx.getString(title);
        SpannableString result = new SpannableString(titleS + ": " + text);
        result.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, titleS.length() + 2, 0);
        return result;
    }

}
