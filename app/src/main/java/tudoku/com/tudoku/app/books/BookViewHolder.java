package tudoku.com.tudoku.app.books;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tudoku.com.tudoku.R;

public class BookViewHolder {

    private ImageView attractionImageView;
    private TextView attractionNameView;
    private TextView statusView;
    private TextView timeStartView;

    private int attractionId;

    public BookViewHolder(View v) {
        attractionImageView = (ImageView) v.findViewById(R.id.list_item_image);
        attractionNameView = (TextView) v.findViewById(R.id.list_item_name);
        statusView = (TextView) v.findViewById(R.id.list_item_status);
        timeStartView = (TextView) v.findViewById(R.id.list_item_date);
    }

    public ImageView getAttractionImageView() {
        return attractionImageView;
    }

    public TextView getAttractionNameView() {
        return attractionNameView;
    }

    public TextView getStatusView() {
        return statusView;
    }

    public TextView getTimeStartView() {
        return timeStartView;
    }

    public int getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(int attractionId) {
        this.attractionId = attractionId;
    }
}
