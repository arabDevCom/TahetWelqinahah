package com.lost_found_it.uis.activity_home.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.lost_found_it.R;
import com.lost_found_it.adapter.MyAdAdapter;
import com.lost_found_it.adapter.SearchAdapter;
import com.lost_found_it.databinding.FragmentNotificationBinding;
import com.lost_found_it.databinding.FragmentSearchBinding;
import com.lost_found_it.model.AdModel;
import com.lost_found_it.mvvm.FragmentHomeMvvm;
import com.lost_found_it.mvvm.FragmentSearchMvvm;
import com.lost_found_it.mvvm.GeneralMvvm;
import com.lost_found_it.tags.Tags;
import com.lost_found_it.uis.activity_base.BaseFragment;
import com.lost_found_it.uis.activity_home.HomeActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FragmentSearch extends BaseFragment {
    private GeneralMvvm generalMvvm;
    private FragmentSearchBinding binding;
    private HomeActivity activity;
    private FragmentSearchMvvm mvvm;
    private SearchAdapter searchAdapter;
    private CompositeDisposable disposable = new CompositeDisposable();


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = (HomeActivity) context;
    }

    public static FragmentSearch newInstance() {
        return new FragmentSearch();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        generalMvvm = ViewModelProviders.of(activity).get(GeneralMvvm.class);
        mvvm = ViewModelProviders.of(this).get(FragmentSearchMvvm.class);
        binding.setLang(getLang());
        Observable.timer(10, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        loadUiData();
                    }
                });

    }

    private void loadUiData() {
        mvvm.getIsLoading().observe(activity,isLoading->{
            binding.recViewLayout.swipeRefresh.setRefreshing(isLoading);
        });

        mvvm.getOnSearchSuccess().observe(activity, adModels -> {
            if (adModels!=null&&adModels.size()>0){
                binding.recViewLayout.tvNoData.setVisibility(View.GONE);
            }else {
                binding.recViewLayout.tvNoData.setVisibility(View.VISIBLE);
            }
            searchAdapter.updateList(adModels);
        });

        binding.edtSearch.requestFocus();
        binding.imageBack.setOnClickListener(v -> {
            generalMvvm.getMainNavigationBackPress().setValue(true);
        });
        mvvm.search(getUserSetting().getCountry(),binding.edtSearch.getText().toString());
        binding.recViewLayout.swipeRefresh.setOnRefreshListener(() -> mvvm.search(getUserSetting().getCountry(),binding.edtSearch.getText().toString()));

        binding.edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mvvm.search(getUserSetting().getCountry(),s.toString());
            }
        });

        searchAdapter = new SearchAdapter(activity, this, getLang());
        binding.recViewLayout.recView.setLayoutManager(new LinearLayoutManager(activity));
        binding.recViewLayout.recView.setHasFixedSize(true);
        binding.recViewLayout.recView.setDrawingCacheEnabled(true);
        binding.recViewLayout.recView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        binding.recViewLayout.recView.setItemViewCacheSize(20);
        binding.recViewLayout.recView.setAdapter(searchAdapter);
        binding.recViewLayout.swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

    }

    public void navigateToDetails(AdModel adModel) {
        generalMvvm.getOnAdDetailsSelected().setValue(adModel.getId());
        generalMvvm.getMainNavigation().setValue(Tags.fragment_ad_details_pos);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposable.clear();
    }
}