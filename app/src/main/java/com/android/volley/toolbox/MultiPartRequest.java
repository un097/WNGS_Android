package com.android.volley.toolbox;

import java.io.File;
import java.util.Map;

/**
 * Created by changliang on 14/11/19.
 */
public interface MultiPartRequest {
    public void addFileUpload(String param,File file);

    public void addStringUpload(String param,String content);

    public Map<String,File> getFileUploads();

    public Map<String,String> getStringUploads();
}
