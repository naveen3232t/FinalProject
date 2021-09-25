public class Tenant{

    boolean onTenant(Subscription subscription);

    boolean onPublish(T item);

    void onComplete(CompletionType completionType);

    static <T> TenantBuilder<T> builder() {
        return new TenantBuilder<>();
    }
}{
}
