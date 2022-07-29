package it.bleb.dpi.utils;

import java.io.Serializable;

public class BeaconData implements Serializable{
    private Float x;
    private Float y;
    private Float z;
    private Float c;

    public BeaconData(Double x, Double y, Double z, Integer c) {
        this.x = x.floatValue();
        this.y = y.floatValue();
        this.z = z.floatValue();
        this.c = c.floatValue();
    }

    public BeaconData(Integer c) {
        this.c = c.floatValue();
    }

    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    public Float getZ() {
        return z;
    }

    public void setZ(Float z) {
        this.z = z;
    }

    public Float getC() {
        return c;
    }

    public void setC(Float c) {
        this.c = c;
    }

    public void setX(Double x) {
        this.x = x.floatValue();
    }

    public void setY(Double y) {
        this.y = y.floatValue();
    }

    public void setZ(Double z) {
        this.z = z.floatValue();
    }

    public void setC(Integer c) {
        this.c = c.floatValue();
    }

    @Override
    public String toString() {
        return "BeaconData{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", c=" + c +
                '}';
    }

}
