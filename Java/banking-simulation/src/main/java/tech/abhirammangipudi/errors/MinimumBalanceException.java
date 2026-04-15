package tech.abhirammangipudi.errors;

public class MinimumBalanceException extends Exception {
    private final double minimumBalance;

    public MinimumBalanceException(String message, double minimumBalance) {
        super(message);
        this.minimumBalance = minimumBalance;
    }

    public double getMinimumBalance() {
        return minimumBalance;
    }

}
