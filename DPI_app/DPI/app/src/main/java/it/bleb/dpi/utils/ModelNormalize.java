package it.bleb.dpi.utils;

import java.util.List;

public class ModelNormalize {
    private String modelName;
    private List<Float> min;
    private List<Float> max;

    public ModelNormalize() {
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public List<Float> getMin() {
        return min;
    }

    public void setMin(List<Float> min) {
        this.min = min;
    }

    public List<Float> getMax() {
        return max;
    }

    public void setMax(List<Float> max) {
        this.max = max;
    }
}
