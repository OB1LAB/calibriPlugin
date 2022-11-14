package ob1lab.calibri.Cart;

public class CartItem {
    public String itemName;
    public String itemViewName;
    public String itemId;
    public String itemType;
    public int amount;
    public int stackSize;

    public CartItem(String itemName, String itemViewName, String itemType, String itemId, int amount, int stackSize) {
        this.itemName = itemName;
        this.itemViewName = itemViewName;
        this.itemType = itemType;
        this.itemId = itemId;
        this.amount = amount;
        this.stackSize = stackSize;
    }
}
