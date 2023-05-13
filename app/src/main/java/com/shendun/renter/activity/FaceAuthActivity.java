package com.shendun.renter.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.FaceDetector;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.EncodeUtils;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.R;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityFaceAuthBinding;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.FaceAuthRequest;
import com.shendun.renter.repository.bean.FaceAuthResponse;
import com.shendun.renter.utils.PicUtil;
import com.shendun.renter.utils.ScreenUtil;
import com.shendun.renter.utils.TFImageUtils;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/*
 * 人脸认证页
 */
public class FaceAuthActivity extends BaseActivity<ActivityFaceAuthBinding>
    implements View.OnClickListener {
    private final String TAG = FaceAuthActivity.class.getSimpleName();

    private Mat mRgba;
    private Mat mGray,mRgbaF,mRgbaT;
    private Mat Matlin;
    private Mat gMatlin;
    private Bitmap bitmap = null;
    private int mAbsoluteFaceSize = 0;
    private CascadeClassifier cascadeClassifier;
    private float mRelativeFaceSize = 0.2f;

    private Button btn_camera,btn_cap;
    private TextView tv_hint;
    private boolean isFaceCap=true;
    private int findFaceNum;

    private Button btn_switch;

    private boolean isFront = false;

    private int width = 640;
    private int height = 480;

    private String mPhone = "";
    private String mSubPhone = "";
    private String mOrder = "";
    private String mName = "";
    private String mId = "";
    private String mClientStyle = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_face_auth;
    }

    @Override
    protected void initEvent() {
        mBinding.topbar.getLeftImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBinding.topbar.getCenterTextView().setText(R.string.user_auth);
        mBinding.topbar.getCenterTextView().setTextSize(ScreenUtil.dp2px(mContext, 6));
        mBinding.topbar.getCenterTextView().setTextColor(getResources().getColor(R.color.text_title_color));
        mBinding.tvStartAuth.setOnClickListener(this);

        mContext = this;

        Bundle bundle = getIntent().getExtras();
        if(null != bundle){
            mPhone = bundle.getString(SpConfig.KEY_PHONE_NUMBER);
            mSubPhone = bundle.getString(SpConfig.KEY_SUB_PHONE_NUMBER);
            mOrder = bundle.getString(SpConfig.KEY_ORDER_NUMBER);
            mName = bundle.getString(SpConfig.KEY_NAME);
            mId = bundle.getString(SpConfig.KEY_ID_CARD);
            mClientStyle = bundle.getString(SpConfig.KEY_CLIENT_STYLE);
        }

        initOpenCV();
    }

    @Override
    public void onClick(View v) {
        if (v == mBinding.tvStartAuth) {
            String title = mBinding.tvStartAuth.getText().toString();
            if(getString(R.string.start_auth).equals(title)){
                //切换前置
                mBinding.cameraView.disableView();
                mBinding.cameraView.setCameraIndex(1);
                mBinding.cameraView.enableView();
                isFront=true;
            } else {
                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                if(!TextUtils.isEmpty(mOrder)){
                    intent.putExtra(SpConfig.KEY_ORDER_NUMBER, mOrder);
                }
                startActivity(intent);
            }
        }
    }

    private void startFaceAuth(Bitmap bitmap) {
        if (null != bitmap) {
            String picture = EncodeUtils.base64Encode2String(TFImageUtils.Bitmap2Bytes(bitmap));
            FaceAuthRequest request = new FaceAuthRequest();
            request.setTel(mPhone);
            request.setTxtel(mSubPhone);
            request.setJlbh(mOrder);
            request.setXm(mName);
            request.setZjhm(mId);
            request.setPic(picture);
            request.setZklx(mClientStyle);
            getRepository(NetService.class).faceAuth(UrlConfig.ZK_RZHY, request.getRequestBody())
                    .compose(dispatchSchedulers(false))
                    .subscribe(new RepositorySubscriber<FaceAuthResponse>() {
                        @Override
                        protected void onResponse(FaceAuthResponse result) {
                            if ("0".equals(result.getCode())) {
                                Intent intent = new Intent(mContext, OrderDetailActivity.class);
                                if(!TextUtils.isEmpty(mOrder)){
                                    intent.putExtra(SpConfig.KEY_ORDER_NUMBER, mOrder);
                                }
                                startActivity(intent);
                                finish();
                                Timber.tag(TAG).d("人脸认证成功");
                            } else {
                                Observable.timer(1000, TimeUnit.MILLISECONDS)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Long>() {
                                            @Override
                                            public void onSubscribe(Disposable d) { }

                                            @Override
                                            public void onNext(Long aLong) {
                                                isFaceCap = true;
                                                showMessage(result.getMessage());
                                                Timber.tag(TAG).d("人脸认证失败");
                                            }

                                            @Override
                                            public void onError(Throwable e) { }

                                            @Override
                                            public void onComplete() { }
                                        });
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable t) {
                            super.onError(t);
                            showMessage(getString(R.string.network_failed));
                        }
                    });
        }
    }

    private void switchFront(boolean front){
        Observable.timer(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) { }

                    @Override
                    public void onNext(Long aLong) {
                        try{
                            if(front){
                                //切换后置
                                Log.e("test","切换后置了");

                                mBinding.cameraView.disableView();
                                mBinding.cameraView.setCameraIndex(0);
                                mBinding.cameraView.enableView();
                                isFront=false;

                            }else{
                                Log.e("test","切换前置了");
                                //切换前置

                                mBinding.cameraView.disableView();
                                mBinding.cameraView.setCameraIndex(1);
                                mBinding.cameraView.enableView();
                                isFront=true;
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) { }

                    @Override
                    public void onComplete() { }
                });
    }

    private void initOpenCV() {

        mBinding.cameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {

            @Override
            public void onCameraViewStarted(int width, int height) {
                mGray = new Mat();
                mRgba = new Mat();

                mRgbaF = new Mat(height, width, CvType.CV_8UC4);
                mRgbaT = new Mat(width, width, CvType.CV_8UC4);
            }

            @Override
            public void onCameraViewStopped() {
                mGray.release();
                mRgba.release();
            }

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                mRgba = inputFrame.rgba();
                mGray = inputFrame.gray();

//                switch (openCVCameraView.getDisplay().getRotation()) {
//                    case Surface.ROTATION_0: // Vertical portrait
//                        Core.transpose(mRgba, mRgbaT);
//                        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
//                        Core.flip(mRgbaF, mRgba, 1);
//                        break;
//                    case Surface.ROTATION_90: // 90° anti-clockwise
//                        break;
//                    case Surface.ROTATION_180: // Vertical anti-portrait
//                        Core.transpose(mRgba, mRgbaT);
//                        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
//                        Core.flip(mRgbaF, mRgba, 0);
//                        break;
//                    case Surface.ROTATION_270: // 90° clockwise
//                        Imgproc.resize(mRgba, mRgbaF, mRgbaF.size(), 0,0, 0);
//                        Core.flip(mRgbaF, mRgba, -1);
//                        break;
//                    default:
//                }

                /*
                 * 函数很简单，一个输入Mat，一个输出Mat，
                 * flipCode有三种情况，0为绕X轴翻转，大于的0代表绕Y轴翻转，-1代表即桡X轴也绕Y轴翻转。
                 */

//                Core.transpose(mRgba, mRgbaT);
//                Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
//                Core.flip(mRgbaF, mRgba, 1);


//                switch (openCVCameraView.getDisplay().getRotation()) {
//                    case Surface.ROTATION_0: // Vertical portrait
//                        Log.e("test","0");
//                        break;
//                    case Surface.ROTATION_90: // 90° anti-clockwise
//                        Log.e("test","90");
//                        break;
//                    case Surface.ROTATION_180: // Vertical anti-portrait
//                        Log.e("test","180");
//
//                        break;
//                    case Surface.ROTATION_270: // 90° clockwise
//                        Log.e("test","270");
//
//
//                        break;
//                    default:
//                }

                if(!isFront){
                    Mat rotateMat =  new Mat();
                    rotateMat = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2, mRgba.rows() / 2), 270, 1);
                    Imgproc.warpAffine(mRgba, mRgba, rotateMat, mRgbaF.size());
                    Imgproc.warpAffine(mGray, mGray, rotateMat, mRgbaF.size());
                }else{
                    Mat rotateMat =  new Mat();
                    rotateMat = Imgproc.getRotationMatrix2D(new Point(mRgba.cols() / 2, mRgba.rows() / 2), 90, 1);
                    Imgproc.warpAffine(mRgba, mRgba, rotateMat, mRgbaF.size());
                    Imgproc.warpAffine(mGray, mGray, rotateMat, mRgbaF.size());
                }

                if(isFront){
                    Core.flip(mGray, mGray, 1);
                    Core.flip(mRgba, mRgba, 1);
                }


                // Core.transpose(mRgba,mGray); //转置函数，可以水平的图像变为垂直
