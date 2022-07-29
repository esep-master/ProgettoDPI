/*
 * Created by Bleb Technology srl.
 * Copyright (c) 2019. All rights reserved.
 * https://bleb.it
 *
 * Last modified 01/04/2019 17:46
 */

package it.bleb.blebandroid.utils;

public class EnumHexValue<T> {
    private T mEnum;
    private String mName;
    private Integer mValue;

    public EnumHexValue(T anEnum, String name, Integer value) {
        mEnum = anEnum;
        mName = name;
        mValue = value;
    }

    public T getEnum() {
        return mEnum;
    }

    public void setEnum(T anEnum) {
        mEnum = anEnum;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Integer getValue() {
        return mValue;
    }

    public void setValue(Integer value) {
        mValue = value;
    }
}
