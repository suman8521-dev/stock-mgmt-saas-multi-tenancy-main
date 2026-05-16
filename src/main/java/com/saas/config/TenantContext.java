package com.saas.config;
/**
 * TenantContext - Stores the current tenant identifier in a ThreadLocal.
 *
 * Each HTTP request is processed by a dedicated thread.
 * ThreadLocal ensures that the tenant_id is isolated per thread,
 * even when multiple tenants send requests simultaneously.
 *
 * Flow:
 *    1. TenantFilter extracts the tenant_id from the HTTP request
 *    2. TenantFilter calls TenantContext.setCurrentTenant(tenant_id)
 *    3. Business logic (services, repositories) accesses the tenant using
 *       TenantContext.getCurrentTenant()
 *    4. TenantFilter calls TenantContext.clear() after the response
 *       to clean up the thread data
 *
 */
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_SCHEMA = new ThreadLocal<>();


    /**
     * Sets the tenant identifier for the current thread.
     */
    public static void setCurrentTenant(final String tenant) {
        CURRENT_TENANT.set(tenant);
    }

    /**
     * Sets the database schema for the current thread.
     */
    public static void setCurrentSchema(final String schema) {
        CURRENT_SCHEMA.set(schema);
    }

    /**
     * Returns the tenant identifier for the current thread.
     */
    public static String getCurrentTenant() {
        return CURRENT_TENANT.get();
    }

    /**
     * Returns the schema for the current thread.
     */
    public static String getCurrentSchema() {
        return CURRENT_SCHEMA.get();
    }

    /**
     * Clears the tenant and schema information from the current thread.
     *
     * IMPORTANT:
     * This must always be called inside a finally block
     * to avoid:
     *   - memory leaks
     *   - tenant data leakage between HTTP requests
     */
    public static void clear() {
        CURRENT_TENANT.remove();
        CURRENT_SCHEMA.remove();
    }

}
