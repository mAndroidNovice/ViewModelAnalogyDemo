package wyw.net.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import wyw.net.myapplication.persistence.UserSource;
import wyw.net.myapplication.ui.UserViewModel;
import wyw.net.myapplication.ui.ViewModelFactory;


public class MainActivity extends AppCompatActivity {

    private TextView tvName, tvAge;
    private EditText etName, etAge;
    private ViewModelFactory viewModelFactory;
    private UserViewModel userViewModel;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvName = findViewById(R.id.tv_name);
        tvAge = findViewById(R.id.tv_age);
        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        viewModelFactory = new ViewModelFactory(new UserSource());

        userViewModel = new ViewModelProvider(this, viewModelFactory).get(UserViewModel.class);
        findViewById(R.id.bt_upload).setOnClickListener(view -> {
            view.setEnabled(false);
            String userName = etName.getText().toString();
            int age;
            try {
                age = Integer.parseInt(etAge.getText().toString());
            } catch (Exception e) {
                age = 0;
            }
            disposable.add(userViewModel.insertName(userName, age)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> view.setEnabled(true), throwable -> Log.e("MainActivity", "throw ", throwable)));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        disposable.add(userViewModel.getUserName()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.e("MainActivity", s);
                    tvName.setText(s);
                }));
        disposable.add(userViewModel.getUserAge()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    Log.e("MainActivity", String.valueOf(s));
                    tvAge.setText(String.valueOf(s));
                }));
    }

    @Override
    protected void onStop() {
        super.onStop();
        disposable.clear();
    }
}
