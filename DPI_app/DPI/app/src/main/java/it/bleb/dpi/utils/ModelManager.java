package it.bleb.dpi.utils;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

public class ModelManager {
    private Interpreter tflite_gan;

    private Context context;

    private final int BYTE_SIZE_OF_FLOAT = Float.SIZE / 8;

    /**
     * istanzia interpreter
     * @param context context
     * @param modelName nome modello
     */
    public ModelManager(Context context, String modelName) {
        this.context = context;
        try {
            tflite_gan = new Interpreter(loadModelFile(context, modelName + ".tflite"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * load model file
     * @param c context
     * @param MODEL_FILE nome modello
     * @return modello in formato buffer
     * @throws IOException
     */
    private MappedByteBuffer loadModelFile(Context c, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = c.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    /**
     * run modello
     * @param normalizedArray array dati normalizzati
     * @return output
     */
    public float runModel(List<Float> normalizedArray) {
        float[] input = ArrayUtil.getInstance().getArrayByList(normalizedArray);

        ByteBuffer input_data = ByteBuffer.allocate(input.length * BYTE_SIZE_OF_FLOAT);
        input_data.order(ByteOrder.nativeOrder());

        ByteBuffer gan_output = ByteBuffer.allocate(BYTE_SIZE_OF_FLOAT);
        gan_output.order(ByteOrder.nativeOrder());

        input_data.clear();
        gan_output.clear();
        input_data.rewind();

        for (int i = 0; i < input.length; i++) {
            input_data.putFloat(input[i]);
        }

        tflite_gan.run(input, gan_output);
        return gan_output.getFloat(0);
    }
}
