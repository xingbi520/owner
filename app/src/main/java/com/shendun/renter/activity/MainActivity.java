package com.shendun.renter.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.azhon.appupdate.config.UpdateConfiguration;
import com.azhon.appupdate.manager.DownloadManager;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.PhoneUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.qmuiteam.qmui.util.QMUIStatusBarHelper;
import com.shendun.architecture.base.BaseActivity;
import com.shendun.architecture.net.RepositorySubscriber;
import com.shendun.renter.adapter.MainPagerAdapter;
import com.shendun.renter.bean.Constants;
import com.shendun.renter.config.UrlConfig;
import com.shendun.renter.databinding.ActivityMainBinding;
import com.shendun.renter.R;
import com.shendun.renter.event.TabJumpEvent;
import com.shendun.renter.repository.NetService;
import com.shendun.renter.repository.bean.AppUpdateRequest;
import com.shendun.renter.repository.bean.AppUpdateResponse;
import com.shendun.renter.repository.bean.Domain;
import com.shendun.renter.repository.bean.DomainResponse;
import com.shendun.renter.repository.bean.ResponseBean;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import me.yokeyword.fragmentation.ISupportFragment;

public class MainActivity extends BaseActivity<ActivityMainBinding>
    implements ViewPager.OnPageChangeListener,
    BottomNavigationView.OnNavigationItemSelectedListener {

    MainPagerAdapter mainPagerAdapter;

    private int previousPosition = -1;

    public static int MAIN_POSITION = 0;

    private boolean isFullScreen;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initEvent() {
        QMUIStatusBarHelper.translucent(this);
        QMUIStatusBarHelper.setStatusBarLightMode(MainActivity.this);

        mainPagerAdapter = new MainPagerAdapter(getSupportFragmentManager());
        mBinding.bnve.enableItemShiftingMode(false);
        mBinding.bnve.enableShiftingMode(false);
        mBinding.bnve.enableAnimation(false);
        mBinding.vp.setAdapter(mainPagerAdapter);

        mBinding.vp.setOffscreenPageLimit(3);
        mBinding.vp.setScroll(false);
        mBinding.bnve.setupWithViewPager(mBinding.vp);

        appUpdate();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mBinding.bnve.setCurrentItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressedSupport() {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int index = menuItem.getItemId();
        int position = 0;
        switch (index) {
            case R.id.room:
                position = 0;
                break;
            case R.id.order:
                position = 1;
                break;
            case R.id.me:
                position = 2;
                break;
        }
        if (previousPosition != position) {
            mBinding.vp.setCurrentItem(position, false);
            previousPosition = position;
        }
        return true;
    }

    /**
     * app更新
     */
    private void appUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            showCenterToast(getString(R.string.cloud_phone_state_denied_msg));
            return;
        }

        AppUpdateRequest request = new AppUpdateRequest();
        request.setApptype("android");
        request.setPackageName("room_owner");
        getRepository(NetService.class).appUpdate(UrlConfig.APP_UPDATE, request.getRequestBody())
                .compose(dispatchSchedulers(false))
                .subscribe(new RepositorySubscriber<ResponseBean<AppUpdateResponse>>() {
                    @Override
                    protected void onResponse(ResponseBean<AppUpdateResponse> result) {
                        try {
                            if (result != null && result.getCode().equals(Constants.RESPONSE_SUCCEED)) {
                                AppUpdateResponse appUpdateResponse = result.getData();
                                int versionCode = Integer.valueOf(appUpdateResponse.getVersionCode());
                                String versionName = appUpdateResponse.getVersionName();
                                String apkUrl = appUpdateResponse.getDownloadUrl();
                                downloadNewApk(versionCode, versionName, apkUrl);
                            } else {
                                String message = result.getMessage();
                                showCenterToast(message);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable t) {
                        super.onError(t);
//                        showCenterToast(getString(R.string.network_failed));
                    }
                });
    }

    /**
     * 下载新的apk
     */
    private void downloadNewApk(final int versionCode, final String versionName, final String apkUrl){
        UpdateConfiguration configuration = new UpdateConfiguration()
                //输出错误日志
                .setEnableLog(true)
                //设置自定义的下载
                //.setHttpManager()
                //下载完成自动跳动安装页面
                .setJumpInstallPage(true)
                //设置对话框背景图片 (图片规范参照demo中的示例图)
                .setDialogImage(R.mipmap.ic_update)
                //设置按钮的颜色
                .setDialogButtonColor(Color.parseColor("#455cc7"))
                //设置对话框强制更新时进度条和文字的颜色
                .setDialogProgressBarColor(Color.parseColor("#455cc7"))
                //设置按钮的文字颜色
                .setDialogButtonTextColor(Color.WHITE)
                //设置是否显示通知栏进度
                .setShowNotification(true)
                //设置是否提示后台下载toast
                .setShowBgdToast(false)
                //设置强制更新
                .setForcedUpgrade(true);

        DownloadManager manager = DownloadManager.getInstance(this);
        manager.setApkName("owner.apk")
                .setApkUrl(apkUrl)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setShowNewerToast(true)
                .setConfiguration(configuration)
                .setApkVersionCode(versionCode)
                .setApkVersionName(versionName)
//                                .setApkSize("20.4")
                .setApkDescription(getString(R.string.app_update_msg))
                .download();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTabJumpEvent(TabJumpEvent event) {
        mBinding.vp.setCurrentItem(event.getPositon(), true);
        if (event.isRefreshContent()) {
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            if (!fragmentList.isEmpty()) {
                ISupportFragment fragment = (ISupportFragment) fragmentList.get(event.getPositon());
                if (fragment != null) {
                    fragment.onSupportVisible();
                }
            }
        }
    }
}
