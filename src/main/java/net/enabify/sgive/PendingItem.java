package net.enabify.sgive;

public class PendingItem {
    private final String itemSpec;
    private final int amount;

    public PendingItem(String itemSpec, int amount) {
        this.itemSpec = itemSpec;
        this.amount = amount;
    }

    public String getItemSpec() {
        return itemSpec;
    }

    public int getAmount() {
        return amount;
    }
}
