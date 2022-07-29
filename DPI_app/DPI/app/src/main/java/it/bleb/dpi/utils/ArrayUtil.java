package it.bleb.dpi.utils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArrayUtil {

    public static ArrayUtil instance;

    private ArrayUtil() {
    }

    /**
     * get array util instance
     *
     * @return
     */
    public static synchronized ArrayUtil getInstance() {
        if (instance == null) {
            instance = new ArrayUtil();
        }
        return instance;
    }

    /**
     * build specific list for parameters
     *
     * @param beaconDataArray
     * @return specific list for parameters
     */
    public Map<String, List<Float>> getArrayValues(List<BeaconData> beaconDataArray) {
        Map<String, List<Float>> value2array = new HashMap<>();
        if (beaconDataArray != null) {
            List<Float> xArray = new ArrayList<>();
            List<Float> yArray = new ArrayList<>();
            List<Float> zArray = new ArrayList<>();
            List<Float> cArray = new ArrayList<>();

            for (BeaconData beaconData : beaconDataArray) {
                xArray.add(beaconData.getX());
                yArray.add(beaconData.getY());
                zArray.add(beaconData.getZ());
                cArray.add(beaconData.getC());
            }

            value2array.put("X", xArray);
            value2array.put("Y", yArray);
            value2array.put("Z", zArray);
            value2array.put("C", cArray);
        }
        return value2array;
    }

    /**
     *
     * @param valueArray list di elementi
     * @return array float
     */
    public float[] getArrayByList(List<Float> valueArray){
        float[] floatArray = new float[valueArray.size()];
        for (int i = 0; i < valueArray.size(); i++) {
            floatArray[i]= valueArray.get(i);
        }
        return floatArray;
    }

    /**
     * @param values
     * @param minValues
     * @param maxValues
     * @return
     */
    public List<Float> normalize(List<Float> values, List<Float> minValues, List<Float> maxValues) {
        List<Float> listNormalized = new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
            Float value = values.get(i);
            Float min = minValues.get(i);
            Float max = maxValues.get(i);
            Float valueNormalized = (value - min) / (max - min);
            listNormalized.add(valueNormalized);
        }
        return listNormalized;
    }

    /**
     * Converts an Integer ArrayList to int[] array
     * @param l The Integer ArrayList
     * @return int[] The primitive array
     */
    public static int[] convertToPrimitiveInt(ArrayList<Integer> l) {
        int[] ret = new int[l.size()];
        for (int i=0; i<ret.length; i++) {
            ret[i] = l.get(i);
        }
        return ret;
    }

    /**
     * Converts an Double ArrayList to double[] array
     * @param l The Double ArrayList
     * @return double[] The primitive array
     */
    public static double[] convertToPrimitiveDouble(ArrayList<Double> l) {
        double[] ret = new double[l.size()];
        for (int i=0; i<ret.length; i++) {
            ret[i] = l.get(i);
        }
        return ret;
    }
}