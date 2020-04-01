package freaktemplate.kingburger.observableLayer;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CartDao {
    ///////////////////query for cart///////////////////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItem(Cart carts);
    @Delete
    void delete(Cart cart);
    @Query("DELETE FROM Cart WHERE Id = :id")
    void deleteCartItem(int id);
    @Query("SELECT * FROM cart")
    LiveData<List<Cart>> getAll();
    @Query("SELECT COUNT(*) from cart")
    int countItems();
    @Query("DELETE FROM Cart")
    void emptyCart();
    @Query("SELECT MAX(Id) as Id FROM cart")
    int getRowId();
    @Query("SELECT COUNT(*) FROM cart ")
    int getCount();
    ///////////////////////query for cartFav///////////////////////

    @Query("SELECT isFav FROM cartFav WHERE itemCategoryId = :itemCategoryId AND itemId = :itemId")
    int isFav(int itemCategoryId, int itemId);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFavItem(CartFav... cartsFav);
    @Query("SELECT * FROM cartFav WHERE isFav=1")
    LiveData<List<CartFav>> getAllFav();


    ////////////////////query for cartItemTopping//////////////////////

    @Query("SELECT * FROM CartItemTopping WHERE orderId = :id")
    List<CartItemTopping> getTopping(int id);
    @Insert
    void insertTopping(List<CartItemTopping> cartItemToppings);
    @Delete
    void delete(CartItemTopping cartItemTopping);
    @Query("DELETE FROM CartItemTopping")
    void emptyCartTopping();
    @Query("DELETE FROM CartItemTopping WHERE orderId = :id")
    void deleteCartToppingItem(int id);


    ///////////////////query for Order/////////////////////////////////////////

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrder(Order orders);
    @Query("SELECT * FROM orderCart WHERE userId = :id" )
    List<Order> getAllOrders(int id);


}
