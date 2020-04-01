package freaktemplate.kingburger.observableLayer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "cartFav",indices = {@Index(value = {"itemId","itemCategoryId"},unique = true)})
public class CartFav {

    @PrimaryKey(autoGenerate = true)
    private int Id;

    @ColumnInfo(name = "itemId")
    private int itemId;

    @ColumnInfo(name = "itemName")
    private String itemName;

    @ColumnInfo(name = "itemDescription")
    private String itemDescription;

    @ColumnInfo(name = "itemPrice")
    private double itemPrice;

    @ColumnInfo(name = "itemCategoryId")
    private int itemCategoryId;

    @ColumnInfo(name = "itemImageId")
    private String itemImageId;

    @ColumnInfo(name = "isFav")
    private int isFav;

    private int itemQuantity;

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public double getItemPrice()
    {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getIsFav() {
        return isFav;
    }

    public void setIsFav(int isFav) {
        this.isFav = isFav;
    }

    public int getItemCategoryId() {
        return itemCategoryId;
    }

    public void setItemCategoryId(int itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
    }

    public String getItemImageId() {
        return itemImageId;
    }

    public void setItemImageId(String itemImageId) {
        this.itemImageId = itemImageId;
    }
}