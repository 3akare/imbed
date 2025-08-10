public class ImbedValue {
    long ttl;
    String value;

    public ImbedValue(long ttl, String value) {
        this.ttl = ttl;
        this.value = value;
    }

    public long getTTL() {
        return this.ttl;
    }

    public void setTTL(long ttl) {
        this.ttl = ttl;
    }

    public boolean isExpired() {
        return (ttl != -1 && ttl < System.currentTimeMillis());
    }
}
