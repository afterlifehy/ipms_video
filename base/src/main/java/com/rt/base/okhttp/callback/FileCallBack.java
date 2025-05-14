//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.rt.base.okhttp.callback;

import com.rt.base.okhttp.OkHttpUtils;
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

    public File saveFile(Response response, final int id) throws IOException {
        InputStream is = null;
        byte[] buf = new byte[2048];
        FileOutputStream fos = null;

        try {
            is = response.body().byteStream();
            final long total = response.body().contentLength();
            long sum = 0L;
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
                long finalSum = sum;
                OkHttpUtils.getInstance().getDelivery().execute(new Runnable() {
                    public void run() {
                        FileCallBack.this.inProgress((float) finalSum * 1.0F / (float)total, total, id);
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
