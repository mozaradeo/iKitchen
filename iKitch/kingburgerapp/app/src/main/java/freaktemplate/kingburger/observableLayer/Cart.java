package freaktemplate.kingburger.observableLayer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "cart",indices = {@Index(value = {"itemId","itemCategoryId"})})
public class Cart {

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

    @ColumnInfo(name = "itemPriceWithTopping")
    private double itemPriceWithTopping;

    @ColumnInfo(name = "itemCategoryId")
    private int itemCategoryId;

    @ColumnInfo(name = "itemQuantity")
    private int itemQuantity;

   // private String img;

  public String getImg_url() {
        return "";
    }

  //  public void setImg_url(String img_url) {
    //    this.img_url = img_url;
    //}

    //private String img_url;

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public double getItemPriceWithTopping() {
        return itemPriceWithTopping;
    }

    public void setItemPriceWithTopping(double itemPriceWithTopping) {
        this.itemPriceWithTopping = itemPriceWithTopping;
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

    public int getItemCategoryId() {
        return itemCategoryId;
    }

    public void setItemCategoryId(int itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

   /* public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
*/

}