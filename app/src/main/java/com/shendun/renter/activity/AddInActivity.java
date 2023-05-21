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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

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
import com.github.gzuliyujiang.wheelpicker.widget.OptionWheelLayout;
import com.github.gzuliyujiang.wheelview.contract.WheelFormatter;
import com.permissionx.guolindev.callback.ForwardToSettingsCallback;
import com.permissionx.guolindev.request.ForwardScope;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.R;
import com.shendun.renter.app.RenterApplication;
import com.shendun.renter.bean.OrderType;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityAddOccupantBinding;
import com.shendun.renter.dialog.NormalDialog;
import com.shendun.renter.dialog.animation.FadeEnter.FadeEnter;
import com.shendun.renter.dialog.animation.ZoomExit.ZoomOutExit;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.AddV2ZkRequest;
import com.shendun.renter.repository.bean.GetPlatFromRequest;
import com.shendun.renter.repository.bean.GetPlatFromResponse;
import com.shendun.renter.repository.bean.OrderDetailResponse;
import com.shendun.renter.repository.bean.ResponseBean;
import com.shendun.renter.repository.bean.UserInfo;
import com.shendun.renter.utils.CacheManager;
import com.shendun.renter.utils.CheckPermissionUtil;
import com.shendun.renter.utils.CommonUtils;
import com.shendun.renter.utils.DateUtil;
import com.shendun.renter.utils.IDCardUtils;
import com.shendun.renter.utils.PermissionDialogUtil;
import com.shendun.renter.utils.ScreenUtil;
import com.shendun.renter.utils.Size;
import com.shendun.renter.utils.rxutils.ErrorHandleSubscriber;
import com.shendun.renter.widget.ChoosePopupWindow;
import com.trello.rxlifecycle4.android.ActivityEvent;

import com.shendun.renter.utils.ImageUtils;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import timber.log.Timber;

/*
 * 添加入住人页
 */
