package it.bleb.dpi.utils;

import android.content.Context;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class ModelUtil {
    public static ModelUtil instance;

    public final static String FILENAME = "models.json";

    private ModelUtil() {
    }


    /**
     * get model util instance
     *
     * @return
     */
    public static synchronized ModelUtil getInstance() {
        if (instance == null) {
            instance = new ModelUtil();
        }
        return instance;
    }

    /**
     * recupera file json dal nome
     *
     * @param context  activity
     * @param fileName json fileName
     * @return jsonString
     */
    private String getJsonFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }


    /**
     * valorizzazione oggetto modelNormalized da Json
     *
     * @param modelName nome Modello
     * @return ModelNormalize
     */
    public ModelNormalize getConfigObjectFromJson(String modelName, Context context) {
        ModelNormalize modelNormalize = null;
        String jsonFileString = getJsonFromAssets(context, FILENAME);
        Gson gson = new Gson();
        Type modelNormalizeType = new TypeToken<List<ModelNormalize>>() {
        }.getType();
        List<ModelNormalize> listaModelli = gson.fromJson(jsonFileString, modelNormalizeType);
        if (listaModelli != null && listaModelli.size() > 0) {
            for (ModelNormalize model : listaModelli) {
                if (model.getModelName().equals(modelName)) {
                    modelNormalize = model;
                    break;
                }
            }
        }
        return modelNormalize;
    }

    public ImageView getImageViewFromString(String nameImage, List<ImageView> imageViewList) {
        ImageView imageView = null;
        if (imageViewList != null && imageViewList.size() > 0 && nameImage != null) {
            for (ImageView item: imageViewList) {
                if(item != null && item.getContentDescription().equals(nameImage)){
                    imageView = item;
                }
            }
        }
        return imageView;
    }


}
