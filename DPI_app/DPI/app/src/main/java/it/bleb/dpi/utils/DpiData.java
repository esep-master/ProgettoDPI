package it.bleb.dpi.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DpiData implements Serializable {
    private List<BeaconData> beaconDataArray;
    private String modelName;
    private String imageView;

    public DpiData(String modelName,String imageView) {
        this.modelName = modelName;
        this.imageView = imageView;
        beaconDataArray = new ArrayList<>();
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }


    public List<BeaconData> getBeaconDataArray() {
        return beaconDataArray;
    }

    public void setBeaconDataArray(List<BeaconData> beaconDataArray) {
        this.beaconDataArray = beaconDataArray;
    }

    public String getImageView() {
        return imageView;
    }

    public void setImageView(String imageView) {
        this.imageView = imageView;
    }
}
