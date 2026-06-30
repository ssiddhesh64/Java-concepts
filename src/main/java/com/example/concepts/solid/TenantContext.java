package com.example.concepts.solid;

public class TenantContext {
    private static final ThreadLocal<String> context = new ThreadLocal<>();

    public static AutoCloseable setTenantId(String tenantId) {
        context.set(tenantId);
        return () -> context.remove();
    }

    public static String getTenantId() {
        return context.get();
    }

    public static void main(String[] args) {
        Runnable task = () -> {
            try (AutoCloseable ctx = TenantContext.setTenantId("tenant-123")) {
                System.out.println("Current Tenant ID: " + TenantContext.getTenantId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        task.run();
    }

}