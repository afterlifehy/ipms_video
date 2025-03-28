//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.peakinfo.base.ca.com.custle.okhttp.callback;

import com.peakinfo.base.ca.com.custle.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.Response;

public abstract class FileCallBack extends Callback<File> {
    private String destFileDir;
    private String destFileName;

    public FileCallBack(String destFileDir, String destFileName) {
        this.destFileDir = destFileDir;
        this.destFileName = destFileName;
    }

    public File parseNetworkResponse(Response response, int id) throws Exception {
        return this.saveFile(response, id);
    }

    private long sum = 0L;
    public File saveFile(Response response, final int id) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;

        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            sum = 0L;
            File dir = new File(this.destFileDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(dir, this.destFileName);
            fos = new FileOutputStream(file);

            int len;
            while((len = is.read(buf)) != -1) {
                sum += (long)len;
                fos.write(buf, 0, len);
                OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                    public void run() {
                        FileCallBack.this.inProgress((float) sum / (float)total, total, id);
                    }
                });
            }

            fos.flush();
            File var13 = file;
            return var13;
        } finally {
            try {
                response.body().close();
                if (is != null) {
                    is.close();
                }
            } catch (IOException var23) {
            }

            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException var22) {
            }

        }
    }
}
