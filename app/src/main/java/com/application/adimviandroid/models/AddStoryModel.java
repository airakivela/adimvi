package com.application.adimviandroid.models;

import java.io.File;

public class AddStoryModel {
    public File imgFile = null;
    public String content = "";

    public AddStoryModel(File imgFile, String content) {
        this.imgFile = imgFile;
        this.content = content;
    }
}
