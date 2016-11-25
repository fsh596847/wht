package com.xiaowei.android.wht.ui.doctorzone.net;

import java.util.Map;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import rx.Observable;

/**
 * Created by AIS on 2016/7/15 0015.
 */
public interface ApiService {
  @Multipart @POST("ImageServer/pub/fileupload?moduleName=docotornotice")
  Observable<String> uploadImage(
      @PartMap Map<String, RequestBody> params);
}
