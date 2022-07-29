package it.bleb.dpi.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.room.Room;

import it.bleb.dpi.DpiAppApplication;
import it.bleb.dpi.R;
import it.bleb.dpi.database.DpiDatabase;
import it.bleb.dpi.database.entity.Task;
import it.bleb.dpi.utils.Constants;
import it.bleb.dpi.utils.DpiData;
import it.bleb.dpi.utils.ModelNormalize;
import it.bleb.dpi.utils.ModelUtil;

public class HomeFragment extends Fragment {

    private ImageView imgHelmet,
            imgLeftGlove, imgRightGlove,
            imgRightShoe, imgLeftShoe,
            imgJacket, imgTrousers,
            imgHarness, imgEarMuffs; //Modifiche cuffie e imbracatura

    private ImageView logoHarness, logoEarMuffs;

    private List<ImageView> imagesView = null;

    private HashMap<String, DpiData> beacon2dpi, beacon2dpiUpdated;

    private SetDetailsDpi setDetailsDpi;

    private static final String TAG = "HomeFragment";
    private ProgressDialog progressDialog;
    //DB
    private boolean taskIsStarted;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        setDetailsDpi = (SetDetailsDpi) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Immagini "marker" default drawable/presence_invisible
        imgHelmet = view.findViewById(R.id.img_helmet);
        imgLeftGlove = view.findViewById(R.id.img_leftGlove);
        imgRightGlove = view.findViewById(R.id.img_rightGlove);
        imgRightShoe = view.findViewById(R.id.img_rightShoe);
        imgLeftShoe = view.findViewById(R.id.img_leftShoe);
        imgJacket = view.findViewById(R.id.img_jacket);
        imgTrousers = view.findViewById(R.id.img_trousers);
        //Modifiche cuffie e imbracatura
        imgHarness = view.findViewById(R.id.img_harness);
        imgEarMuffs = view.findViewById(R.id.img_earmuffs);

        //logo cuffie e imbracatura
        logoHarness = view.findViewById(R.id.silouhette_harness);
        logoEarMuffs = view.findViewById(R.id.silouhette_earmuffs);

        if (DpiAppApplication.EASY_MODE) {
            logoHarness.setVisibility(View.GONE);
            logoEarMuffs.setVisibility(View.GONE);
        }

        setImagesView();
        setImageViewGone();


