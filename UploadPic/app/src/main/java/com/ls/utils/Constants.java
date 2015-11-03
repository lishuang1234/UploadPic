package com.ls.utils;

/**
 * Created by ls on 15-11-3.
 */
public class Constants {

    public static final int GET_CAMERA_PHOTO_REQUEST_CODE = 1;
    public static final int GET_ATTACH_PHOTO_REQUEST_CODE = 2;

    public static final String BASE = "113.251.219.5";
    public static final String IMID = "113.251.219.5";

    public static final String UPLOAD = "http://" + BASE + ":8080/PictureUpload/Uploade";
    public static final String HANDLE = "http://" + BASE + ":8080/PictureUpload/Uploade?type=0";
    public static final String GET_RESULT = "http://" + BASE + ":8080/PictureUpload/Uploade?type=1"+ "&from=";


}
