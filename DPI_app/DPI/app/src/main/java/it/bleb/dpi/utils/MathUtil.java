package it.bleb.dpi.utils;

import java.util.List;

public class MathUtil {

    private static MathUtil instance;

    private MathUtil() {
    }

    /**
     * get math util instance
     *
     * @return
     */
    public static synchronized MathUtil getInstance() {
        if (instance == null) {
            instance = new MathUtil();
        }
        return instance;
    }

    /**
     * calculate min of array
     *
     * @param elementsArray
     * @return min of array
     */
    public float getMin(List<Float> elementsArray) {
        float min = Float.MAX_VALUE;
        if (elementsArray != null) {
            for (Float el : elementsArray) {
                if (el < min) {
                    min = el;
                }
            }
        }
        return min;
    }

    /**
     * calculate max of array
     *
     * @param elementsArray
     * @return max of array
     */
    public float getMax(List<Float> elementsArray) {
        float max = Float.MIN_VALUE;
        if (elementsArray != null) {
            for (Float el : elementsArray) {
                if (el > max) {
                    max = el;
                }
            }
        }
        return max;
    }

    /**
     * calculate average of array
     *
     * @param elementsArray
     * @return avg of array
     */
    public float getAvg(List<Float> elementsArray) {
        float avg = 0;
        float sum = 0;
        if (elementsArray != null) {
            for (Float el : elementsArray) {
                sum += el;
            }
            avg = sum / elementsArray.size();
        }
        return avg;
    }

    /**
     * calculate standard deviation of array
     *
     * @param elementsArray
     * @return standard deviation of array
     */
    public float getSD(List<Float> elementsArray) {
        float sdSum = 0;
        if (elementsArray != null) {
            float avg = this.getAvg(elementsArray);

            for (Float el : elementsArray) {
                sdSum += Math.pow(el - avg, 2);
            }
        }

        return (float) Math.sqrt(sdSum / elementsArray.size());
    }
}