package com.awesomedavid.app_mvvm.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.awesomedavid.app_mvvm.R;
import com.awesomedavid.app_mvvm.contract.RepositoryListViewContract;
import com.awesomedavid.app_mvvm.databinding.ActivityRepositoryListBinding;
import com.awesomedavid.app_mvvm.model.GitHubService;
import com.awesomedavid.app_mvvm.viewmodel.RepositoryListViewModel;

public class RepositoryListActivity extends AppCompatActivity implements RepositoryListViewContract {

    private Spinner languageSpinner;
    private CoordinatorLayout coordinatorLayout;

    private RepositoryAdapter repositoryAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityRepositoryListBinding binding = DataBindingUtil.setContentView(this , R.layout.activity_repository_list);
        final GitHubService gitHubService = ((NewGitHubReposApplication)getApplication()).getGitHubService();
        binding.setViewModel(new RepositoryListViewModel((RepositoryListViewContract)this , gitHubService));

        // 뷰를 셋업
        setupViews();

    }

    /**
     * 목록 등 화면 요소를 만든다
     */
    private void setupViews() {
        // 툴바 설정
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Recycler View
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_repos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        repositoryAdapter = new RepositoryAdapter((Context) this, (RepositoryListViewContract) this);
        recyclerView.setAdapter(repositoryAdapter);

        // SnackBar 표시에서 이용한다
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        // Spinner
        languageSpinner = (Spinner) findViewById(R.id.language_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.addAll("java", "objective-c", "swift", "groovy", "python", "ruby", "c");
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);
    }

    @Override
    public void showRepositories(GitHubService.Repositories repositories) {
        repositoryAdapter.setItemsAndRefresh(repositories.items);
    }

    @Override
    public void showError() {
        Snackbar.make(coordinatorLayout, "읽을 수 없습니다", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void startDetailActivity(String full_name) {
        DetailActivity.start(this, full_name);

    }
}
