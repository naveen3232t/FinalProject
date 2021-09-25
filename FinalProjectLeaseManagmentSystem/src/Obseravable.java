

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

    public interface Observable<T> {


        default void subscribe(Consumer<? super T> consumer) {
            subscribe(Subscriber.<T>builder().onPublish(consumer).build());
        }


        default void conditionalSubscribe(ConditionalConsumer<? super T> consumer) {
            subscribe(Subscriber.<T>builder().onPublishConditional(consumer).build());
        }

        default void subscribe(SubscriberBuilder<? super T> subscriberBuilder) {
            subscribe(subscriberBuilder.build());
        }


        default void subscribeOnComplete(Consumer<CompletionType> onComplete) {
            subscribe(Subscriber.<T>builder().onComplete(onComplete).build());
        }

        void subscribe(Subscriber<? super T> subscriber);


        @Deprecated
        default void await() {
            await(Duration.ofMillis(-1));
        }

        default void await(Duration timeout) {
            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean isTimout = new AtomicBoolean(false);

            long millis = timeout.toMillis();

            Observable.this.subscribe(Subscriber.builder().onComplete(completionType -> {
                isTimout.compareAndSet(false, true);
                latch.countDown();
            }));

            if (millis < 0) {
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new ThreadInterruptedException();
                }
            } else {
                boolean normally;
                try {
                    normally = latch.await(millis, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new ThreadInterruptedException();
                }
                if (!normally) {
                    boolean changed = isTimout.compareAndSet(false, true);
                    if (changed) {
                        throw new TimeoutException(String.format("The observable not completed in %sms", millis));
                    }
                }
            }

