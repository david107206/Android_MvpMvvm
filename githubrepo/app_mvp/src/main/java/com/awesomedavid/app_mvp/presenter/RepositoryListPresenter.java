package com.awesomedavid.app_mvp.presenter;

import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.view.View;

import com.awesomedavid.app_mvp.contract.RepositoryListContract;
import com.awesomedavid.app_mvp.model.GitHubService;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * MVP의 Presenter 역할을 하는 클래스
 */
public class RepositoryListPresenter implements RepositoryListContract.UserActions {
    private final RepositoryListContract.View repositoryListView;
    private  final GitHubService gitHubService;


    public RepositoryListPresenter(RepositoryListContract.View repositoryListView , GitHubService gitHubService) {
        // RepositoryListContract.View로서 멤버 변수에 저장한다
        this.repositoryListView = repositoryListView;
        this.gitHubService = gitHubService;;
    }

    @Override
    public void selectLanguage(String language) {
        loadrepositories();
    }

    @Override
    public void selectRepositoryItem(GitHubService.RepositoryItem item) {
        repositoryListView.startDetailActivity(item.full_name);
    }

    /**
     * 지난 일주일간 만들어진 라이브러리의 인기순으로 가져온다
     */
    private void loadrepositories() {
        //  로딩 중이므로 진행바를 표시한다
        repositoryListView.showProgress();

        // 일주일 전 날짜 문자열 지금이 2016-10-27이면 2016-10-20 이라는 문자열을 얻는다
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        String text = DateFormat.format("yyyy-MM-dd", calendar).toString();

        Observable<GitHubService.Repositories> observable = gitHubService.listRepos("language:" + repositoryListView.getSelectedLanguage()
                +" " + "created:>" + text );

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GitHubService.Repositories>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GitHubService.Repositories repositories) {
                        //  로딩을 마쳤으므로 진행바 표시를 하지 않는다
                        repositoryListView.hideProgress();
                        //  가져온 아이템을 표시하기 위해, RecyclerView에 아이템을 설정하고 갱신한다
                        repositoryListView.showRepositories(repositories);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // 통신 실패 시에 호출된다
                        // 여기서는 스낵바를 표시한다(아래에 표시되는 바)
                        repositoryListView.showError();
                    }

                    @Override
                    public void onComplete() {
                        // 아무것도 하지 않는다
                    }

                });
    }

}
