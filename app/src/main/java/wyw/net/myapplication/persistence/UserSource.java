package wyw.net.myapplication.persistence;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import wyw.net.myapplication.rx.FlowableSource;

public class UserSource {
    public static User user;
    //todo 可以使用map  然后代码编译时自动生成
    private FlowableSource<String> source;

    /**
     * 模拟room 存储数据
     *
     * @param user
     * @return
     */
    public Completable insertUser(User user) {
        //数据存储 数据库等              TimeUnit.SECONDS.sleep(1);
        UserSource.user = user;
        return Completable.create(emitter -> {
            try {
                emitter.onComplete();
                if (source != null) {
                    //数据修改通知 通知页面修改
                    source.emitter.onNext(user.name);
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

    /**
     * 模拟room 存储数据 然后通知
     *
     * @return
     */
    public Flowable<String> getUserName() {
        if (source == null) {
            source = new FlowableSource<>(emitter -> {
                if (user != null) {
                    emitter.onNext(user.name);
                }
            });
        }
        return Flowable.create(source.subscribe, BackpressureStrategy.DROP);
    }


    public Flowable<Integer> getUserAge() {
        return Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                //从数据库之类读取
                if (user != null) {
                    emitter.onNext(user.age);
                }
            }
        }, BackpressureStrategy.DROP);
    }
}
