package wyw.net.myapplication.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import wyw.net.myapplication.persistence.UserSource;


public class ViewModelFactory implements ViewModelProvider.Factory {
    private UserSource userSource;

    public ViewModelFactory(UserSource userSource) {
        this.userSource = userSource;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(userSource);
        }
        return null;
    }
}
