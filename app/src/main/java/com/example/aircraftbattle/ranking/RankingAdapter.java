package com.example.aircraftbattle.ranking;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aircraftbattle.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAYn on 2023.09.05 13:50.
 */
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingHolder> {
    private final List<RankingBean> mList = new ArrayList<>();

    public RankingAdapter() {

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<RankingBean> list) {
        mList.clear();
        if (list != null) {
            mList.addAll(list);
        }
        notifyDataSetChanged();
    }

    public RankingBean getItem(int position) {
        return mList.get(position);
    }

    @NonNull
    @Override
    public RankingAdapter.RankingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new RankingHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingAdapter.RankingHolder holder, int position) {
        RankingBean item = getItem(position);
        holder.mTvRanking.setText(String.valueOf(position + 1));
        holder.mTvScore.setText(String.valueOf(item.getScore()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class RankingHolder extends RecyclerView.ViewHolder {

        private final TextView mTvRanking;
        private final TextView mTvScore;

        public RankingHolder(@NonNull View itemView) {
            super(itemView);
            mTvRanking = itemView.findViewById(R.id.tv_ranking);
            mTvScore = itemView.findViewById(R.id.tv_score);
        }
    }
}
