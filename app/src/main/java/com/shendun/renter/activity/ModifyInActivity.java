package com.shendun.renter.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.github.gzuliyujiang.wheelpicker.DatePicker;
import com.github.gzuliyujiang.wheelpicker.NumberPicker;
import com.github.gzuliyujiang.wheelpicker.TimePicker;
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode;
import com.github.gzuliyujiang.wheelpicker.annotation.TimeMode;
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnNumberPickedListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnNumberSelectedListener;
import com.github.gzuliyujiang.wheelpicker.contract.OnTimePickedListener;
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity;
import com.github.gzuliyujiang.wheelpicker.entity.DatimeEntity;
import com.github.gzuliyujiang.wheelpicker.entity.TimeEntity;
import com.github.gzuliyujiang.wheelpicker.impl.UnitTimeFormatter;
import com.github.gzuliyujiang.wheelview.contract.WheelFormatter;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.request.ForwardScope;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.R;
import com.shendun.renter.app.RenterApplication;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityModifyRenterBinding;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.AddV2ZkRequest;
import com.shendun.renter.repository.bean.GetPlatFromRequest;
import com.shendun.renter.repository.bean.GetPlatFromResponse;
import com.shendun.renter.repository.bean.OrderDetailRequest;
import com.shendun.renter.repository.bean.OrderDetailResponse;
import com.shendun.renter.repository.bean.ResponseBean;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.utils.CheckPermissionUtil;
import com.shendun.renter.utils.CommonUtils;
import com.shendun.renter.utils.DateUtil;
import com.shendun.renter.utils.IDCardUtils;
import com.shendun.renter.utils.ImageUtils;
import com.shendun.renter.utils.PermissionDialogUtil;
import com.shendun.renter.utils.ScreenUtil;
import com.shendun.renter.utils.Size;
import com.shendun.renter.utils.rxutils.ErrorHandleSubscriber;
import com.trello.rxlifecycle4.android.ActivityEvent;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import timber.log.Timber;

/*
 * 修改订单页
 */
