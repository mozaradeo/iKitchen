package freaktemplate.kingburger.observableLayer;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;


@Entity(indices = {@Index(value = {"orderId"})})
public class CartItemTopping {

    @PrimaryKey(autoGenerate = true)
    private int Id;


    @ColumnInfo(name = "toppingId")
    private int toppingId;

    @ColumnInfo(name = "userId")
    private int userId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @ColumnInfo(name = "orderId")
    private int orderId;

    @ColumnInfo(name = "itemId")
    private int itemId;

    @ColumnInfo(name = "toppingName")
    private String toppingName;

    @ColumnInfo(name = "toppingPrice")
    private double toppingPrice;

    @ColumnInfo(name = "toppingType")
    private int type;

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    @Ignore
    private boolean isChecked;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getToppingName() {
        return toppingName;
    }

    public void setToppingName(String toppingName) {
        this.toppingName = toppingName;
    }

    public double getToppingPrice() {
        return toppingPrice;
    }

    public void setToppingPrice(double toppingPrice) {
        this.toppingPrice = toppingPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getToppingId() {
        return toppingId;
    }

    public void setToppingId(int toppingId) {
        this.toppingId = toppingId;
    }

}
//foreignKeys = @ForeignKey(entity = Cart.class, parentColumns = "Id", childColumns = "orderId"),
