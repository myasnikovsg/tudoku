package tudoku.com.tudoku.content;

import android.content.ContentValues;

public class BookDataType {

    private int id;
    private int attractionId;
    private String attractionName;
    private String attractionImageUrl;
    private String status;
    private long timeStart;

    public void setId(int id) {
        this.id = id;
    }

    public void setAttractionId(int attractionId) {
        this.attractionId = attractionId;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }

    public void setAttractionImageUrl(String attractionImageUrl) {
        this.attractionImageUrl = attractionImageUrl;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public ContentValues asContentValues() {
        ContentValues result = new ContentValues();
        result.put(Columns.BOOK_ID, id);
        result.put(Columns.BOOK_ATTRACTION_ID, attractionId);
        result.put(Columns.BOOK_ATTRACTION_NAME, attractionName);
        result.put(Columns.BOOK_ATTRACTION_IMAGE_URL, attractionImageUrl);
        result.put(Columns.BOOK_STATUS, status);
        result.put(Columns.BOOK_TIME_START, timeStart);
        return result;
    }

    public int getId() {
        return id;
    }
}
