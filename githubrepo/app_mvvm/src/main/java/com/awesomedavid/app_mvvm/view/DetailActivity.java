package com.awesomedavid.app_mvvm.view;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.awesomedavid.app_mvvm.R;
import com.awesomedavid.app_mvvm.contract.DetailViewContract;
import com.awesomedavid.app_mvvm.databinding.ActivityDetailBinding;
import com.awesomedavid.app_mvvm.model.GitHubService;
import com.awesomedavid.app_mvvm.viewmodel.DetailViewModel;

/**
 * 상세 화면을 표시하는 액티비티
 */

public class DetailActivity extends AppCompatActivity implements DetailViewContract {
    private static final String EXTRA_FULL_REPOSITORY_NAME = "EXTRA_FULL_REPOSITORY_NAME";
    private String fullRepoName;

    /**
     * DetailActivity를 시작하는 메소드
     * @param fullRepositoryName 표시하고 싶은 리포지토리 이름(google/iosched 등)
     */
    public static void start(Context context, String fullRepositoryName) {
        final Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(EXTRA_FULL_REPOSITORY_NAME, fullRepositoryName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //final ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        final ActivityDetailBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        final GitHubService gitHubService = ((NewGitHubReposApplication)getApplication()).getGitHubService();
        final DetailViewModel detailViewModel = new DetailViewModel((DetailViewContract)this , gitHubService);
        binding.setViewModel(detailViewModel);

        final Intent intent = getIntent();
        fullRepoName = intent.getStringExtra(EXTRA_FULL_REPOSITORY_NAME);
        detailViewModel.loadRepositories();

    }

    @Override
    public String getFullRepositoryName() {
        return fullRepoName;
    }

    @Override
    public void startBrowser(String url) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public void showError(String message) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                .show();
    }
}
