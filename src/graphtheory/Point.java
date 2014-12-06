package graphtheory;

public class Point {

    private double x;
    private double y;

    public Point() {
        x = 0;
        y = 0;
    }

    public Point(double x1, double y1) {
        x = x1;
        y = y1;
    }

    public Point(Point in) {
        x = in.getX();
        y = in.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getIntX() {
        return (int) x;
    }

    public int getIntY() {
        return (int) y;
    }

    public void setX(double x1) {
        x = x1;
    }

    public void setY(double y1) {
        y = y1;
    }

    public void setLocation(double x1, double y1) {
        x = x1;
        y = y1;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
