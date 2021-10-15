package app.visafe.ui;

public class Item {

    // Store the id of the  movie poster
    private int mImageDrawable;
    // Store the name of the movie
    private String mText;

    public Item(int mImageDrawable, String mtext) {
        this.mImageDrawable = mImageDrawable;
        this.mText = mtext;
    }

    public int getmImageDrawable() {
        return mImageDrawable;
    }

    public void setmImageDrawable(int mImageDrawable) {
        this.mImageDrawable = mImageDrawable;
    }

    public String getmName() {
        return mText;
    }

    public void setmName(String mName) {
        this.mText = mName;
    }
}