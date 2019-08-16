package wyw.net.myapplication.ui;

import androidx.lifecycle.ViewModel;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import wyw.net.myapplication.persistence.User;
import wyw.net.myapplication.persistence.UserSource;

public class UserViewModel extends ViewModel {
    private User user;
    private UserSource userSource;

    public UserViewModel(UserSource userSource) {
        this.userSource = userSource;
    }

    public Flowable<String> getUserName() {
        return userSource.getUserName();
    }

    public Flowable<Integer> getUserAge() {
        return userSource.getUserAge();
    }


    public Completable insertName(String username, int age) {
//        user = user == null ? new User(username, age) :
//                new User(username, age);
        user = new User(username, age);
        return userSource.insertUser(user);
    }
}
