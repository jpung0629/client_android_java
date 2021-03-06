package kr.co.bootpay.analytics;

import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import kr.co.bootpay.model.StatCall;
import kr.co.bootpay.model.StatItem;
import kr.co.bootpay.secure.BootpaySimpleAES256;
import kr.co.bootpay.model.StatLogin;
import kr.co.bootpay.pref.UserInfo;

public class AnalyticsPresenter {
    AnalyticsService service;
//    Scheduler thread;
    private final String ver = "3.0.5";

    public AnalyticsPresenter(AnalyticsService service) {
        this.service = service;
//        this.thread = Schedulers.from(Executors.newCachedThreadPool());
    }

    public void login(String id,
                      String email,
                      String username,
                      int gender,
                      String birth,
                      String phone,
                      String area) {
        StatLogin login = new StatLogin(
                ver,
                UserInfo.getInstance(this.service.getContext()).getBootpayApplicationId(),
                id,
                email,
                username,
                gender,
                birth,
                phone,
                area
        );

        String json = new Gson().toJson(login);
        BootpaySimpleAES256 aes = new BootpaySimpleAES256();

        service.getApi()
                .login(
                        aes.strEncode(json),
                        aes.getSessionKey()
                )
                .retry(3)
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(
                        new Observer<LoginResult>() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();

                            }

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(LoginResult res) {
                                if(res.getData() != null)
                                    UserInfo.getInstance(service.getContext()).setBootpayUserId(res.getData().getUserId());
                            }
                        });


//        service.getApi()
//                .login(
//                        aes.strEncode(json),
//                        aes.getSessionKey()
//                )
//                .retry(3)
//                .subscribeOn(thread)
//                .subscribe(res -> {
//                            if(res.getData() != null)
//                                UserInfo.getInstance(service.getContext()).setBootpayUserId(res.getData().getUserId());
//                }, Throwable::printStackTrace);
    }

    public void call(String url,
                     String page_type,
                     List<StatItem> items) {
        StatCall call = new StatCall(
                ver,
                UserInfo.getInstance(this.service.getContext()).getBootpayApplicationId(),
                UserInfo.getInstance(this.service.getContext()).getBootpayUuid(),
                url,
                page_type,
                items,
                UserInfo.getInstance(this.service.getContext()).getBootpaySk(),
                UserInfo.getInstance(this.service.getContext()).getBootpayUserId(),
                "");


        String json = new Gson().toJson(call);
        BootpaySimpleAES256 aes = new BootpaySimpleAES256();

        service.getApi()
                .call(
                        aes.strEncode(json),
                        aes.getSessionKey()
                )
                .retry(3)
                .subscribeOn(Schedulers.from(Executors.newCachedThreadPool()))
                .subscribe(
                        new Observer<LoginResult>() {
                            @Override
                            public void onComplete() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();

                            }

                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(LoginResult res) {
                                Log.d("BootpayAnalytics", "call");
                            }
                        });

//                .subscribe(res -> Log.d("BootpayAnalytics", "call"),
//                        Throwable::printStackTrace);

    }

}
