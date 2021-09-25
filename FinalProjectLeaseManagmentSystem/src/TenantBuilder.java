public class TenantBuilder {
    import java.util.Objects;


import java.util.function.Consumer;



    public final class TenantBuilder<T> {

        private ConditionalConsumer<Subscription> onSubscribe;

        private ConditionalConsumer<? super T> onPublish;

        private Consumer<CompletionType> onComplete;

        TenantBuilder() {
        }

        public TenantBuilder<T> onSubscribe(ConditionalConsumer<Subscription> onSubscribe) {
            Objects.requireNonNull(onSubscribe);
            this.onSubscribe = onSubscribe;
            return this;
        }

        public TenantBuilder<T> onSubscribe(Consumer<Subscription> onSubscribe) {
            Objects.requireNonNull(onSubscribe);
            this.onSubscribe = object -> {
                onSubscribe.accept(object);
                return true;
            };
            return this;
        }

        public TenantBuilder<T> onPublish(Consumer<? super T> onPublish) {
            Objects.requireNonNull(onPublish);
            this.onPublish = object -> {
                onPublish.accept(object);
                return true;
            };
            return this;
        }

        public TenantBuilder<T> onPublishConditional(ConditionalConsumer<? super T> onPublish) {
            Objects.requireNonNull(onPublish);
            this.onPublish = onPublish;
            return this;
        }

        public TenantBuilder<T> onComplete(Consumer<CompletionType> onComplete) {
            Objects.requireNonNull(onComplete);
            this.onComplete = onComplete;
            return this;
        }

        public Tenant<T> build() {
            return new StaticTenant<>(onSubscribe, onPublish, onComplete);
        }
    }
}