//                Imgproc.resize(this.mRgbaT,this.mRgbaF, this.mRgbaF.size(), 0.0D, 0.0D, 0); //将转置后的图像缩放为mRgbaF的大小
//                Core.flip(this.mRgbaF, this.mRgba,1);

                if (mAbsoluteFaceSize == 0) {
                    int height = mGray.rows();
                    if (Math.round(height * mRelativeFaceSize) > 0) {
                        mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
                    }
                }

                //检测并显示
                MatOfRect faces = new MatOfRect();
                if (cascadeClassifier != null) {
                    cascadeClassifier.detectMultiScale(mGray, faces, 1.1, 3, 2,
                            new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());
                }
                Rect[] facesArray = faces.toArray();

                if (facesArray.length == 1) {
//                    if (true) {
                    PicUtil.resizeFace(facesArray[0], mRgba.width(), mRgba.height());
                    // 只检测一个人脸
                    Imgproc.rectangle(mRgba, facesArray[0].tl(), facesArray[0].br(), new Scalar(0, 255, 0, 255), 3);
                    Mat tmMat = new Mat(mRgba, facesArray[0]);
                    bitmap = Bitmap.createBitmap(tmMat.cols(), tmMat.rows(), Bitmap.Config.RGB_565);



                    Utils.matToBitmap(tmMat, bitmap);
                    //做一个镜像翻转
                    Matrix m = new Matrix();
                    //m.postScale(-.5f, .5f);

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);

                    FaceDetector faceDet = new FaceDetector(bitmap.getWidth(), bitmap.getHeight(), 1);

                    FaceDetector.Face[] faceArray = new FaceDetector.Face[1];

                    findFaceNum = faceDet.findFaces(bitmap, faceArray);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Log.e("test","findFaceNum:"+findFaceNum+"");
                            if(findFaceNum==1 && isFaceCap){
                                isFaceCap=false;
                                mBinding.ivBitmap.setImageBitmap(bitmap);
                                startFaceAuth(bitmap);
                            }
                        }
                    });

                }
                return mRgba;
            }
        });
        if (OpenCVLoader.initDebug()) {
            loaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        } else {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, mContext, loaderCallback);
        }
    }

    private BaseLoaderCallback loaderCallback = new BaseLoaderCallback(mContext) {
        @Override
        public void onManagerConnected(int status) {
            super.onManagerConnected(status);
            if (status == LoaderCallbackInterface.SUCCESS) {
                try {
                    //加载人脸检测XML
                    InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
                    File cascadeDir = mContext.getDir("cascade", Context.MODE_PRIVATE);
                    File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt.xml");
                    FileOutputStream fos = new FileOutputStream(cascadeFile);

                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    fos.close();
                    cascadeClassifier = new CascadeClassifier(cascadeFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mBinding.cameraView.enableView();

//                switchFront(false);
            } else {
                super.onManagerConnected(status);
            }
        }
    };
}