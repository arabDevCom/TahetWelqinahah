package com.lost_found_it.mvvm;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.lost_found_it.model.AdModel;
import com.lost_found_it.model.AdsDataModel;
import com.lost_found_it.model.CategoryDataModel;
import com.lost_found_it.model.CategoryModel;
import com.lost_found_it.model.CityDataModel;
import com.lost_found_it.model.CityModel;
import com.lost_found_it.model.StatusResponse;
import com.lost_found_it.model.SubCategoryModel;
import com.lost_found_it.model.UserModel;
import com.lost_found_it.remote.Api;
import com.lost_found_it.tags.Tags;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class FragmentAddAdsMvvm extends AndroidViewModel {
    private final String TAG = FragmentAddAdsMvvm.class.getName();
    public MutableLiveData<List<CategoryModel>> onCategoryDataSuccess;
    public MutableLiveData<List<CityModel>> onCityDataSuccess;


    private CompositeDisposable disposable = new CompositeDisposable();

    public FragmentAddAdsMvvm(@NonNull Application application) {
        super(application);
    }
    public MutableLiveData<List<CategoryModel>> getOnCategoryDataSuccess() {
        if (onCategoryDataSuccess == null) {
            onCategoryDataSuccess = new MutableLiveData<>();
        }
        return onCategoryDataSuccess;
    }
    public MutableLiveData<List<CityModel>> getOnCityDataSuccess() {
        if (onCityDataSuccess == null) {
            onCityDataSuccess = new MutableLiveData<>();
        }
        return onCityDataSuccess;
    }
    public void getCategories(String country){

        Api.getService(Tags.base_url)
                .getCategoryWithSubCategory(country)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<CategoryDataModel>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(Response<CategoryDataModel> response) {

                        if (response.isSuccessful()){

                            if (response.body()!=null){

                                getOnCategoryDataSuccess().setValue(response.body().getData());

                            }

                        }else {
                            try {
                                Log.e(TAG,response.errorBody().string()+"___"+response.body().getMessage().toString());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        Log.e(TAG,e.getMessage());
                    }
                });
    }
    public void getCities(String country, String lang) {
        Api.getService(Tags.base_url)
                .getCities(country, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Response<CityDataModel>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onSuccess(@NonNull Response<CityDataModel> response) {

                        if (response.isSuccessful()) {
                            if (response.body() != null) {
                                if (response.body().getCode() == 200) {
                                    getOnCityDataSuccess().setValue(response.body().getData());
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: ", e);

                    }
                });

    }

}
