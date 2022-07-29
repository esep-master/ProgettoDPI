package it.bleb.dpi.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Objects;

import it.bleb.dpi.R;

public class IFTTTModel {
    private String mKey;
    private String mEvent;
    private String mValue1;
    private String mValue2;
    private String mValue3;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IFTTTModel that = (IFTTTModel) o;
        return Objects.equals(mKey, that.mKey) &&
                Objects.equals(mEvent, that.mEvent) &&
                Objects.equals(mValue1, that.mValue1) &&
                Objects.equals(mValue2, that.mValue2) &&
                Objects.equals(mValue3, that.mValue3);
    }

    public IFTTTModel() {
        this("", "", "", "", "");
    }

    public IFTTTModel(String key, String event, String value1, String value2, String value3) {
        mKey = key;
        mEvent = event;
        mValue1 = value1;
        mValue2 = value2;
        mValue3 = value3;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getEvent() {
        return mEvent;
    }

    public void setEvent(String event) {
        mEvent = event;
    }

    public String getValue1() {
        return mValue1;
    }

    public void setValue1(String value1) {
        mValue1 = value1;
    }

    public String getValue2() {
        return mValue2;
    }

    public void setValue2(String value2) {
        mValue2 = value2;
    }

    public String getValue3() {
        return mValue3;
    }

    public void setValue3(String value3) {
        mValue3 = value3;
    }

    public static class SpinAdapter extends ArrayAdapter<IFTTTModel> {
        private ArrayList<IFTTTModel> mPresets;

        public SpinAdapter(Context context, ArrayList<IFTTTModel> presets) {
            super(context, R.layout.spinner_item, presets);
            this.mPresets = presets;

            setDropDownViewResource(R.layout.spinner_dropdown_item);
        }

        @Override
        public int getCount() {
            return mPresets.size();
        }

        @Override
        public IFTTTModel getItem(int position) {
            return mPresets.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView label = (TextView) super.getView(position, convertView, parent);
            IFTTTModel model = mPresets.get(position);

            String labelText;
            if (model != null)
                labelText = model.getEvent() + " " + (model.getKey().isEmpty() ? "" : ("(" + model.getKey() + ")"));
            else
                labelText = "";

            label.setTextColor(Color.BLACK);
            label.setText(labelText);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);
            IFTTTModel model = mPresets.get(position);

            String labelText;
            if (model != null)
                labelText = model.getEvent() + " " + (model.getKey().isEmpty() ? "" : ("(" + model.getKey() + ")"));
            else
                labelText = "";

            label.setTextColor(Color.BLACK);
            label.setText(labelText);
            return label;
        }
    }
}

