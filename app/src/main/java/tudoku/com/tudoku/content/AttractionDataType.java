package tudoku.com.tudoku.content;

import android.content.ContentValues;

public class AttractionDataType {
    private int id;
    private String nameShort;
    private String nameLong;
    private String descriptionShort;
    private String descriptionLong;
    private String imageUrl;
    private int latitude;
    private int longitude;

    public void setId(int id) {
        this.id = id;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public void setNameLong(String nameLong) {
        this.nameLong = nameLong;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescriptionShort(String descriptionShort) {
        this.descriptionShort = descriptionShort;
    }

    public void setDescriptionLong(String descriptionLong) {
        this.descriptionLong = descriptionLong;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public ContentValues asContentValues() {
        ContentValues result = new ContentValues();
        result.put(Columns.ATTRACTION_ID, id);
        result.put(Columns.ATTRACTION_NAME_SHORT, nameShort);
        result.put(Columns.ATTRACTION_NAME_LONG, nameLong);
        result.put(Columns.ATTRACTION_DESCRIPTION_SHORT, descriptionShort);
        result.put(Columns.ATTRACTION_DESCRIPTION_LONG, descriptionLong);
        result.put(Columns.ATTRACTION_IMAGE_URL, imageUrl);
        result.put(Columns.ATTRACTION_LATITUDE, latitude);
        result.put(Columns.ATTRACTION_LONGITUDE, longitude);
        return result;
    }

    public int getId() {
        return id;
    }
}
