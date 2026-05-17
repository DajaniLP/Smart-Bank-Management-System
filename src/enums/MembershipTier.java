package enums;

public enum MembershipTier {

    REGULAR(5000, 2000),
    PREMIUM(15000, 6000),
    VIP(50000, 20000);

    private final double withdrawalLimit;
    private final double transferLimit;

    MembershipTier(double withdrawalLimit, double transferLimit) {
        this.withdrawalLimit = withdrawalLimit;
        this.transferLimit = transferLimit;
    }

    public double getWithdrawalLimit() {
        return withdrawalLimit;
    }

    public double getTransferLimit() {
        return transferLimit;
    }
}