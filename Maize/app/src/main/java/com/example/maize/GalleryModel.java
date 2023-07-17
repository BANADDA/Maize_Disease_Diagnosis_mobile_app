package com.example.maize;

public class GalleryModel {
    private String result, ImagePath;

    public GalleryModel() {
    }

    public GalleryModel(String result, String imagePath) {
        this.result = result;
        ImagePath = imagePath;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String imagePath) {
        ImagePath = imagePath;
    }
}
