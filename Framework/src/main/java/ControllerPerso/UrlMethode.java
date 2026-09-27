package ControllerPerso;

public class UrlMethode {

    private final String url;
    private final String httpMethod;

    public UrlMethode(String url, String httpMethod) {
        this.url = url;
        this.httpMethod = httpMethod.toUpperCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UrlMethode)) return false;
        UrlMethode other = (UrlMethode) o;
        return this.url.equals(other.url) && this.httpMethod.equals(other.httpMethod);
    }

    @Override
    public int hashCode() {
        return (url + ":" + httpMethod).hashCode();
    }

    public String getUrl()        { return url; }
    public String getHttpMethod() { return httpMethod; }
}