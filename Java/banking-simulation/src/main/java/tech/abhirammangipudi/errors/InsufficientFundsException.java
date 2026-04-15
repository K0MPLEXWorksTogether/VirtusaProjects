package tech.abhirammangipudi.errors;

public class InsufficientFundsException extends Exception {
    private final double currentBalance;

    public InsufficientFundsException(String message, double currentBalance) {
        super(message);
        this.currentBalance = currentBalance;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }
}
