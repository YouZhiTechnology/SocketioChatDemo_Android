package com.youzhi.chatdemo.utils;


import java.util.List;

import io.reactivex.Observer;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author gaoyuheng
 * @description: 简单的异步工具类封装，主要实现线程切换功能，需要用户自行构建数据，返回数据处理
 * @date :2020/9/28 15:19
 */
public class RxJavaUtils {

    /**
     * 执行异步的方法
     *
     * @param simpleListener 构建数据，接受数据结果的回调
     * @param param
     * @param <T>
     */
    public static <T, D> void executeAsync(final SimpleListener<T, D> simpleListener, final D... param) {


        Observable<T> tObservable = Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) {
                try {
                    emitter.onNext(simpleListener.buildData(param));
                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        });


        tObservable.subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {


            @Override
            public void accept(Disposable disposable) {
                if (simpleListener != null) {
                    simpleListener.rxDoOnSubscribe(disposable);
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<T>() {


            @Override
            public void onSubscribe(Disposable d) {


            }

            @Override
            public void onNext(T data) {
                if (simpleListener != null) {
                    simpleListener.rxSuccess(data);
                }

            }

            @Override
            public void onError(Throwable e) {
                if (simpleListener != null) {
                    simpleListener.rxError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        });
    }

//    public static class FunctionImp<P, T> implements Function<P, T> {
//        private RxListListener<T, P> mRxListListener;
//
//        public FunctionImp(RxListListener<T, P> rxListListener) {
//            mRxListListener = rxListListener;
//
//
//        }
//
//        @Override
//        public T apply(P p) throws Exception {
//            T dataResult = mRxListListener.buildData(p);
//            //RxJava2.0后结果如果返回null会导致异常，其他全部事件发送中断问题
//            if (dataResult == null) {
//
//                return TUtil.getT(this, 1);
//            }
//
//            return dataResult;
//
//        }
//    }


    public static class FunctionImp<P, T> implements Function<P, T> {

        RxListListener<T, P> mRxListListener;

        public FunctionImp(RxListListener<T, P> rxListListener) {
            mRxListListener = rxListListener;

        }

        @Override
        public T apply(P p) throws Exception {

            //RxJava2.0后结果如果返回null会导致异常，其他全部事件发送中断问题


            return mRxListListener.buildData(p);


        }
    }


    /*试用数组异步方式*/
    public static <T, P> void executeAsyncList(final RxListListener<T, P> rxListListener, List<P> list) {
        if (rxListListener == null) {
            return;
        }

        Observable.fromIterable(list).map(new Function<P, T>() {
            @Override
            public T apply(P p) throws Exception {


                T dataResult = rxListListener.buildData(p);

                return dataResult;
            }
        }).subscribeOn(Schedulers.io()).doOnSubscribe(new Consumer<Disposable>() {


            @Override
            public void accept(Disposable disposable) {

                rxListListener.rxDoOnSubscribe(disposable);

            }
        }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<T>() {


            @Override
            public void onSubscribe(Disposable d) {


            }

            @Override
            public void onNext(T data) {

                rxListListener.rxSuccess(data);

            }

            @Override
            public void onError(Throwable e) {
                if (rxListListener != null) {
                    rxListListener.rxError(e);
                }
            }

            @Override
            public void onComplete() {

            }
        });


    }


    public interface SimpleListener<T, D> {
        //耗时操作的方法 （对应Schedulers.io()线程，好处可以复用，newThread 方式消耗cpu）
        T buildData(D... param);

        //返回成功的结果，（主线程）
        void rxSuccess(T data);

        //报错方法  （主线程）
        void rxError(Throwable e);

        //在耗时方法开始前做一些事情比如加载窗（主线程）
        void rxDoOnSubscribe(Disposable disposable);


    }

    public interface RxListListener<T, P> {
        //耗时操作的方法 （对应Schedulers.io()线程，好处可以复用，newThread 方式消耗cpu）
        T buildData(P param);

        //返回成功的结果，（主线程）
        void rxSuccess(T data);

        //报错方法  （主线程）
        void rxError(Throwable e);

        //在耗时方法开始前做一些事情比如加载窗（主线程）
        void rxDoOnSubscribe(Disposable disposable);


    }


}
