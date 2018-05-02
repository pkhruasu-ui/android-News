package com.example.android.news;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by prajakkhruasuwan on 11/19/17.
 */

public class NewsAdaptor extends ArrayAdapter<NYTNews> {

    private static final String BYLINE_SEPARATOR = "By ";
    private static final String TIMEZONE_SEPARATOR = "+0000";
    ImageDownloader imageDownloader = new ImageDownloader();

    public NewsAdaptor(Activity context, List<NYTNews> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View contextView, @NonNull ViewGroup parent) {
        View listItemView = contextView;
        ViewHolder holder;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.news, parent, false);
            // cache view fields into the holder
            holder = new ViewHolder(listItemView);
            // associate the holder with the view for later lookup
            listItemView.setTag(holder);
        } else {
            holder = (ViewHolder) listItemView.getTag();
        }

        // get current item
        final NYTNews currentNews = getItem(position);

        holder.titleView.setText(currentNews.getTitle());
        holder.descriptionView.setText(currentNews.getDescription());

        // source - need to check for "By " from response
        String source = currentNews.getSource();
        if (source.contains(BYLINE_SEPARATOR)) {
            String[] stubs = source.split(BYLINE_SEPARATOR);
            holder.sourceView.setText(stubs[1]);
        } else {
            holder.sourceView.setText(source);
        }

        // date - use proper format
        String dateString = formatDate(currentNews.getPublishDate());
        holder.publishDate.setText(dateString);

        // download image
        String thumbnailUrl = currentNews.getImageUrl();
        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            imageDownloader.download(thumbnailUrl, holder.thumbnailView);
        } else {
            holder.thumbnailView.setVisibility(View.GONE);
        }

        return listItemView;
    }

    // somewhere else in your class definition
    static class ViewHolder {
        @BindView(R.id.title)
        TextView titleView;
        @BindView(R.id.description)
        TextView descriptionView;
        @BindView(R.id.thumbnail)
        ImageView thumbnailView;
        @BindView((R.id.source))
        TextView sourceView;
        @BindView(R.id.publishDate)
        TextView publishDate;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(String date) {
        // Note: not sure if I should extract these strings.
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ssZ");
        SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy hh:mm a");

        try {
            return format.format(dateFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

    }
}
