package com.awesomedavid.app_mvvm.viewmodel;

import android.databinding.ObservableInt;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;

import com.awesomedavid.app_mvvm.contract.RepositoryListViewContract;
import com.awesomedavid.app_mvvm.model.GitHubService;

import java.util.Calendar;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * MVVM의 ViewModel 역할을 하는 클래스
 */
public class RepositoryListViewModel {
    public final ObservableInt progressBarVisibility = new ObservableInt(View.VISIBLE);
    private final RepositoryListViewContract repositoryListView;
    private final GitHubService gitHubService;

    public RepositoryListViewModel(RepositoryListViewContract repositoryListView, GitHubService gitHubService) {
        this.repositoryListView = repositoryListView;
        this.gitHubService = gitHubService;
    }

    public void onLanguageSpinnerItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //  스피너의 선택 내용이 바뀌면 호출된다
        loadRepositories((String) parent.getItemAtPosition(position));
    }

    /**
     * 지난 일주일간 만들어진 라이브러리를 인기순으로 가져온다
     */
    private void loadRepositories(String langugae) {
        // 로딩 중이므로 진행바를 표시한다
        progressBarVisibility.set(View.VISIBLE);

        // 일주일 전 날짜 문자열 지금이 2016-10-27이라면 2016-10-20이라는 문자열을 얻는다
        final Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        String text = DateFormat.format("yyyy-MM-dd", calendar).toString();

        // Retrofit을 이용해 서버에 액세스한다

        // 지난 일주일간 만들어졌고 언어가 language인 것을 쿼리로 전달한다
        Observable<GitHubService.Repositories> observable = gitHubService.listRepos("language:" + langugae + " " + "created:>" + text);

        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<GitHubService.Repositories>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(GitHubService.Repositories repositories) {
                        // 로딩이 끝났으므로 진행바를 표시하지 않는다
                        progressBarVisibility.set(View.GONE);
                        // 가져온 아이템을 표시하고자, RecyclerView에 아이템을 설정해 갱신한다
                        repositoryListView.showRepositories(repositories);

                    }

                    @Override
                    public void onError(Throwable e) {
                        // 통신에 실패하면 호출된다
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
