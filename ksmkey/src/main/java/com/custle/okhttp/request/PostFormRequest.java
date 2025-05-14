//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.custle.okhttp.request;

import com.custle.okhttp.OkHttpUtils;
import com.custle.okhttp.builder.PostFormBuilder;
import com.custle.okhttp.callback.Callback;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

public class PostFormRequest extends OkHttpRequest {
    private List<PostFormBuilder.FileInput> files;

    public PostFormRequest(String url, Object tag, Map<String, String> params, Map<String, String> headers, List<PostFormBuilder.FileInput> files, int id) {
        super(url, tag, params, headers, id);
        this.files = files;
    }

    protected RequestBody buildRequestBody() {
        if (this.files != null && !this.files.isEmpty()) {
            MultipartBody.Builder builder = (new MultipartBody.Builder()).setType(MultipartBody.FORM);
            this.addParams(builder);

            for(int i = 0; i < this.files.size(); ++i) {
                PostFormBuilder.FileInput fileInput = (PostFormBuilder.FileInput)this.files.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(this.guessMimeType(fileInput.filename)), fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
            }

            return builder.build();
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            this.addParams(builder);
            FormBody formBody = builder.build();
            return formBody;
        }
    }

    protected RequestBody wrapRequestBody(RequestBody requestBody, final Callback callback) {
        if (callback == null) {
            return requestBody;
        } else {
            CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
                public void onRequestProgress(final long bytesWritten, final long contentLength) {
                    OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                        public void run() {
                            callback.inProgress((float)bytesWritten * 1.0F / (float)contentLength, contentLength, PostFormRequest.this.id);
                        }
                    });
                }
            });
            return countingRequestBody;
        }
    }

    protected Request buildRequest(RequestBody requestBody) {
        return this.builder.post(requestBody).build();
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;

        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
        }

        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }

        return contentTypeFor;
    }

    private void addParams(MultipartBody.Builder builder) {
        if (this.params != null && !this.params.isEmpty()) {
            Iterator var2 = this.params.keySet().iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                builder.addPart(Headers.of(new String[]{"Content-Disposition", "form-data; name=\"" + key + "\""}), RequestBody.create((MediaType)null, (String)this.params.get(key)));
            }
        }

    }

    private void addParams(FormBody.Builder builder) {
        if (this.params != null) {
            Iterator var2 = this.params.keySet().iterator();

            while(var2.hasNext()) {
                String key = (String)var2.next();
                builder.add(key, (String)this.params.get(key));
            }
        }

    }
}
