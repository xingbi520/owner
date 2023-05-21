package com.shendun.renter.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.shendun.renter.bean.Constants;
import com.shendun.renter.bean.OrderType;
import com.shendun.renter.config.ConstantConfig;
import com.shendun.renter.config.ParamConfig;
import com.shendun.renter.config.SpConfig;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityEditOccupantBinding;
import com.shendun.renter.fragment.BottomMenuFragment;
import com.shendun.renter.fragment.adapter.MenuItem;
import com.shendun.renter.fragment.adapter.MenuItemOnClickListener;
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
import com.shendun.renter.utils.NationUtils;
import com.shendun.renter.utils.PermissionDialogUtil;
import com.shendun.renter.utils.ScreenUtil;
import com.shendun.renter.utils.Size;
import com.shendun.renter.utils.rxutils.ErrorHandleSubscriber;
import com.shendun.renter.widget.ChoosePopupWindow;
import com.trello.rxlifecycle4.android.ActivityEvent;
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
import java.util.Map;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.functions.Function;
import timber.log.Timber;

/*
 * 修改入住人主客
 */
public class EditInActivity extends BaseActivity<ActivityEditOccupantBinding>
    implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private final static String TAG = EditInActivity.class.getSimpleName();

    // 拍照回传码
    public final static int CAMERA_REQUEST_CODE_FEEDBACK = 1;
    // 选择照片回传码
    public final static int CHOOSE_PHOTO_REQUEST_CODE_FEEDBACK = 2;

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
    private String mAge = "";                         //年龄
    private String mBirthday = "";                    //出生日期
    private String mAddress = "";                     //地址
    private String mPic = "";                         //人物场景照
    private int mCohaPosition = 0;                    //是否有同住人员滚轮位置
    private String mCoha = "";                        //是否有同住人。0否 1 是
    private int mCohaRelationPosition = 0;            //同住人关系滚轮位置
    private String mCohaRelation = "";                //同住人关系 JHT(监护人)，TX(同学)，QT(其他)
    private int mMinorOrNoPosition = 0;               //是否是未成年人滚轮位置
    private String mMinorOrNo = "0";                  //是否是未成年人。0否 1 是
    private int mBabyOrNoPosition = 0;                //是否是婴幼儿滚轮位置
    private String mHasBabyOrNo = "0";                //是否携带婴幼儿。0否 1 是
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
    private String mOrderStatus = "";  //订单状态。0空闲，1待入住，2已入住，3已退房，4取消，9全部
    private String mPersonStatus = "";
    private OrderDetailResponse mOrderDetailResponse = null;
    //新增同住人时使用 end

    private enum ACTION{
        save/*修改保存*/, checkIn/*直接入住*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_occupant;
    }

    @Override
    protected void initEvent() {
        initData();
        loadData();
    }

    private void initData(){
        initDateTime();

        mOrderNumber = getIntent().getStringExtra(SpConfig.KEY_ORDER_NUMBER);
        mOrderStatus = getIntent().getStringExtra(SpConfig.KEY_ORDER_STATUS);
        mPersonStatus = getIntent().getStringExtra(ParamConfig.PARAM_PERSON_STATUS);
        mRoomNo = getIntent().getStringExtra(ParamConfig.PARAM_ROOM_NO);
        mRoomId = getIntent().getStringExtra(ParamConfig.PARAM_ROOM_ID);
        mBinding.tvRoomNo.setText("房号:" + mRoomNo);

        mBinding.topbar.getCenterTextView().setText(R.string.add_room_edit_person);
        mBinding.topbar.getCenterTextView().setTextSize(ScreenUtil.dp2px(mContext, 5));
        mBinding.topbar.getCenterTextView().setTextColor(getResources().getColor(R.color.text_title_color));

        setOnClickListener();
        setEditTextListener();
        setItems();
    }

    private void initDateTime(){
        mIn = DatimeEntity.now();
        mBirthdayEntity = mIn.getDate();
    }

    private void setOnClickListener(){
        mBinding.topbar.getLeftImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mBinding.tvOrderSource.setOnClickListener(this);
        mBinding.tvSex.setOnClickListener(this);
        mBinding.tvBirthday.setOnClickListener(this);
        mBinding.tvNation.setOnClickListener(this);
        mBinding.tvCoha.setOnClickListener(this);
        mBinding.tvCohaRelation.setOnClickListener(this);
        mBinding.tvBabyAsk.setOnClickListener(this);
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

    private void setEditTextListener() {
        mBinding.etIdCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //解析身份证数据
                String idCard = mBinding.etIdCard.getText().toString().trim();
                if (null != idCard && !TextUtils.isEmpty(idCard) && RegexUtils.isIDCard18Exact(idCard)) {
                    Map<String, String> dataMap = IDCardUtils.getBirAgeSex(idCard);
                    mBirthday = dataMap.get("birthday");
                    mAge = dataMap.get("age");
                    String sex = dataMap.get("sexCode");
                    if ("M".equals(sex)) {
                        mSex = "1";
                    }
                    else if ("F".equals(sex)){
                        mSex = "2";
                    }
                    mAddress = IDCardUtils.getAddressByCardId(mContext, idCard);
                    Timber.tag(TAG).d("mBirthday:" + mBirthday + " mAge:" + mAge + " mSex:" + mSex + " mAddress:" + mAddress);

                    if(DateUtil.checkAdult(mBirthday) == 1){
                        //成年人
                        showMinorItems(false);
                        mMinorOrNo = "0";
                    } else if(DateUtil.checkAdult(mBirthday) == 0){
                        //未成年人
                        showMinorItems(true);
                        mMinorOrNo = "1";
                    }
                } else {
                    showMinorItems(false);
                    mMinorOrNo = "0";
                    mBirthday = "";
                    mAge = "";
                    mSex = "";
                    mAddress = "";
                }
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
                            setQueryData(mOrderDetailResponse);
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

    private void setQueryData(OrderDetailResponse response){
        if(null == response){
            return;
        }

        mBinding.etName.setText(response.getXm());
        mBinding.etPhone.setText(response.getLxdh());
        mBinding.etIdCard.setText(response.getZjhm());
        mBinding.tvNation.setText(NationUtils.getNationCn(response.getMz()));
        mNationPosition = NationUtils.getNationIndex(response.getMz());
        mNation = response.getMz();
        mBinding.tvGuardianRelation.setText(response.getG_guardian_relationship());
        mBinding.etGuardianPhone.setText(response.getG_guardian_phone());
        mBinding.etGuardianName.setText(response.getG_guardian_name());
        mBinding.etGuardianIdcard.setText(response.getG_guardian_idcard());
        mBinding.etGuardianStatus.setText(response.getG_guardian_happening());
        mBinding.tvMinorSusp.setText("1".equals(response.getG_is_injured()) ? "可疑" : "不可疑");
        mBinding.etMinorSuspDesc.setText(response.getG_suspicious_descrip());
        mBinding.etInspectionStay.setText(response.getG_patrol_happening());
        mBinding.etDesc.setText(response.getBz());

        mHasBabyOrNo = response.getG_is_baby();
        if("1".equals(mHasBabyOrNo)){
            mBinding.btnBabyYes.setChecked(true);
            mBinding.btnBabyNo.setChecked(false);
        } else {
            mBinding.btnBabyYes.setChecked(false);
            mBinding.btnBabyNo.setChecked(true);
        }

        String checkIn = response.getRzsj() + ":00";  //入住时间
        //预订修改使用当前时间
        if(ConstantConfig.ORDER_ADD.equals(mPersonStatus)){
            checkIn = TimeUtils.getNowString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()));  //入住时间
        }
        String checkOut = response.getTfsj() + ":00"; //预离时间
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
        mBinding.tvInDate.setText("入住日期 : " + checkInYear
                + "-" + CommonUtils.prefixZero(Integer.valueOf(checkInMonth))
                + "-" + CommonUtils.prefixZero(Integer.valueOf(checkInDay)));
        mBinding.tvInTime.setText("入住时间 : " + CommonUtils.prefixZero(Integer.valueOf(checkInHour))
                + ":" + CommonUtils.prefixZero(Integer.valueOf(checkInMin)));

        Date checkOutDate = TimeUtils.string2Date(checkOut, "yyyy-MM-dd HH:mm:ss");
        String checkOutYear = new SimpleDateFormat("yyyy").format(checkOutDate);
        String checkOutMonth = new SimpleDateFormat("MM").format(checkOutDate);
        String checkOutDay = new SimpleDateFormat("dd").format(checkOutDate);
        String checkOutHour = new SimpleDateFormat("HH").format(checkOutDate);
        String checkOutMin = new SimpleDateFormat("mm").format(checkOutDate);
        String checkOutSec = new SimpleDateFormat("ss").format(checkOutDate);
        mOutDateEntity = DateEntity.target(Integer.valueOf(checkOutYear), Integer.valueOf(checkOutMonth), Integer.valueOf(checkOutDay));
        mOutTimeEntity = TimeEntity.target(Integer.valueOf(checkOutHour), Integer.valueOf(checkOutMin), Integer.valueOf(checkOutSec));
        mBinding.tvOutDate.setText("预离日期 : " + checkOutYear
                + "-" + CommonUtils.prefixZero(Integer.valueOf(checkOutMonth))
                + "-" + CommonUtils.prefixZero(Integer.valueOf(checkOutDay)));
        mBinding.tvOutTime.setText("预离时间 : " + CommonUtils.prefixZero(Integer.valueOf(checkOutHour))
                + ":" + CommonUtils.prefixZero(Integer.valueOf(checkOutMin)));
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

    @Override
    public void onClick(View v) {
        if(v == mBinding.tvOrderSource){
            showOrderSourcePop();
        } else if(v == mBinding.tvSex){
            showSexPop();
        } else if(v == mBinding.tvNation){
            showNationPop();
        } else if(v == mBinding.tvCoha){
            showCohaPop();
        } else if(v == mBinding.tvCohaRelation){
            showCohaRelationPop();
        } else if(v == mBinding.tvGuardianRelation){
            showGuardianRelationPop();
        } else if(v == mBinding.tvMinorSusp){
            showMinorSuspPop();
        } else if(v == mBinding.tvInspectionSusp){
            showInspectioSuspPop();
        } else if(v == mBinding.btnPortraitScene){
            showChooseSheet();
        } /*else if(v == mBinding.llOutDate){
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
            if(v == mBinding.llInDate) {
                picker.getWheelLayout().setRange(mInDateEntity, DateEntity.target(2220, 1, 1), mInDateEntity);
            }
            if(v == mBinding.llOutDate) {
                picker.getWheelLayout().setRange(mInDateEntity, DateEntity.target(2220, 1, 1), mOutDateEntity);
            }
            picker.setOnDatePickedListener(new OnDatePickedListener(){

                @Override
                public void onDatePicked(int year, int month, int day) {
                    if(v == mBinding.llInDate){
                        mBinding.tvInDate.setText("入住日期 : " + year
                                + "-" + CommonUtils.prefixZero(month)
                                + "-" + CommonUtils.prefixZero(day));
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
                        mBinding.tvOutDate.setText("预离日期 : " + year
                                + "-" + CommonUtils.prefixZero(month)
                                + "-" + CommonUtils.prefixZero(day));
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
        }*/ else if(v == mBinding.llOutTime){
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
            if(v == mBinding.llInTime) picker.getWheelLayout().setDefaultValue(mInTimeEntity);
            if(v == mBinding.llOutTime) picker.getWheelLayout().setDefaultValue(mOutTimeEntity);
            picker.setOnTimePickedListener(new OnTimePickedListener() {
                @Override
                public void onTimePicked(int hour, int minute, int second) {
                    if(v == mBinding.llInTime){
                        mBinding.tvInTime.setText("入住时间 : " + CommonUtils.prefixZero(hour)
                                + ":" + CommonUtils.prefixZero(minute));
                        mInTime = hour + ":" + minute;
                        mInTimeEntity = TimeEntity.target(hour, minute, second);
                    }

                    if(v == mBinding.llOutTime){
                        mBinding.tvOutTime.setText("预离时间 : " + CommonUtils.prefixZero(hour)
                                + ":" + CommonUtils.prefixZero(minute));
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
            picker.getWheelLayout().setDefaultValue(mDay);
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
            if(ConstantConfig.ORDER_ADD.equals(mPersonStatus)){
                picker.setRange(ConstantConfig.DAY_MIN, ConstantConfig.DAY_MAX, 1);
            } else if(ConstantConfig.ORDER_UADATE.equals(mPersonStatus)){
                if(mDay > -1){
                    if(ConstantConfig.DAY_MAX == mDay){
                        picker.setRange(0, ConstantConfig.DAY_MAX - mDay, 1);
                    } else {
                        picker.setRange(ConstantConfig.DAY_MIN, ConstantConfig.DAY_MAX - mDay, 1);
                    }
                } else {
                    picker.setRange(ConstantConfig.DAY_MIN, ConstantConfig.DAY_MAX, 1);
                }
            }
            picker.setDefaultValue(mDay);
            picker.getTitleView().setText("入住天数");
            picker.show();
        } else if (v == mBinding.btOk) {
            submitOrder(ACTION.save);
        } else if (v == mBinding.btCheckIn) {
            submitOrder(ACTION.checkIn);
        }
    }

    private void submitOrder(ACTION action){
        final String name = mBinding.etName.getText().toString().trim();
        final String idCard = mBinding.etIdCard.getText().toString().trim();
        final String phone = mBinding.etPhone.getText().toString().trim();
        final String orderSource = mBinding.tvOrderSource.getText().toString().trim();
        final String orderNo = mBinding.etOrderNo.getText().toString().trim();
        final String cohaStatus = mBinding.etCohaStatus.getText().toString().trim();
        final String guardianPhone = mBinding.etGuardianPhone.getText().toString().trim();
        final String guardianName = mBinding.etGuardianName.getText().toString().trim();
        final String guardianIdcard = mBinding.etGuardianIdcard.getText().toString().trim();
        final String guardianStatus = mBinding.etGuardianStatus.getText().toString().trim();
        final String minorSuspDesc = mBinding.etMinorSuspDesc.getText().toString().trim();
        final String inspectionStay = mBinding.etInspectionStay.getText().toString().trim();
        final String bz = mBinding.etDesc.getText().toString().trim();

        if(null != mOrderStatus && !TextUtils.isEmpty(mOrderStatus)
                && Constants.ROOM_ORDER_READY.equals(mOrderStatus)){
            if(TextUtils.isEmpty(name)) {
                showCenterToast("请输入姓名");
                return;
            }

            if(TextUtils.isEmpty(mNation)){
                showCenterToast("请选择民族");
                return;
            }

            if(TextUtils.isEmpty(idCard) || !RegexUtils.isIDCard18Exact(idCard)) {
                showCenterToast("请输入正确的身份证号码");
                return;
            }

            //未成年人
            if("1".equals(mMinorOrNo)){
                if(TextUtils.isEmpty(mGuardianRelation)) {
                    showCenterToast("请选择监护人关系");
                    return;
                }

                if(TextUtils.isEmpty(guardianName)) {
                    showCenterToast("请输入监护人姓名");
                    return;
                }

                if(TextUtils.isEmpty(guardianPhone) || !RegexUtils.isMobileSimple(guardianPhone)) {
                    showCenterToast("请输入正确的监护人手机号");
                    return;
                }

                if(TextUtils.isEmpty(guardianIdcard) || !RegexUtils.isIDCard18Exact(guardianIdcard)) {
                    showCenterToast("请输入正确的监护人身份证号码");
                    return;
                }

                if(TextUtils.isEmpty(mMinorSusp)) {
                    showCenterToast("请选择未成年人是否可疑");
                    return;
                }

                if("1".equals(mMinorSusp) && TextUtils.isEmpty(minorSuspDesc)) {
                    showCenterToast("请输入未成年人可疑情况");
                    return;
                }

                if(TextUtils.isEmpty(mInspec)){
                    showCenterToast("请选择巡查是否发现异常");
                    return;
                }

                if("1".equals(mInspec) && TextUtils.isEmpty(inspectionStay)){
                    showCenterToast("请输入住宿巡查异常情况");
                    return;
                }
            }

            if(TextUtils.isEmpty(bz) && "1".equals(mHasBabyOrNo)) {
                showCenterToast("请输入备注内容");
                return;
            }
        }

        if(action == ACTION.checkIn){
            if(null == photoImgPath || TextUtils.isEmpty(photoImgPath)){
                showCenterToast("请拍摄人像场景照");
                return;
            } else {
                mPic = CommonUtils.fileToBase64(photoImgPath);
            }
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
        request.setPtype((action == ACTION.checkIn) ? ConstantConfig.TYPE_COHA : ConstantConfig.TYPE_ORDER); //数据类型，1表示订单，2表示主客直接入住，客人入住
        request.setStype((action == ACTION.checkIn) ? ConstantConfig.ORDER_ADD : ConstantConfig.ORDER_UADATE);      //I(大写的i)表示新增，U表示更新
        request.setJlbh(mOrderDetailResponse.getJlbh());    //订单：新增传空字符串;更新传订单号;客人入住：传入订单号;
        request.setLkbh("");    //客人唯一编号.订单：传空字符串；客人入住：新增传空字符串，更新传客人编号；
        request.setXm(name);
        request.setXb(mSex);
        request.setMz(mNation);
        request.setCsrq(mBirthday);
        request.setZjhm(idCard);
        request.setXxdz(mAddress);
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
        request.setDdlypttydm(mOrderDetailResponse.getDdlypttydm());
        request.setDdptbh(mOrderDetailResponse.getDdptbh());
        request.setPic(mPic);
        request.setG_is_minor(mMinorOrNo);
        request.setG_is_baby(mHasBabyOrNo);
        request.setG_guardian_relationship(mGuardianRelation);
        request.setG_guardian_phone(guardianPhone);
        request.setG_guardian_name(guardianName);
        request.setG_guardian_idCard(guardianIdcard);
        request.setG_guardian_happening(guardianStatus);
        request.setG_is_with(mCoha);
        request.setG_with_relationship(mCohaRelation);
        request.setG_with_happening(cohaStatus);
        request.setG_is_injured(mMinorSusp);
        request.setG_suspicious_descrip(minorSuspDesc);
        request.setG_is_patrol_erro(mInspec);
        request.setG_patrol_happening(inspectionStay);
        request.setBz(bz);
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
     * 民族选择弹窗
     */
    private void showNationPop(){
        popupWindow = new ChoosePopupWindow(mContext, R.layout.layout_coha_popup, view -> {
            TextView tv_cancel = view.findViewById(R.id.tv_cancel);
            TextView tv_ok = view.findViewById(R.id.tv_ok);
            OptionWheelLayout wheelLayout = view.findViewById(R.id.wheel);
            List<String> list = NationUtils.getNationList();
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
        mBinding.llGuardianRelation.setVisibility(visible);
        mBinding.llGuardianPhone.setVisibility(visible);
        mBinding.llGuardianName.setVisibility(visible);
        mBinding.llGuardianIdcard.setVisibility(visible);
        mBinding.llGuardianStatus.setVisibility(visible);
        mBinding.llMinorSusp.setVisibility(visible);
        mBinding.llInspectionSusp.setVisibility(visible);
        if(show){
            if("0".equals(mMinorSusp)) {
                mBinding.llMinorSuspDesc.setVisibility(View.GONE);
            } else {
                mBinding.llMinorSuspDesc.setVisibility(View.VISIBLE);
            }
            if("0".equals(mInspec)){
                mBinding.llInspectionStay.setVisibility(View.GONE);
            } else {
                mBinding.llInspectionStay.setVisibility(View.VISIBLE);
            }
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
     * 根据类型显示隐藏items
     */
    private void setItems() {
        mBinding.llOrderSource.setVisibility(View.GONE);
        mBinding.llOrderNo.setVisibility(View.GONE);
        mBinding.llCoha.setVisibility(View.GONE);
        mBinding.llInspectionSusp.setVisibility(View.GONE);
        mBinding.llInDate.setVisibility(View.VISIBLE);
        mBinding.llInTime.setVisibility(View.VISIBLE);
        mBinding.llOutDay.setVisibility(View.VISIBLE);
        mBinding.llOutDate.setVisibility(View.VISIBLE);
        mBinding.llOutTime.setVisibility(View.VISIBLE);
        mBinding.btCheckIn.setVisibility(View.VISIBLE);

        CommonUtils.setEditTextEnable(mBinding.etPhone, false);
        if(null != mOrderStatus && !TextUtils.isEmpty(mOrderStatus)
        && Constants.ROOM_ORDER_CHECK_IN.equals(mOrderStatus)){
            mBinding.tvNation.setOnClickListener(null);
            mBinding.tvGuardianRelation.setOnClickListener(null);
            mBinding.tvMinorSusp.setOnClickListener(null);
            mBinding.tvInspectionSusp.setOnClickListener(null);
            CommonUtils.setEditTextEnable(mBinding.etName, false);
            CommonUtils.setEditTextEnable(mBinding.etIdCard, false);
            CommonUtils.setEditTextEnable(mBinding.etGuardianPhone, false);
            CommonUtils.setEditTextEnable(mBinding.etGuardianName, false);
            CommonUtils.setEditTextEnable(mBinding.etGuardianIdcard, false);
            CommonUtils.setEditTextEnable(mBinding.etGuardianStatus, false);
            CommonUtils.setEditTextEnable(mBinding.etMinorSuspDesc, false);
            CommonUtils.setEditTextEnable(mBinding.etInspectionStay, false);
            mBinding.llPortraitScene.setVisibility(View.GONE);
            mBinding.btCheckIn.setVisibility(View.GONE);
        }
    }

    private void showChooseSheet() {
        BottomMenuFragment bottomMenuFragment = new BottomMenuFragment();
        List<MenuItem> menuItemList = new ArrayList<MenuItem>();
        MenuItem menuItem1 = new MenuItem();
        menuItem1.setText(getString(R.string.photo_from_camera));
        menuItem1.setStyle(MenuItem.MenuItemStyle.COMMON);
//        MenuItem menuItem2 = new MenuItem();
//        menuItem2.setText(getString(R.string.photo_from_album));
//        menuItem2.setStyle(MenuItem.MenuItemStyle.COMMON);
        menuItem1.setMenuItemOnClickListener(
                new MenuItemOnClickListener(bottomMenuFragment, menuItem1) {
                    @Override
                    public void onClickMenuItem(android.view.View v, MenuItem menuItem) {
                        takePhotoClick();
                    }
                });
//        menuItem2.setMenuItemOnClickListener(
//                new MenuItemOnClickListener(bottomMenuFragment, menuItem2) {
//                    @Override
//                    public void onClickMenuItem(android.view.View v, MenuItem menuItem) {
//                        openAlbumClick();
//                    }
//                });
        menuItemList.add(menuItem1);
//        menuItemList.add(menuItem2);
        bottomMenuFragment.setMenuItems(menuItemList);
        bottomMenuFragment.show(getSupportFragmentManager(), "BottomMenuFragment");
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
                            return CheckPermissionUtil.checkPermissionObservable(EditInActivity.this,
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
     * 选择照片
     */
    private void openAlbumClick() {
        CheckPermissionUtil.checkPermissionObservable(this, Manifest.permission.READ_EXTERNAL_STORAGE, new ForwardToSettingsCallback() {
                    @Override
                    public void onForwardToSettings(ForwardScope scope, List<String> deniedList) {
                        nerverAskAgainStorage();
                    }
                })
                .flatMap(new Function<Boolean, ObservableSource<Boolean>>() {
                    @Override
                    public ObservableSource<Boolean> apply(@NotNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            return CheckPermissionUtil.checkPermissionObservable(EditInActivity.this,
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
                                    openAlbum();
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

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent openAlbumIntent = new Intent(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.setType("image/*");
        startActivityForResult(openAlbumIntent, CHOOSE_PHOTO_REQUEST_CODE_FEEDBACK);
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

                case CHOOSE_PHOTO_REQUEST_CODE_FEEDBACK: {
                    if (data == null) {
                        return;
                    }
                    try {
                        handleImageFromAlbum(data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleImageFromAlbum(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        imagePath = ImageUtils.getFeedbackImgPathFromAlbum(mContext, uri);

        compressPhoto(imagePath);
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