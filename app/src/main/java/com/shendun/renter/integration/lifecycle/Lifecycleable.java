package com.shendun.renter.integration.lifecycle;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.trello.rxlifecycle4.RxLifecycle;

import com.shendun.renter.utils.RxLifecycleUtils;

import io.reactivex.rxjava3.subjects.Subject;

/**
 * ================================================
 * 让 {@link Activity}/{@link Fragment} 实现此接口,即可正常使用 {@link RxLifecycle}
 * 无需再继承 {@link RxLifecycle} 提供的 Activity/Fragment ,扩展性极强
 *
 * @see RxLifecycleUtils 详细用法请查看此类
 * ================================================
 */
public interface Lifecycleable<E> {
    @NonNull
    Subject<E> provideLifecycleSubject();
}