public class ModifyInActivity extends BaseActivity<ActivityModifyRenterBinding>
    implements View.OnClickListener {

    private final static String TAG = ModifyInActivity.class.getSimpleName();

    private String mOrderNumber;
    private String mOrderStatus;

    // 拍照回传码
    public final static int CAMERA_REQUEST_CODE_FEEDBACK = 1;
    // 选择照片回传码
    public final static int CHOOSE_PHOTO_REQUEST_CODE_FEEDBACK = 2;

    private String mInData;   //入住日期
    private String mInTime;   //入住时间
    private String mOutData;  //预离日期
    private String mOutTime;  //预离时间
    private int mDay;         //入住天数
    private DatimeEntity mIn;
    private DatimeEntity mOut;
    private DateEntity mBirthdayEntity;//出生日期
    private DateEntity mInDateEntity;  //入住日期
    private DateEntity mOutDateEntity; //预离日期
    private TimeEntity mInTimeEntity;  //入住时间
    private TimeEntity mOutTimeEntity; //预离时间

    // 拍照的照片的存储位置
    private String photoPathFeedBack;
    // 照片所在的Uri地址
    private Uri photoUriFeedBack;
    // 上传照片路径
    private String photoImgPath;

    private OrderDetailResponse mOrderDetailResponse;

    private enum ACTION{
        submit/*提交数据*/, checkIn/*直接入住*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_modify_renter;
    }

    @Override
    protected void initEvent() {
        mOrderNumber = getIntent().getStringExtra(SpConfig.KEY_ORDER_NUMBER);
        mOrderStatus = getIntent().getStringExtra(SpConfig.KEY_ORDER_STATUS);

        mBinding.topbar.getLeftImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBinding.topbar.getCenterTextView().setText(R.string.modify_order);
        mBinding.topbar.getCenterTextView().setTextSize(ScreenUtil.dp2px(mContext, 5));
        mBinding.topbar.getCenterTextView().setTextColor(getResources().getColor(R.color.text_title_color));

        mBinding.llOutDay.setOnClickListener(this);
        mBinding.llOutDate.setOnClickListener(this);
        mBinding.llOutTime.setOnClickListener(this);
        mBinding.btnPortraitScene.setOnClickListener(this);
        mBinding.btOk.setOnClickListener(this);
        mBinding.btCheckIn.setOnClickListener(this);

        loadData();
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.btnPortraitScene){
            takePhotoClick();
        } else if(v == mBinding.llOutDate){
            DatePicker picker = new DatePicker(this);
            picker.setBodyWidth(300);
            picker.getWheelLayout().setDateMode(DateMode.YEAR_MONTH_DAY);
            picker.getWheelLayout().setDateLabel("年", "月", "日");
            picker.getTitleView().setTextSize(ScreenUtil.dp2px(mContext, 6));
            picker.getOkView().setTextColor(getResources().getColor(R.color.theme_blue));
            picker.getWheelLayout().setIndicatorColor(0xFF008AFF);
            picker.getWheelLayout().setTextColor(0xFF999999);
            picker.getWheelLayout().setSelectedTextColor(0xFF008AFF);
            picker.getWheelLayout().getYearLabelView().setTextColor(0xFF008AFF);
            picker.getWheelLayout().getMonthLabelView().setTextColor(0xFF008AFF);
            picker.getWheelLayout().getDayLabelView().setTextColor(0xFF008AFF);
            picker.getWheelLayout().setRange(mInDateEntity, DateEntity.target(2220, 1, 1), mOutDateEntity);
            picker.setOnDatePickedListener(new OnDatePickedListener(){

                @Override
                public void onDatePicked(int year, int month, int day) {
                    mBinding.tvOutDate.setText("预离日期 : " + year + "-" + month + "-" + day);
                    mOutDateEntity = DateEntity.target(year, month, day);

                    String inDate = mInDateEntity.toString();
                    String outDate = mOutDateEntity.toString();
                    if(-1 != DateUtil.compareDateDay(inDate, outDate)){
                        showCenterToast("退房时间要大于入住时间" + mInDateEntity.toString());
                        return;
                    }

                    DateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    long daySpan = TimeUtils.getTimeSpan(outDate, inDate, mFormat, TimeConstants.DAY);
                    Timber.tag(TAG).d("daySpan:" + daySpan);
                    mDay = (int)daySpan;
                    mBinding.tvOutDay.setText("入住天数 : " + mDay);
                }
            });
            picker.show();
        } else if(v == mBinding.llOutTime){
            TimePicker picker = new TimePicker(this);
            picker.getWheelLayout().setTimeMode(TimeMode.HOUR_24_NO_SECOND);
            picker.getTitleView().setTextSize(ScreenUtil.dp2px(mContext, 6));
            picker.getOkView().setTextColor(getResources().getColor(R.color.theme_blue));
            picker.getWheelLayout().setIndicatorColor(0xFF008AFF);
            picker.getWheelLayout().setTextColor(0xFF999999);
            picker.getWheelLayout().setSelectedTextColor(0xFF008AFF);
            picker.getWheelLayout().getHourLabelView().setTextColor(0xFF008AFF);
            picker.getWheelLayout().getMinuteLabelView().setTextColor(0xFF008AFF);
            picker.getWheelLayout().getSecondLabelView().setTextColor(0xFF008AFF);
            picker.getWheelLayout().setTimeFormatter(new UnitTimeFormatter());
            picker.getWheelLayout().setDefaultValue(mOutTimeEntity);
            picker.setOnTimePickedListener(new OnTimePickedListener() {
                @Override
                public void onTimePicked(int hour, int minute, int second) {
                    if(v == mBinding.llOutTime){
                        mBinding.tvOutTime.setText("预离时间 : " + hour + ":" + minute);
                        mOutTime = hour + ":" + minute;
                        mOutTimeEntity = TimeEntity.target(hour, minute, second);
                    }
                }
            });
            picker.show();
        } else if(v == mBinding.llOutDay){
            NumberPicker picker = new NumberPicker(this);
            picker.getTitleView().setTextSize(ScreenUtil.dp2px(mContext, 6));
            picker.getOkView().setTextColor(getResources().getColor(R.color.theme_blue));
            picker.getWheelLayout().setIndicatorColor(0xFF008AFF);
            picker.getWheelLayout().setTextColor(0xFF999999);
            picker.getWheelLayout().setSelectedTextColor(0xFF008AFF);
//            picker.getWheelLayout().setDefaultValue();
            picker.setOnNumberPickedListener(new OnNumberPickedListener() {
                @Override
                public void onNumberPicked(int position, Number item) {
                    mDay = item.intValue();
                    mBinding.tvOutDay.setText("入住天数 : " + mDay);

                    String inDataTime = mInDateEntity.toString() + " " + mInTimeEntity.toString();
                    Date newDateTime = DateUtil.dateOffset(inDataTime, mDay);
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTime(newDateTime);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int second = calendar.get(Calendar.SECOND);
                    mOutDateEntity = DateEntity.target(year, month, day);
                    mOutTimeEntity = TimeEntity.target(hour, minute, second);
                    mBinding.tvOutDate.setText("预离日期 : " + mOutDateEntity.getYear() + "-"
                            + mOutDateEntity.getMonth() + "-" + mOutDateEntity.getDay());
                    mBinding.tvOutTime.setText("预离时间 : " + mOutTimeEntity.getHour() + ":" + mOutTimeEntity.getMinute());
                    LogUtils.d(TAG, "Selected days:" + mDay);
                }
            });
            picker.getWheelLayout().setOnNumberSelectedListener(new OnNumberSelectedListener() {
                @Override
                public void onNumberSelected(int position, Number item) {
                    picker.getTitleView().setText(picker.getWheelView().formatItem(position));
                }
            });
            picker.setFormatter(new WheelFormatter() {
                @Override
                public String formatItem(@NonNull Object item) {
                    return item.toString() + "天";
                }
            });
            picker.setRange(1, 30, 1);
            picker.setDefaultValue(mDay);
            picker.getTitleView().setText("入住天数");
            picker.show();
        } else if (v == mBinding.btOk) {
            submitOrder(ACTION.submit);
        } else if (v == mBinding.btCheckIn) {
            submitOrder(ACTION.checkIn);
        }
    }

    private void submitOrder(ACTION action){
        if(null == mOrderDetailResponse){
            return;
        }

        final String name = mBinding.etName.getText().toString().trim();
        final String idCard = mBinding.etIdCard.getText().toString().trim();
        final String phone = mBinding.etPhone.getText().toString().trim();

        if(TextUtils.isEmpty(name)) {
            showCenterToast("请输入姓名");
            return;
        }

        if(TextUtils.isEmpty(phone) || !RegexUtils.isMobileSimple(phone)) {
            showCenterToast("请输入正确的手机号码");
            return;
        }

        //预订时有身份证号，修改保存也要有
        if(null != mOrderDetailResponse.getZjhm() && !TextUtils.isEmpty(mOrderDetailResponse.getZjhm())){
            if(TextUtils.isEmpty(idCard) || !RegexUtils.isIDCard18Exact(idCard)) {
                showCenterToast("请输入正确的身份证号码");
                return;
            }
        }

        String pic = "";  //人物场景照
        if(action == ACTION.checkIn){
            if(null == photoImgPath || TextUtils.isEmpty(photoImgPath)){
                showCenterToast("请拍摄人像场景照");
                return;
            } else {
                pic = CommonUtils.fileToBase64(photoImgPath);
            }
        }

        String address = "";
        if(null != idCard && !TextUtils.isEmpty(idCard)){
            address = IDCardUtils.getAddressByCardId(mContext, idCard);
        }

        UserInfo userInfo = CacheManager.readFromJson(this, ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            LogUtils.d(TAG, "loadData:null == userInfo");
            showCenterToast("账户信息失效，请重新登陆");
            return;
        }

        String inDate = mInDateEntity.toString();
        String outDate = mOutDateEntity.toString();
        if(-1 != DateUtil.compareDateDay(inDate, outDate)){
            showCenterToast("入住日期不能晚于离开日期");
            return;
        }

        AddV2ZkRequest request = new AddV2ZkRequest();
        request.setPtype(ConstantConfig.TYPE_ORDER);        //数据类型，1表示订单，2表示客人入住
        request.setStype(ConstantConfig.ORDER_UADATE);      //I(大写的i)表示新增，U表示更新
        request.setJlbh(mOrderDetailResponse.getJlbh());    //订单：新增传空字符串;更新传订单号;客人入住：传入订单号;
        request.setLkbh("");    //客人唯一编号.订单：传空字符串；客人入住：新增传空字符串，更新传客人编号；
        request.setXm(name);
        request.setXb(mOrderDetailResponse.getXb());
        request.setMz(mOrderDetailResponse.getMz());
        request.setCsrq(mOrderDetailResponse.getCsrq());
        request.setZjhm(idCard);
        request.setXxdz(address);
        request.setLxdh(phone);
        request.setRzsj(mInDateEntity.toString() + " " + mInTimeEntity.getHour() + ":" + mInTimeEntity.getMinute());
        request.setTfsj(mOutDateEntity.toString() + " " + mOutTimeEntity.getHour() + ":" + mOutTimeEntity.getMinute());
        request.setFhid(mOrderDetailResponse.getFhid());
        request.setZklx("1");
        request.setDwbh(userInfo.getDwbh());
        request.setCybh(userInfo.getCybh());
        request.setDdlypttydm(mOrderDetailResponse.getDdlypttydm());
        request.setDdptbh(mOrderDetailResponse.getDdptbh());
        request.setPic(pic);
        request.setG_is_minor(mOrderDetailResponse.getG_is_minor());
        request.setG_is_baby(mOrderDetailResponse.getG_is_baby());
        request.setG_guardian_relationship(mOrderDetailResponse.getG_guardian_relationship());
        request.setG_guardian_phone(mOrderDetailResponse.getG_guardian_phone());
        request.setG_guardian_name(mOrderDetailResponse.getG_guardian_name());
        request.setG_guardian_idCard(mOrderDetailResponse.getG_guardian_idcard());
        request.setG_guardian_happening(mOrderDetailResponse.getG_guardian_happening());
        request.setG_is_with(mOrderDetailResponse.getG_is_with());
        request.setG_with_relationship(mOrderDetailResponse.getG_with_relationship());
        request.setG_with_happening(mOrderDetailResponse.getG_with_happening());
        request.setG_is_injured(mOrderDetailResponse.getG_is_injured());
        request.setG_suspicious_descrip(mOrderDetailResponse.getG_suspicious_descrip());
        request.setG_is_patrol_erro(mOrderDetailResponse.getG_is_patrol_error());
        request.setG_patrol_happening(mOrderDetailResponse.getG_patrol_happening());
        getRepository(NetService.class).getAddV2Zks(UrlConfig.FD_V2_ADD_ZK, request.getRequestBody())
                .compose(dispatchSchedulers(true))
                .subscribe(new RepositorySubscriber<ResponseBean>() {
                    @Override
                    protected void onResponse(ResponseBean result) {
                        if ("0".equals(result.getCode())) {
                            showCenterToast("成功");
                            finish();
                        } else {
                            showCenterToast(result.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                        showCenterToast(getString(R.string.network_failed));
                    }
                });
    }

    /**
     * 查询订单数据
     */
    private void loadData(){
        if(TextUtils.isEmpty(mOrderNumber))
            return;

        UserInfo userInfo = CacheManager.readFromJson(this, ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            return;
        }

        OrderDetailRequest request = new OrderDetailRequest();
        request.setCybh(userInfo.getCybh());
        request.setDwbh(userInfo.getDwbh());
        request.setJlbh(mOrderNumber);
        getRepository(NetService.class).getOrderDetail(UrlConfig.FD_DDXX, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<ResponseBean<OrderDetailResponse>>() {
                    @Override
                    protected void onResponse(ResponseBean<OrderDetailResponse> result) {
                        if (result.isSuccessful()) {
                            mOrderDetailResponse = result.getData();
                            mBinding.etName.setText(result.getData().getXm());
                            mBinding.etPhone.setText(result.getData().getLxdh());
                            String idCard = result.getData().getZjhm();
                            if(null == idCard || TextUtils.isEmpty(idCard)){
                                mBinding.etIdCard.setVisibility(View.INVISIBLE);
                            } else {
                                mBinding.etIdCard.setVisibility(View.VISIBLE);
                                mBinding.etIdCard.setText(idCard);
                            }
                            String checkIn = result.getData().getRzsj() + ":00";  //入住时间
                            String checkOut = result.getData().getTfsj() + ":00"; //预离时间
                            DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                            long daySpan = TimeUtils.getTimeSpan(checkOut, checkIn, format, TimeConstants.DAY);
                            Timber.tag(TAG).d("daySpan:" + daySpan);
                            mDay = (int)daySpan;
                            mBinding.tvOutDay.setText("入住天数 : " + mDay);

                            Date checkInDate = TimeUtils.string2Date(checkIn, "yyyy-MM-dd HH:mm:ss");
                            String checkInYear = new SimpleDateFormat("yyyy").format(checkInDate);
                            String checkInMonth = new SimpleDateFormat("MM").format(checkInDate);
                            String checkInDay = new SimpleDateFormat("dd").format(checkInDate);
                            String checkInHour = new SimpleDateFormat("HH").format(checkInDate);
                            String checkInMin = new SimpleDateFormat("mm").format(checkInDate);
                            String checkInSec = new SimpleDateFormat("ss").format(checkInDate);
                            mInDateEntity = DateEntity.target(Integer.valueOf(checkInYear), Integer.valueOf(checkInMonth), Integer.valueOf(checkInDay));
                            mInTimeEntity = TimeEntity.target(Integer.valueOf(checkInHour), Integer.valueOf(checkInMin), Integer.valueOf(checkInSec));

                            Date checkOutDate = TimeUtils.string2Date(checkOut, "yyyy-MM-dd HH:mm:ss");
                            String checkOutYear = new SimpleDateFormat("yyyy").format(checkOutDate);
                            String checkOutMonth = new SimpleDateFormat("MM").format(checkOutDate);
                            String checkOutDay = new SimpleDateFormat("dd").format(checkOutDate);
                            String checkOutHour = new SimpleDateFormat("HH").format(checkOutDate);
                            String checkOutMin = new SimpleDateFormat("mm").format(checkOutDate);
                            String checkOutSec = new SimpleDateFormat("ss").format(checkOutDate);
                            mOutDateEntity = DateEntity.target(Integer.valueOf(checkOutYear), Integer.valueOf(checkOutMonth), Integer.valueOf(checkOutDay));
                            mOutTimeEntity = TimeEntity.target(Integer.valueOf(checkOutHour), Integer.valueOf(checkOutMin), Integer.valueOf(checkOutSec));
                            mBinding.tvOutDate.setText("预离日期 : " + checkOutYear + "-" + checkOutMonth + "-" + checkOutDay);
                            mBinding.tvOutTime.setText("预离时间 : " + checkOutHour + ":" + checkOutMin);
                        } else {
                            if(!TextUtils.isEmpty(result.getMessage()))
                                showCenterToast(result.getMessage());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
                    }
                });
    }

    /**
     * 拍照
     */
    private void takePhotoClick() {
        CheckPermissionUtil.checkPermissionObservable(this, Manifest.permission.CAMERA, new ForwardToSettingsCallback() {
            @Override
            public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                nerverAskAgainStorage();
            }
        })
                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(@NotNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            return CheckPermissionUtil.checkPermissionObservable(ModifyInActivity.this,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE, new ForwardToSettingsCallback() {
                                        @Override
                                        public void onForwardToSettings(ForwardScope scope,
                                                                        List<String> deniedList) {
                                            nerverAskAgainStorage();
                                        }
                                    });
                        }
                        return Observable.error(new SecurityException("摄像头权限未授予"));
                    }
                })
                .compose(this.bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(
                        new ErrorHandleSubscriber<Boolean>(RenterApplication.getContext().getRxErrorHandler()) {
                            @Override
                            protected void onResponse(Boolean result) {
                                if (result) {
                                    takePhoto();
                                }else{
                                    //permissionsDenied();
                                }
                            }

                            @Override
                            public void onError(@NotNull Throwable t) {
                                super.onError(t);
                                //permissionsDenied();
                            }
                        });
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoPathFeedBack = ImageUtils.getFeedbackImgPath(mContext);
        photoUriFeedBack = ImageUtils.getUriFromPath(mContext, photoPathFeedBack);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUriFeedBack);
        startActivityForResult(intent, CAMERA_REQUEST_CODE_FEEDBACK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE_FEEDBACK: {
                    try {
                        compressPhoto(photoPathFeedBack);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void compressPhoto(String path) {
        if (ImageUtils.getFileSizeByKB(path) <= ImageUtils.MAX_IMAGE_SIZE) {
            File file = new File(path);
            showImage(file.getAbsolutePath());
            return;
        }
        Tiny.FileCompressOptions options = new Tiny.FileCompressOptions();
        Size size = ImageUtils.getSuitableSize(path);
        options.width = size.getWidth();
        options.height = size.getHeight();
        options.size = ImageUtils.MAX_IMAGE_SIZE - 5;
        options.outfile = ImageUtils.getCompressedFeedbackImgPath(mContext);

        Tiny.getInstance().source(path).asFile().withOptions(options).compress(new FileCallback() {
            @Override
            public void callback(boolean isSuccess, String outfile, Throwable t) {
                File file = new File(outfile);
                showImage(file.getAbsolutePath());
            }
        });
    }

    private void showImage(String path) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getContentResolver()
                    .openInputStream(ImageUtils.getUriFromPath(mContext, path)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (bitmap != null) {
            photoImgPath = path;
            mBinding.ivPortraitScene.setImageBitmap(bitmap);
        }
    }

    private  void nerverAskAgainStorage() {
        PermissionDialogUtil.showDialog(mContext, R.string.permission_tip,
                R.string.never_sak_message, R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //引导用户至设置页手动授权
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }
}