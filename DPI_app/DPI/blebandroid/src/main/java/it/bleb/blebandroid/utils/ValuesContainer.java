/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;

import it.bleb.blebandroid.R;

public class ValuesContainer<T> {
    private ArrayList<EnumHexValue<T>> mValues;

    public ValuesContainer() {
        mValues = new ArrayList<>();
    }

    public ValuesContainer(EnumHexValue<T>... values) {
        this();
        mValues.addAll(Arrays.asList(values));
    }

    public EnumHexValue<T> getByValue(int value) {
        EnumHexValue<T> result = null;
        for(EnumHexValue<T> ehv : mValues) {
            if(ehv.getValue().equals(value)) {
                result = ehv;
                break;
            }
        }
        return result;
    }

    public EnumHexValue<T> getByEnum(T anEnum) {
        EnumHexValue<T> result = null;
        for(EnumHexValue<T> ehv : mValues) {
            if(ehv.getEnum().equals(anEnum)) {
                result = ehv;
                break;
            }
        }
        return result;
    }

    public EnumHexValue<T> getByName(String name) {
        if(name != null)
            name = name.trim().toLowerCase();

        EnumHexValue<T> result = null;
        for(EnumHexValue<T> ehv : mValues) {
            if(ehv.getName() != null && ehv.getName().trim().toLowerCase().equals(name)) {
                result = ehv;
                break;
            }
        }
        return result;
    }

    public EnumHexValue<T> getByPosition(int index) {
        if(index < 0 || index >= mValues.size())
            return null;
        return mValues.get(index);
    }

    public void add(T anEnum, String name, int value) {
        mValues.add(new EnumHexValue<T>(anEnum, name, value));
    }

    public int size() {
        return mValues.size();
    }

    public ArrayList<EnumHexValue<T>> getList() {
        return mValues;
    }

    public static class ValuesContainerArrayAdapter<T> extends ArrayAdapter<EnumHexValue<T>> {
        private ArrayList<EnumHexValue<T>> mList;
        private boolean mAlignCenter;

        public ValuesContainerArrayAdapter(@NonNull Context context, ValuesContainer<T> valuesContainer) {
            this(context, valuesContainer, false);
        }

        public ValuesContainerArrayAdapter(@NonNull Context context, ValuesContainer<T> valuesContainer, boolean alignCenter) {
            super(context, R.layout.spinner_item, valuesContainer.getList());
            this.mList = valuesContainer.getList();
            this.mAlignCenter = alignCenter;

            setDropDownViewResource(R.layout.spinner_dropdown_item);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public EnumHexValue<T> getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView label = (TextView) super.getView(position, convertView, parent);

            if(mAlignCenter)
                label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            label.setText(mList.get(position).getName());
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
            TextView label = (TextView) super.getDropDownView(position, convertView, parent);

            if(mAlignCenter)
                label.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            label.setText(mList.get(position).getName());
            return label;
        }
    }
}
