package com.lewis.myoa;

/**
 * Created by lewis on 16/6/24.
 */
public class UpYun {
    private String url;

    public String getUrl() {
        return "http://ainana.b0.upaiyun.com" + url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    // {"image-type":"PNG","image-frames":1,"image-height":340,"sign":"845768d3d0d3c11277e7a4e754e36420","code":200,"file_size":31062,"image-width":500,"url":"\/8613170345845381466740647774.png","time":1466740647,"message":"ok","mimetype":"image\/png"}
}
