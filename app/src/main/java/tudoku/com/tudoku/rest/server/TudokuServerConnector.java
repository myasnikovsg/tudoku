package tudoku.com.tudoku.rest.server;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tudoku.com.tudoku.content.AttractionDataType;
import tudoku.com.tudoku.content.BookDataType;
import tudoku.com.tudoku.content.Columns;

public class TudokuServerConnector {

    private String authToken;

    public TudokuServerConnector(String authToken) {
        this.authToken = authToken;
    }

    public List<BookDataType> getBooks() {
        List<BookDataType> result = new ArrayList<>();
        File imagePath = Environment.getExternalStorageDirectory();
        File catsPath = new File(imagePath, "cats");

        BookDataType emperorCatBook = new BookDataType();
        emperorCatBook.setId(12);
        emperorCatBook.setAttractionId(55);
        emperorCatBook.setAttractionName("Emperor of catkind");
        File emperorCatPath = new File(catsPath, "emperor_of_catkind.jpg");
        emperorCatBook.setAttractionImageUrl(emperorCatPath.getAbsolutePath());
        emperorCatBook.setStatus("Reigning");
        emperorCatBook.setTimeStart(System.currentTimeMillis() + 1000 * 60 * 60 * 24);

        BookDataType happyCatBook = new BookDataType();
        happyCatBook.setId(13);
        happyCatBook.setAttractionId(66);
        happyCatBook.setAttractionName("Happy cat");
        File happyCatPath = new File(catsPath, "happy_cat.jpg");
        happyCatBook.setAttractionImageUrl(happyCatPath.getAbsolutePath());
        happyCatBook.setStatus("Happy");
        happyCatBook.setTimeStart(System.currentTimeMillis() + 1000 * 60 * 60 * 25);

        result.add(emperorCatBook);
        result.add(happyCatBook);

        return result;
    }

    // 12 -> 55
    // 13 -> 66
    // 14 -> 77
    public AttractionDataType getAttraction(int attractionId) {
        File imagePath = Environment.getExternalStorageDirectory();
        File catsPath = new File(imagePath, "cats");

        AttractionDataType result = new AttractionDataType();
        switch (attractionId) {
            case 55:
                result.setId(55);
                result.setNameShort("Emperor of catkind");
                result.setNameLong("All-mighty Emperor of catkind");
                result.setDescriptionShort("All hail to Emperor");
                result.setDescriptionLong("Bow, you, miserable!");
                File emperorCatPath = new File(catsPath, "emperor_of_catkind.jpg");
                result.setImageUrl(emperorCatPath.getAbsolutePath());
                result.setLatitude(100);
                result.setLongitude(100);
                break;
            case 66:
                result.setId(66);
                result.setNameShort("Happy cat");
                result.setNameLong("Happiest cat ever lived");
                result.setDescriptionShort("Happy cat is happy you're here");
                result.setDescriptionLong("Happy cat almost explodes of happiness");
                File happyCatPath = new File(catsPath, "happy_cat.jpg");
                result.setImageUrl(happyCatPath.getAbsolutePath());
                result.setLatitude(100);
                result.setLongitude(100);
                break;
        }

        return result;
    }

}
