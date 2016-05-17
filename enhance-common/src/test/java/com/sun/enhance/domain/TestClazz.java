package com.sun.enhance.domain;

import com.sun.enhance.annotation.FieldEnhance;
import com.sun.enhance.annotation.MethodEnhance;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by zksun on 5/11/16.
 */
public class TestClazz implements Serializable {
    private Integer intValue;

    private Boolean booleanValue;
    @FieldEnhance(value = "world", attr = "attr")
    private String stringValue;

    private Long longValue;

    private Double doubleValue;

    private Character charValue;

    private Short shortValue;


    public Integer getIntValue() {
        return intValue;
    }

    public void setIntValue(Integer intValue) {
        this.intValue = intValue;
    }

    public Boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(Boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public Character getCharValue() {
        return charValue;
    }

    public void setCharValue(Character charValue) {
        this.charValue = charValue;
    }

    public Short getShortValue() {
        return shortValue;
    }

    public void setShortValue(Short shortValue) {
        this.shortValue = shortValue;
    }

    @MethodEnhance(methodName = "testMethod")
    public void testMethod(String arg) {

    }

    @MethodEnhance(methodName = "testMethod0")
    public void testMethod0(String arg) throws IOException {

    }
}
