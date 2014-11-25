package tudoku.com.tudoku.content;

public interface Columns {

    // Booking
    String BOOK_ID = "_id";
    String BOOK_ATTRACTION_ID = "attraction_id";
    String BOOK_ATTRACTION_NAME = "attraction_name";
    String BOOK_ATTRACTION_IMAGE_URL = "attraction_image_url";
    String BOOK_STATUS = "status";
    String BOOK_TIME_START = "time_start";

    // Attractions
    String ATTRACTION_ID = "_id";
    String ATTRACTION_NAME_SHORT = "name_short";
    String ATTRACTION_NAME_LONG = "name_long";
    String ATTRACTION_DESCRIPTION_SHORT = "description_short";
    String ATTRACTION_DESCRIPTION_LONG = "description_long";
    String ATTRACTION_IMAGE_URL = "image_path";
    String ATTRACTION_LATITUDE = "latitude";
    String ATTRACTION_LONGITUDE = "longitude";
}
