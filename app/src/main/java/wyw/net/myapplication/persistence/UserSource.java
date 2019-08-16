package wyw.net.myapplication.persistence;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class UserSource {
    public static User user;
    private FlowableEmitter<String> emitter;
    private FlowableOnSubscribe<String> flowableSource = new FlowableOnSubscribe<String>() {
        @Override
        public void subscribe(FlowableEmitter<String> emitter) throws Exception {
            UserSource.this.emitter = emitter;
            if (user != null) {
                emitter.onNext(user.name);
            }
        }
    };
//    private Flowable flowable = ;

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
                this.emitter.onNext(user.name);
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
        return Flowable.create(flowableSource, BackpressureStrategy.DROP);
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
