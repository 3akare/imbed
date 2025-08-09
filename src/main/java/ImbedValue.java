public class ImbedValue {
    long ttl;
    String value;

    public ImbedValue(long ttl, String value) {
        this.ttl = ttl;
        this.value = value;
    }

    public boolean isExpired() {
        return (ttl != -1 && ttl < System.currentTimeMillis());
    }
}