public class AddInActivity extends BaseActivity<ActivityAddOccupantBinding>
    implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private final static String TAG = AddInActivity.class.getSimpleName();

    // 拍照回传码
    public final static int CAMERA_REQUEST_CODE_FEEDBACK = 1;
    // 选择照片回传码
    public final static int CHOOSE_PHOTO_REQUEST_CODE_FEEDBACK = 2;

    private String mOrderType;//订单类型。1表示订单，2表示同住人
    private String mRoomNo;   //房号
    private String mRoomId;   //房间id
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

    private GetPlatFromResponse mGetPlatFromResponse;
    private int mSourcePosition = 0;                  //订单来源滚轮位置
    private String mOrderSourceCode = "";             //订单来源
    private int mNationPosition = 0;                  //民族选择滚轮位置
    private String mNation = "";                      //默认。01 汉族
    private String mZklx = "1";                       //客人类型，客人入住需传值；1表示主客，2表示同住客,默认1，
    private int mSexPosition = 0;                     //性别滚轮位置
    private String mSex = "";                         //性别。1男2女
    private String mBirthday = "";                    //出生日期
    private String mPic = "";                         //人物场景照
    private int mCohaPosition = 0;                    //是否有同住人员滚轮位置
    private String mCoha = "";                        //是否有同住人。0否 1 是
    private int mCohaRelationPosition = 0;            //同住人关系滚轮位置
    private String mCohaRelation = "";                //同住人关系 JHT(监护人)，TX(同学)，QT(其他)
    private int mMinorOrNoPosition = 0;               //是否是未成年人滚轮位置
    private String mMinorOrNo = "";                   //是否是未成年人。0否 1 是
    private int mBabyOrNoPosition = 0;                //是否是婴幼儿滚轮位置
    private String mHasBabyOrNo = "";                 //是否携带婴幼儿。0否 1 是
    private int mGuardianRelationPosition = 0;        //监护人关系滚轮位置
    private String mGuardianRelation = "";            //监护人关系 FQ(父亲)，MQ(母亲)，LS(老师)，QT(其他)
    private int mMinorSuspPosition = 0;               //未成年人存在可疑情况滚轮位置
    private String mMinorSusp = "";                   //未成年人是否存在可疑情况。0否 1 是
    private int mInspecPosition = 0;                  //巡查是否发现异常滚轮位置
    private String mInspec = "";                      //巡查是否发现异常。0否 1 是

    // 拍照的照片的存储位置
    private String photoPathFeedBack;
    // 照片所在的Uri地址
    private Uri photoUriFeedBack;
    // 上传照片路径
    private String photoImgPath;

    //新增同住人时使用 start
    private String mOrderNumber = "";
    private String mOrderStatus = "";
    private OrderDetailResponse mOrderDetailResponse = null;
    //新增同住人时使用 end

    private enum ACTION{
        submit/*提交数据*/, checkIn/*直接入住*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_occupant;
    }

    @Override
    protected void initEvent() {
        initData();
        orderSource();
    }

    private void initData(){
        mBinding.topbar.getLeftImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBinding.topbar.getCenterTextView().setText(R.string.add_room_add_person);
        mBinding.topbar.getCenterTextView().setTextSize(ScreenUtil.dp2px(mContext, 5));
        mBinding.topbar.getCenterTextView().setTextColor(getResources().getColor(R.color.text_title_color));

        mOrderType = getIntent().getStringExtra(ParamConfig.PARAM_ORDER_TYPE);
        if(OrderType.guest == orderType()){
            mOrderNumber = getIntent().getStringExtra(SpConfig.KEY_ORDER_NUMBER);
            mOrderStatus = getIntent().getStringExtra(SpConfig.KEY_ORDER_STATUS);
        }
        mRoomNo = getIntent().getStringExtra(ParamConfig.PARAM_ROOM_NO);
        mRoomId = getIntent().getStringExtra(ParamConfig.PARAM_ROOM_ID);
        mBinding.tvRoomNo.setText("房号:" + mRoomNo);

        initDateTime();
        showItems();
        setOnClickListener();
    }

    private void initDateTime(){
        mDay = 1;
        mIn = DatimeEntity.now();
        mBirthdayEntity = mIn.getDate();
        mInDateEntity = mIn.getDate();
        mInTimeEntity = mIn.getTime();

        String inDataTime = mInDateEntity.toString();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date newDateTime = TimeUtils.getDate(inDataTime, format, mDay, TimeConstants.DAY);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(newDateTime);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mOutDateEntity = DateEntity.target(year, month, day);
        mOutTimeEntity = TimeEntity.target(12, 0, 0);

        mBinding.tvInDate.setText("入住日期 : " + mInDateEntity.getYear()
                + "-" + CommonUtils.prefixZero(mInDateEntity.getMonth())
                + "-" + CommonUtils.prefixZero(mInDateEntity.getDay()));
        mBinding.tvInTime.setText("入住时间 : " + CommonUtils.prefixZero(mInTimeEntity.getHour())
                + ":" + CommonUtils.prefixZero(mInTimeEntity.getMinute()));
        mBinding.tvOutDay.setText("入住天数 : " + mDay);
        mBinding.tvOutDate.setText("预离日期 : " + mOutDateEntity.getYear()
                + "-" + CommonUtils.prefixZero(mOutDateEntity.getMonth())
                + "-" + CommonUtils.prefixZero(mOutDateEntity.getDay()));
        mBinding.tvOutTime.setText("预离时间 : " + CommonUtils.prefixZero(mOutTimeEntity.getHour())
                + ":" + CommonUtils.prefixZero(mOutTimeEntity.getMinute()));
    }

    private void setOnClickListener(){
        mBinding.tvOrderSource.setOnClickListener(this);
        mBinding.tvSex.setOnClickListener(this);
        mBinding.tvBirthday.setOnClickListener(this);
        mBinding.tvNation.setOnClickListener(this);
        mBinding.tvCoha.setOnClickListener(this);
        mBinding.tvCohaRelation.setOnClickListener(this);
        mBinding.tvMinorAsk.setOnClickListener(this);
        mBinding.tvGuardianRelation.setOnClickListener(this);
        mBinding.tvMinorSusp.setOnClickListener(this);
        mBinding.tvInspectionSusp.setOnClickListener(this);
        mBinding.llInDate.setOnClickListener(this);
        mBinding.llInTime.setOnClickListener(this);
        mBinding.llOutDay.setOnClickListener(this);
        mBinding.llOutDate.setOnClickListener(this);
        mBinding.llOutTime.setOnClickListener(this);
        mBinding.btnPortraitScene.setOnClickListener(this);
        mBinding.btOk.setOnClickListener(this);
        mBinding.btCheckIn.setOnClickListener(this);
        mBinding.radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mBinding.tvOrderSource){
            showOrderSourcePop();
        } else if(v == mBinding.tvSex){
            showSexPop();
        } else if(v == mBinding.tvBirthday){
            showBirthdayPop();
        } else if(v == mBinding.tvNation){
            showNationPop();
        } else if(v == mBinding.tvCoha){
            showCohaPop();
        } else if(v == mBinding.tvCohaRelation){
            showCohaRelationPop();
        } else if(v == mBinding.tvMinorAsk){
            showMinorOrNoPop();
        } else if(v == mBinding.tvGuardianRelation){
            showGuardianRelationPop();
        } else if(v == mBinding.tvMinorSusp){
            showMinorSuspPop();
        } else if(v == mBinding.tvInspectionSusp){
            showInspectioSuspPop();
        } else if(v == mBinding.btnPortraitScene){
            takePhotoClick();
        } else if(v == mBinding.llInDate){
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
            if(v == mBinding.llInDate) picker.getWheelLayout().setDefaultValue(mInDateEntity);
            if(v == mBinding.llOutDate) picker.getWheelLayout().setDefaultValue(mOutDateEntity);
            picker.setOnDatePickedListener(new OnDatePickedListener(){

                @Override
                public void onDatePicked(int year, int month, int day) {
                    if(v == mBinding.llInDate){
                        mBinding.tvInDate.setText("入住日期 : " + year + "-" + CommonUtils.prefixZero(month) + "-" + CommonUtils.prefixZero(day));
                        mInDateEntity = DateEntity.target(year, month, day);
                        String inDate = mInDateEntity.toString();
                        String outDate = mOutDateEntity.toString();
                        if(-1 != DateUtil.compareDateDay(inDate, outDate)){
                            showCenterToast("入住时间要小于退房时间");
                            return;
                        }

                        DateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        long daySpan = TimeUtils.getTimeSpan(outDate, inDate, mFormat, TimeConstants.DAY);
                        Timber.tag(TAG).d("daySpan:" + daySpan);
                        mDay = (int)daySpan;
                    }

                    if(v == mBinding.llOutDate){
                        mBinding.tvOutDate.setText("预离日期 : " + year + "-" + CommonUtils.prefixZero(month) + "-" + CommonUtils.prefixZero(day));
                        mOutDateEntity = DateEntity.target(year, month, day);
                        String inDate = mInDateEntity.toString();
                        String outDate = mOutDateEntity.toString();
                        if(-1 != DateUtil.compareDateDay(inDate, outDate)){
                            showCenterToast("退房时间要大于入住时间");
                            return;
                        }

                        DateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        long daySpan = TimeUtils.getTimeSpan(outDate, inDate, mFormat, TimeConstants.DAY);
                        Timber.tag(TAG).d("daySpan:" + daySpan);
                        mDay = (int)daySpan;
                    }

                    mBinding.tvOutDay.setText("入住天数 : " + mDay);
                }
            });
            picker.show();
        } else if(v == mBinding.llInTime || v == mBinding.llOutTime){
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
            if(v == mBinding.llInTime)  picker.getWheelLayout().setDefaultValue(TimeEntity.now());
            if(v == mBinding.llOutTime) picker.getWheelLayout().setDefaultValue(mOutTimeEntity);
            picker.setOnTimePickedListener(new OnTimePickedListener() {
                @Override
                public void onTimePicked(int hour, int minute, int second) {
                    if(v == mBinding.llInTime){
                        mBinding.tvInTime.setText("入住时间 : " + CommonUtils.prefixZero(hour) + ":" + CommonUtils.prefixZero(minute));
                        mInTime = hour + ":" + minute;
                        mInTimeEntity = TimeEntity.target(hour, minute, second);
                    }

                    if(v == mBinding.llOutTime){
                        mBinding.tvOutTime.setText("预离时间 : " + CommonUtils.prefixZero(hour) + ":" + CommonUtils.prefixZero(minute));
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
            picker.setOnNumberPickedListener(new OnNumberPickedListener() {
                @Override
                public void onNumberPicked(int position, Number item) {
                    mDay = item.intValue();
                    mBinding.tvOutDay.setText("入住天数 : " + mDay);

                    String inDataTime = mInDateEntity.toString();
                    DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date newDateTime = TimeUtils.getDate(inDataTime, format, mDay, TimeConstants.DAY);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(newDateTime);
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    mOutDateEntity = DateEntity.target(year, month, day);
                    mOutTimeEntity = TimeEntity.target(mInTimeEntity.getHour(), mInTimeEntity.getMinute(), mInTimeEntity.getSecond());
                    mBinding.tvOutDate.setText("预离日期 : " + mOutDateEntity.getYear()
                            + "-" + CommonUtils.prefixZero(mOutDateEntity.getMonth())
                            + "-" + CommonUtils.prefixZero(mOutDateEntity.getDay()));
                    mBinding.tvOutTime.setText("预离时间 : " + CommonUtils.prefixZero(mOutTimeEntity.getHour())
                            + ":" + CommonUtils.prefixZero(mOutTimeEntity.getMinute()));
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
            picker.setRange(1, 7, 1);
            picker.setDefaultValue(mDay);
            picker.getTitleView().setText("入住天数");
            picker.show();
        } else if (v == mBinding.btOk) {
            final String name = mBinding.etName.getText().toString().trim();
            final String idCard = mBinding.etIdCard.getText().toString().trim();
            if(null == name || TextUtils.isEmpty(name)
                    || null == idCard || TextUtils.isEmpty(idCard)){
                final NormalDialog dialog = new NormalDialog(mContext);
                dialog.setCanceledOnTouchOutside(false);
                dialog.isContentShow(true)
                        .titleTextSize(50f)
                        .content(getString(R.string.dlg_content_missed_name_idcard))
                        .contentTextSize(50f)
                        .contentTextColor(getResources().getColor(R.color.cloud_normal_dark))
                        .style(NormalDialog.STYLE_TWO)//
                        .showAnim(new FadeEnter())
                        .btnNum(2)
                        .btnTextSize(45f,45f)
                        .btnText(getString(R.string.btn_cancel),
                                getString(R.string.btn_ok))
                        .btnTextColor(getResources().getColor(R.color.cloud_light_dark),
                                getResources().getColor(R.color.cloud_theme))
                        .dismissAnim(new ZoomOutExit())//
                        .show();
                dialog.setOnBtnClickL(() -> {
                    dialog.dismiss();
                }, () -> {
                    submitOrder();
                    dialog.dismiss();
                });
            } else {
                submitOrder();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        RadioButton radioButton = (RadioButton)findViewById(i);
        if("是".equals(radioButton.getText())){
            mHasBabyOrNo = "1";
            mBinding.etDesc.setHint(R.string.hint_baby_info);
        } else {
            mHasBabyOrNo = "0";
            mBinding.etDesc.setHint(R.string.hint_desc_info);
        }
    }

    private void submitOrder(){
        final String name = mBinding.etName.getText().toString().trim();
        final String idCard = mBinding.etIdCard.getText().toString().trim();
        final String phone = mBinding.etPhone.getText().toString().trim();
        final String orderNo = mBinding.etOrderNo.getText().toString().trim();
        final String bz = mBinding.etDesc.getText().toString().trim();

        if(TextUtils.isEmpty(mOrderSourceCode)) {
            showCenterToast("请选择订单来源");
            return;
        }

        if(TextUtils.isEmpty(orderNo)) {
            showCenterToast("请输入订单在网约房平台编号");
            return;
        }

//        if(TextUtils.isEmpty(name)) {
//            showCenterToast("请输入姓名");
//            return;
//        }

//        if(TextUtils.isEmpty(idCard) || !RegexUtils.isIDCard18Exact(idCard)) {
//            showCenterToast("请输入正确的身份证号码");
//            return;
//        }

        if(TextUtils.isEmpty(phone) || !RegexUtils.isMobileSimple(phone)) {
            showCenterToast("请输入正确的手机号码");
            return;
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

        //携带婴幼儿
        if("1".equals(mHasBabyOrNo) && TextUtils.isEmpty(bz)){
            showCenterToast("请输入婴幼儿备注");
            return;
        }

        AddV2ZkRequest request = new AddV2ZkRequest();
        request.setPtype(ConstantConfig.TYPE_ORDER);  //数据类型，1表示订单，2表示客人入住
        request.setStype(ConstantConfig.ORDER_ADD);   //I(大写的i)表示新增，U表示更新
        request.setJlbh("");    //订单：新增传空字符串;更新传订单号;客人入住：传入订单号;
        request.setLkbh("");    //客人唯一编号.订单：传空字符串；客人入住：新增传空字符串，更新传客人编号；
        request.setXm((null == name || TextUtils.isEmpty(name)) ? "" : name);
        request.setXb("");
        request.setMz("01");
        request.setCsrq("");
        request.setZjhm((null == idCard || TextUtils.isEmpty(idCard)) ? "" : idCard);
        request.setXxdz(address);
        request.setLxdh(phone);

        StringBuffer inDateBuffer = new StringBuffer();
        inDateBuffer.append(mInDateEntity.getYear());
        inDateBuffer.append("-" + CommonUtils.prefixZero(mInDateEntity.getMonth()));
        inDateBuffer.append("-" + CommonUtils.prefixZero(mInDateEntity.getDay()));
        inDateBuffer.append(" " + CommonUtils.prefixZero(mInTimeEntity.getHour()));
        inDateBuffer.append(":" + CommonUtils.prefixZero(mInTimeEntity.getMinute()));
        request.setRzsj(inDateBuffer.toString());

        StringBuffer outDateBuffer = new StringBuffer();
        outDateBuffer.append(mOutDateEntity.getYear());
        outDateBuffer.append("-" + CommonUtils.prefixZero(mOutDateEntity.getMonth()));
        outDateBuffer.append("-" + CommonUtils.prefixZero(mOutDateEntity.getDay()));
        outDateBuffer.append(" " + CommonUtils.prefixZero(mOutTimeEntity.getHour()));
        outDateBuffer.append(":" + CommonUtils.prefixZero(mOutTimeEntity.getMinute()));
        request.setTfsj(outDateBuffer.toString());

        request.setFhid(mRoomId);
        request.setZklx("1");
        request.setDwbh(userInfo.getDwbh());
        request.setCybh(userInfo.getCybh());
        request.setDdlypttydm(mOrderSourceCode);
        request.setDdptbh(orderNo);
        request.setPic("");
        request.setG_is_minor("0");
        request.setG_is_baby(mHasBabyOrNo);
        request.setG_guardian_relationship("");
        request.setG_guardian_phone("");
        request.setG_guardian_name("");
        request.setG_guardian_idCard("");
        request.setG_guardian_happening("");
        request.setG_is_with("0");
        request.setG_with_relationship("");
        request.setG_with_happening("");
        request.setG_is_injured("0");
        request.setG_suspicious_descrip("");
        request.setG_is_patrol_erro("0");
        request.setG_patrol_happening("");
        request.setBz(bz);
        getRepository(NetService.class).getAddV2Zks(UrlConfig.FD_V2_ADD_ZK, request.getRequestBody())
                .compose(dispatchSchedulers(true))
                .subscribe(new RepositorySubscriber<ResponseBean>() {
                    @Override
                    protected void onResponse(ResponseBean result) {
                        if ("0".equals(result.getCode())) {
                            showCenterToast("预订成功");
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
     * 查询订单来源数据
     */
    private void orderSource(){
        UserInfo userInfo = CacheManager.readFromJson(this, ConstantConfig.CACHE_NAME_USER_INFO, UserInfo.class);
        if(null == userInfo){
            LogUtils.d(TAG, "loadData:null == userInfo");
            showCenterToast("账户信息失效，请重新登陆");
            return;
        }

        GetPlatFromRequest request = new GetPlatFromRequest();
        request.setDwbh(userInfo.getDwbh());
        getRepository(NetService.class).getPlatfrom(UrlConfig.GET_PLATFROM, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<ResponseBean<GetPlatFromResponse>>() {
                    @Override
                    protected void onResponse(ResponseBean<GetPlatFromResponse> result) {
                        if (result.isSuccessful()) {
                            mGetPlatFromResponse = result.getData();
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
     * 显示订单来源弹窗
     */
    private void showOrderSourcePop() {
        if(mGetPlatFromResponse == null  || mGetPlatFromResponse.getList() == null
        || mGetPlatFromResponse.getList().size() == 0){
            return;
        }

        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_bottom_order_source_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = new ArrayList<>();
            for(GetPlatFromResponse.ListDTO dto : mGetPlatFromResponse.getList()){
                list.add(dto.getName());
            }
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mSourcePosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSourcePosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvOrderSource.setText(mGetPlatFromResponse.getList().get(mSourcePosition).getName());
                    mOrderSourceCode = mGetPlatFromResponse.getList().get(mSourcePosition).getCode();

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 性别弹窗
     */
    private void showSexPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.sex));
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mSexPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSexPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvSex.setText(list.get(mSexPosition));
                    mSex = String.valueOf(mSexPosition + 1);

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 出生日期弹窗
     */
    private void showBirthdayPop() {
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
        picker.getWheelLayout().setRange(DateEntity.target(1920, 1, 1)
                , DatimeEntity.now().getDate());
        picker.getWheelLayout().setDefaultValue(mBirthdayEntity);
        picker.setOnDatePickedListener(new OnDatePickedListener(){

            @Override
            public void onDatePicked(int year, int month, int day) {
                mBinding.tvBirthday.setText("出生日期 : " + year + "-" + month + "-" + day);
                mBirthdayEntity = DateEntity.target(year, month, day);
                mBirthday = year + "-" + month + "-" + day;
            }
        });
        picker.show();
    }

    /**
     * 民族选择弹窗
     */
    private void showNationPop(){
        final String[] NATIION_DATAS = new String[]{"汉", "蒙古", "回", "藏", "维吾尔", "苗", "彝", "壮", "布依", "朝鲜", "满", "侗", "瑶", "白", "土家", "哈尼", "哈萨克", "傣", "黎", "傈僳", "佤", "畲", "高山", "拉祜", "水", "东乡", "纳西", "景颇", "柯尔克孜", "土", "达斡尔", "仫佬", "羌", "布朗", "撒拉", "毛南", "仡佬", "锡伯", "阿昌", "普米", "塔吉克", "怒", "乌孜别克", "俄罗斯", "鄂温克", "德昂", "保安", "裕固", "京", "塔塔尔", "独龙", "鄂伦春", "赫哲", "门巴", "珞巴", "基诺"};
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(NATIION_DATAS);
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mNationPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNationPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvNation.setText(list.get(mNationPosition));
                    if(mNationPosition + 1 > 9){
                        mNation = String.valueOf(mNationPosition + 1);
                    } else {
                        mNation = "0" + String.valueOf(mNationPosition + 1);
                    }

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 是否有同住人弹窗
     */
    private void showCohaPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.cohaOrNo));
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mCohaPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCohaPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvCoha.setText(list.get(mCohaPosition));
                    mCoha = String.valueOf(mCohaPosition);
                    if(0 == mCohaPosition){
                        showCohaItems(false);
                    } else {
                        showCohaItems(true);
                    }

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 同住人关系弹窗
     */
    private void showCohaRelationPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.cohaRelation));
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mCohaRelationPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCohaRelationPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvCohaRelation.setText(list.get(mCohaRelationPosition));
                    if(0 == mCohaRelationPosition){
                        mCohaRelation = "JHT";
                    } else if(1 == mCohaRelationPosition){
                        mCohaRelation = "TX";
                    } else if(2 == mCohaRelationPosition){
                        mCohaRelation = "QT";
                    }

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 是否是未成年人弹窗
     */
    private void showMinorOrNoPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.minorOrNo));
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mMinorOrNoPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMinorOrNoPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvMinorAsk.setText(list.get(mMinorOrNoPosition));
                    mMinorOrNo = String.valueOf(mMinorOrNoPosition);
                    if(0 == mMinorOrNoPosition){
                        showMinorItems(false);
                    } else {
                        showMinorItems(true);
                    }

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 是否是婴幼儿弹窗
     */
    private void showBabyOrNoPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.babyOrNo));
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mBabyOrNoPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBabyOrNoPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvBabyAsk.setText(list.get(mBabyOrNoPosition));
                    mHasBabyOrNo = String.valueOf(mBabyOrNoPosition);

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 未成年人监护人关系弹窗
     */
    private void showGuardianRelationPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.guardian));
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mGuardianRelationPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGuardianRelationPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvGuardianRelation.setText(list.get(mGuardianRelationPosition));
                    if(0 == mGuardianRelationPosition){
                        mGuardianRelation = "FQ";
                    } else if(1 == mGuardianRelationPosition){
                        mGuardianRelation = "MQ";
                    } else if(2 == mGuardianRelationPosition){
                        mGuardianRelation = "LS";
                    } else {
                        mGuardianRelation = "QT";
                    }

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 未成年人是否存在可疑情况弹窗
     */
    private void showMinorSuspPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.minorSusp));
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mMinorSuspPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMinorSuspPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvMinorSusp.setText(list.get(mMinorSuspPosition));
                    mMinorSusp = String.valueOf(mMinorSuspPosition);
                    if(0 == mMinorSuspPosition){
                        mBinding.llMinorSuspDesc.setVisibility(View.GONE);
                    } else {
                        mBinding.llMinorSuspDesc.setVisibility(View.VISIBLE);
                    }

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 巡查是否发现异常弹窗
     */
    private void showInspectioSuspPop() {
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = Arrays.asList(getResources().getStringArray(R.array.InspectioSusp));
            wheelLayout.setData(list);
            wheelLayout.setDefaultPosition(mInspecPosition);

            TextView tvCancel = view.findViewById(R.id.tv_cancel);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPopWindow();
                }
            });

            TextView tvOk = view.findViewById(R.id.tv_ok);
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mInspecPosition = wheelLayout.getWheelView().getCurrentPosition();
                    mBinding.tvInspectionSusp.setText(list.get(mInspecPosition));
                    mInspec = String.valueOf(mInspecPosition);
                    if(0 == mInspecPosition){
                        showInspectionStayItems(false);
                    } else {
                        showInspectionStayItems(true);
                    }

                    dismissPopWindow();
                }
            });
        });
        ((ChoosePopupWindow)popupWindow).setOnDismissListener(new ChoosePopupWindow.DismissCallback() {
            @Override
            public void onDismiss() {

            }
        });
        ((ChoosePopupWindow)popupWindow).show(getWindow().getDecorView(), getWindow());
    }

    /**
     * 显示隐藏同住人选项
     */
    private void showCohaItems(boolean show) {
        int visible = show ? View.VISIBLE : View.GONE;
        mBinding.llCohaRelation.setVisibility(visible);
        mBinding.llCohaStatus.setVisibility(visible);
    }

    /**
     * 显示隐藏未成年人选项
     */
    private void showMinorItems(boolean show) {
        int visible = show ? View.VISIBLE : View.GONE;
        mBinding.llBabyAsk.setVisibility(visible);
        mBinding.llGuardianRelation.setVisibility(visible);
        mBinding.llGuardianPhone.setVisibility(visible);
        mBinding.llGuardianName.setVisibility(visible);
        mBinding.llGuardianIdcard.setVisibility(visible);
        mBinding.llGuardianStatus.setVisibility(visible);
        mBinding.llMinorSusp.setVisibility(visible);
        if(show){
            if("0".equals(mMinorSusp))
                mBinding.llMinorSuspDesc.setVisibility(View.GONE);
            else
                mBinding.llMinorSuspDesc.setVisibility(View.VISIBLE);
        } else {
            mBinding.llMinorSuspDesc.setVisibility(View.GONE);
        }
    }

    /**
     * 显示隐藏住宿巡查情况选项
     */
    private void showInspectionStayItems(boolean show) {
        int visible = show ? View.VISIBLE : View.GONE;
        mBinding.llInspectionStay.setVisibility(visible);
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
                            return CheckPermissionUtil.checkPermissionObservable(AddInActivity.this,
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
     * 判断本界面是订单，还是同住人
     */
    private OrderType orderType() {
        if(null == mOrderType || TextUtils.isEmpty(mOrderType))
            return OrderType.order;

        if(ConstantConfig.TYPE_ORDER.equals(mOrderType)){
            return OrderType.order;
        } else
            return OrderType.guest;
    }

    /**
     * 根据类型显示隐藏items
     */
    private void showItems() {
        if(OrderType.order == orderType()){
            mBinding.llOrderSource.setVisibility(View.VISIBLE);
            mBinding.llOrderNo.setVisibility(View.VISIBLE);
            mBinding.llInDate.setVisibility(View.VISIBLE);
            mBinding.llInTime.setVisibility(View.VISIBLE);
            mBinding.llOutDay.setVisibility(View.VISIBLE);
            mBinding.llOutDate.setVisibility(View.VISIBLE);
            mBinding.llOutTime.setVisibility(View.VISIBLE);
        } else {
            mBinding.llOrderSource.setVisibility(View.GONE);
            mBinding.llOrderNo.setVisibility(View.GONE);
            mBinding.llInDate.setVisibility(View.GONE);
            mBinding.llInTime.setVisibility(View.GONE);
            mBinding.llOutDay.setVisibility(View.GONE);
            mBinding.llOutDate.setVisibility(View.GONE);
            mBinding.llOutTime.setVisibility(View.GONE);
        }
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