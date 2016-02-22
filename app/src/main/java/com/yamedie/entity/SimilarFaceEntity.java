package com.yamedie.entity;

/**
 * Created by Li Dachang on 16/1/27.
 * ..-..---.-.--..---.-...-..-....-.
 */
public class SimilarFaceEntity {
    private String faceId;
    private double similarity;
    private String name;
    private String tag;

    public String getFaceId() {
        return faceId;
    }

    public void setFaceId(String faceId) {
        this.faceId = faceId;
    }

    public double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(double similarity) {
        this.similarity = similarity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