        if (getArguments() != null) {
            ArrayList<String> dpiResponse = (ArrayList<String>) getArguments().getSerializable("dpiResponse");
            beacon2dpi = (HashMap) getArguments().getSerializable("beacon2dpi");
            boolean fromTest = getArguments().getBoolean("fromTest");
            taskIsStarted = getArguments().getBoolean("isStarting");
            try {
                if (fromTest) {
                    setDpiKitOld(dpiResponse);
                } else {
                    setDpiKit();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (taskIsStarted) {
            taskIsStarted = false;
            //showProgressDialog();
        }
    }

    public List<ImageView> getImagesView() {
        return imagesView;
    }

    private void setImagesView() {
        imagesView = new ArrayList<>();
        imagesView.add(imgHelmet);
        imagesView.add(imgLeftGlove);
        imagesView.add(imgRightGlove);
        imagesView.add(imgRightShoe);
        imagesView.add(imgLeftShoe);
        imagesView.add(imgJacket);
        imagesView.add(imgTrousers);
        //Aggiungo cuffie e Imbracatura solo se serve
        if (!DpiAppApplication.EASY_MODE) {
            imagesView.add(imgHarness);
            imagesView.add(imgEarMuffs);
        }
    }

    /**
     * for default set Image view GONE
     */
    private void setImageViewGone() {
        imgHelmet.setVisibility(ImageView.GONE);
        imgLeftGlove.setVisibility(ImageView.GONE);
        imgRightGlove.setVisibility(ImageView.GONE);
        imgRightShoe.setVisibility(ImageView.GONE);
        imgLeftShoe.setVisibility(ImageView.GONE);
        imgJacket.setVisibility(ImageView.GONE);
        imgTrousers.setVisibility(ImageView.GONE);
        imgHarness.setVisibility(ImageView.GONE);
        imgEarMuffs.setVisibility(ImageView.GONE);
    }

    /**
     * get hashMap key and set and visible ImageView
     */
    private void setDpiKit() {
        if (beacon2dpi != null) {
            for (Map.Entry<String, DpiData> dpi : beacon2dpi.entrySet()) {
                switch (dpi.getValue().getModelName()) {
                    case Constants.MODEL_JACKET:
                        dpi.getValue().setImageView("imgJacket");
                        imgJacket.setVisibility(ImageView.VISIBLE);
                        break;
                    case Constants.MODEL_HELMET:
                        dpi.getValue().setImageView("imgHelmet");
                        imgHelmet.setVisibility(ImageView.VISIBLE);
                        break;
                    case Constants.MODEL_LEFT_GLOVE:
                        dpi.getValue().setImageView("imgLeftGlove");
                        imgLeftGlove.setVisibility(ImageView.VISIBLE);
                        break;
                    case Constants.MODEL_RIGHT_GLOVE:
                        dpi.getValue().setImageView("imgRightGlove");
                        imgRightGlove.setVisibility(ImageView.VISIBLE);
                        break;
                    case Constants.MODEL_LEFT_SHOE:
                        dpi.getValue().setImageView("imgLeftShoe");
                        imgLeftShoe.setVisibility(ImageView.VISIBLE);
                        break;
                    case Constants.MODEL_RIGHT_SHOE:
                        dpi.getValue().setImageView("imgRightShoe");
                        imgRightShoe.setVisibility(ImageView.VISIBLE);
                        break;
                    case Constants.MODEL_TROUSERS:
                        dpi.getValue().setImageView("imgTrousers");
                        imgTrousers.setVisibility(ImageView.VISIBLE);
                        break;
                    //Modifiche cuffie e imbracatura
                    case Constants.MODEL_HARNESS:
                        dpi.getValue().setImageView("imgHarness");
                        imgHarness.setVisibility(!DpiAppApplication.EASY_MODE ? ImageView.VISIBLE : ImageView.GONE);
                        break;
                    case Constants.MODEL_EARMUFFS:
                        dpi.getValue().setImageView("imgEarMuffs");
                        imgEarMuffs.setVisibility(!DpiAppApplication.EASY_MODE ? ImageView.VISIBLE : ImageView.GONE);
                        break;
                }
            }

        }
    }


    /**
     * Set silouhette sensors according to DPI response
     */
    public void setDpiKitOld(ArrayList<String> dpiResponse) throws Exception {
        if (dpiResponse != null) {
            beacon2dpi = new HashMap<>();
            beacon2dpiUpdated = new HashMap<>();

            //beacon2dpi.put("F8:BE:95:CE:43:A2", new DpiData(Constants.MODEL_HELMET, imgHelmet)); //BEACON TEST
            beacon2dpi.put("F8:81:5E:05:44:7E", new DpiData(Constants.MODEL_HELMET, "imgHelmet"));

            //beacon2dpi.put("F2:FC:A1:8B:EB:C1", new DpiData(Constants.MODEL_HELMET, imgHelmet));
            beacon2dpi.put("CF:62:B8:0F:D2:A6", new DpiData(Constants.MODEL_TROUSERS, "imgTrousers"));
            //beacon2dpi.put("D4:39:63:D8:45:32", new DpiData(Constants.MODEL_JACKET, imgJacket));
            beacon2dpi.put("DF:6B:87:07:39:83", new DpiData(Constants.MODEL_JACKET, "imgJacket"));

            //beacon2dpi.put("FE:FA:44:ED:E2:23", new DpiData(Constants.MODEL_HELMET, imgHelmet));
            beacon2dpi.put("E0:4C:75:B3:F0:D0", new DpiData(Constants.MODEL_LEFT_SHOE, "imgLeftShoe"));
            beacon2dpi.put("D9:78:9D:FB:D0:20", new DpiData(Constants.MODEL_RIGHT_SHOE, "imgRightShoe"));
            beacon2dpi.put("D6:49:01:E3:2A:6A", new DpiData(Constants.MODEL_LEFT_GLOVE, "imgLeftGlove"));
            beacon2dpi.put("F2:FC:A1:8B:EB:C1", new DpiData(Constants.MODEL_RIGHT_GLOVE, "imgRightGlove"));

            //No altering if HashMap and Array have the same length
            if (beacon2dpi.size() != dpiResponse.size()) {
                for (Map.Entry<String, DpiData> dpi : beacon2dpi.entrySet()) {
                    if (dpiResponse.contains(dpi.getKey())) {
                        ImageView imageView = ModelUtil.getInstance().getImageViewFromString(beacon2dpi.get(dpi.getKey()).getImageView(), imagesView);
                        if (imageView != null) {
                            imageView.setVisibility(View.VISIBLE);
                            beacon2dpiUpdated.put(dpi.getKey(), dpi.getValue());
                        }
                    }
                }
            } else {
                beacon2dpiUpdated = beacon2dpi;
            }
            //set name and battery level
            setDetailsDpi.setInfoDPI(beacon2dpiUpdated);
        } else {
            throw new Exception();
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setProgressStyle(R.attr.progressBarStyle);
            progressDialog.setMessage(getString(R.string.txt_attendere));
            progressDialog.setMax(4);
            show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog = null;
        }
    }

    public interface SetDetailsDpi {
        void setInfoDPI(HashMap<String, DpiData> dpiListUpdated);
    }

    private void show() {
        ValueAnimator animator = ValueAnimator.ofInt(0, progressDialog.getMax());
        animator.setDuration(3000);
        progressDialog.show();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                progressDialog.setProgress((Integer) animation.getAnimatedValue());

            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                hideProgressDialog();
                // start your activity here
            }
        });
        animator.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideProgressDialog();
    }
}