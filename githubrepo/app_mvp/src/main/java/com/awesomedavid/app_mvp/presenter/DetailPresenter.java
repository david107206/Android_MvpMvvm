package com.awesomedavid.app_mvp.presenter;

import android.drm.DrmStore;

import com.awesomedavid.app_mvp.contract.DetailContract;
import com.awesomedavid.app_mvp.model.GitHubService;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailPresenter implements DetailContract.UserActions {
    final DetailContract.View detailView;
    private final GitHubService gitHubService;
    private GitHubService.RepositoryItem repositoryItem;


    public DetailPresenter(DetailContract.View detailView, GitHubService gitHubService) {
        this.detailView = detailView;
        this.gitHubService = gitHubService;
    }

    /**
     * 하나의 리포지토리에 관한 정보를 가져온다
     * 기본적으로 API 액세스 방법은 RepositoryListActivity#loadRepositories(String)와 같다
     */
    private void loadRepositories(){
        String fullRepoName = detailView.getFullRepositoryName();
        // 리포지토리 이름을 /로 분할한다
        final String[] repoData = fullRepoName.split("/");
        final String owner = repoData[0];
        final String repoName = repoData[1];

        gitHubService
                .detailRepo(owner , repoName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GitHubService.RepositoryItem>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GitHubService.RepositoryItem response) {
                        repositoryItem = response;
                        detailView.showRepositoryInfo(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        detailView.showError("읽을 수 없습니다.");
                    }

                    @Override
                    public void onComplete() {
                        // 아무것도 하지 않는다
                    }
                });

    }

    @Override
    public void titleClick() {
        try{
            detailView.startBrowser(repositoryItem.html_url);
        }catch (Exception e){
            detailView.showError("링크를 열 수 없습니다.");
        }
    }

    @Override
    public void prepare() {
        loadRepositories();
    }
}
