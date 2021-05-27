package com.bignerdranch.android.nerdlauncher;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.bignerdranch.android.nerdlauncher.R.*;
import static com.bignerdranch.android.nerdlauncher.R.layout.custom;

//Changes made by Abigail Simpson
/*Changes include:
*      Play sound when opening app
*      Implementing Cardview
*      Adding app icon to list*/


public class NerdLauncherFragment extends Fragment {
    public static final String TAG = "NerdLauncherFragment";

    private RecyclerView mRecyclerView;


    public static NerdLauncherFragment newInstance() {
        return new NerdLauncherFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(layout.fragment_nerd_launcher, container, false);
        mRecyclerView = (RecyclerView) v.findViewById(id.app_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        setupAdapter();
        return v;
    }

    private void setupAdapter() {
        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);
        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                PackageManager pm = getActivity().getPackageManager();
                return String.CASE_INSENSITIVE_ORDER.compare(
                        a.loadLabel(pm).toString(),
                        b.loadLabel(pm).toString());
            }
        });
        Log.i(TAG, "Found " + activities.size() + " activities.");
        mRecyclerView.setAdapter(new ActivityAdapter(getContext(), activities));
    }

    private class ActivityHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ResolveInfo mResolveInfo;
        private ImageView mImageView;
        public TextView mNameTextView;
        public MediaPlayer mp1;


        public ActivityHolder(View itemView) {
            super(itemView);
            mImageView = (itemView.findViewById(R.id.icon));
            mNameTextView = (TextView) itemView.findViewById(id.label);
            mNameTextView.setOnClickListener(this);
        }

        public void bindActivity(ResolveInfo resolveInfo) {
            mResolveInfo = resolveInfo;
            PackageManager pm = getActivity().getPackageManager();

            //Add App Icon to List
            Drawable icon = mResolveInfo.loadIcon(pm).getCurrent();
            mImageView.setImageDrawable(icon);

            String appName = mResolveInfo.loadLabel(pm).toString();
            mNameTextView.setText(appName);

        }

        @Override
        public void onClick(View view) {
            ActivityInfo activityInfo = mResolveInfo.activityInfo;

            //Starts sound when app selected
            mp1 = MediaPlayer.create(getContext(), R.raw.sample1);
            mp1.start();

            Intent i = new Intent(Intent.ACTION_MAIN)
                    .setClassName(activityInfo.applicationInfo.packageName,
                            activityInfo.name)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(i);
        }
    }

    private class ActivityAdapter extends RecyclerView.Adapter<ActivityHolder> {
        private Context context;
        private final List<ResolveInfo> mActivities;

        public ActivityAdapter(Context mContext, List<ResolveInfo> activities) {
            this.context = mContext;
            mActivities = activities;
        }


        @Override
        public ActivityHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            //Inflates activity to cardview
            View view = LayoutInflater.from(parent.getContext()).inflate(custom, parent, false);
            return new ActivityHolder(view);
        }

        @Override
        public void onBindViewHolder(ActivityHolder holder, int position) {
            ResolveInfo resolveInfo = mActivities.get(position);
            holder.bindActivity(resolveInfo);

        }

        @Override
        public int getItemCount() {
            return mActivities.size();
        }
    }


}
