package edu.neu.pixelpainter;

public class ViewPagerItem {
    int imageID;
    String heading;

    public ViewPagerItem(int imageID, String heading) {
        this.imageID = imageID;
        this.heading = heading;
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
