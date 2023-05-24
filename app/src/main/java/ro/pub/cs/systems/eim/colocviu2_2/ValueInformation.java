package ro.pub.cs.systems.eim.colocviu2_2;

public class ValueInformation {

    private String value;
    private boolean isPrime;

    public ValueInformation(String value) {
        this.value = null;
        this.isPrime = false;
    }

    public ValueInformation(String value, boolean isPrime) {
        this.value = value;
        this.isPrime = isPrime;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getInformation() {
        return value;
    }

    public void setIsPrime(boolean isPrime) {
        this.isPrime = isPrime;
    }

    public boolean getIsPrime() {
        return isPrime;
    }

    @Override
    public String toString() {
        return value + " " + isPrime;
    }
}
