package wyw.net.myapplication.rx;

import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

public class FlowableSource<T> {
    public FlowableOnSubscribe<T> subscribe;
    public FlowableEmitter<T> emitter;

    public FlowableSource(SourceEmitterAction<T> action) {
        subscribe = emitter -> {
            this.emitter = emitter;
            if (action != null) {
                action.emit(emitter);
            }
        };
    }

    public interface SourceEmitterAction<T> {
        void emit(FlowableEmitter<T> emitter);
    }
}