package edu.neu.pixelpainter;

public class ViewPagerItem {
    int imageID;
    String heading;

    boolean gray;

    public ViewPagerItem(int imageID, String heading, boolean gray) {
        this.imageID = imageID;
        this.heading = heading;
        this.gray = gray;

    }

    public int getImageID() {
        return imageID;
    }

    public void setImageID(int imageID) {
        this.imageID = imageID;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }
}
