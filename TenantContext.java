public class TenantContext {
    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static AutoCloseable setTenantId(String tenantId) {
        context.set(tenantId);
        return () -> context.remove();
    }

    public static String getTenantId() {
        return context.get();
    }
}
