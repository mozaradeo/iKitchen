package freaktemplate.kingburger.observableLayer;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

public class CartListViewModel extends AndroidViewModel {

    private final AppDatabase appDatabase;


    public CartListViewModel(@NonNull Application application) {
        super(application);
        appDatabase = AppDatabase.getAppDatabase(this.getApplication());

    }

    /*
     * =============================================================
     * ================ Database Operation Handlers=================
     * =============================================================
     */

    //Controlling queries in async Mode Only

    public LiveData<List<Cart>> getCartList() {
        return appDatabase.cartDao().getAll();
    }

    public LiveData<List<CartFav>> getFavCartList() {
        return appDatabase.cartDao().getAllFav();
    }

    public boolean getFavItem(int itemId, int CatId) {
        Log.e("CheckFav", appDatabase.cartDao().isFav(CatId, itemId) + "");
        return appDatabase.cartDao().isFav(CatId, itemId) == 1;
    }

    public int getRowId() {
        return appDatabase.cartDao().getRowId();
    }

    public int getCartCount() {
        return appDatabase.cartDao().getCount();
    }


    public void deleteItem(Cart cart) {
        new deleteAsyncTask(appDatabase).execute(cart);
    }

    public void emptyCart() {
        new deleteCartAsyncTask(appDatabase).execute();
    }

    public void emptyCartTopping() {
        new deleteCartToppingAsyncTask(appDatabase).execute();
    }


    public void insertItem(Cart cart, OnTaskCompleted listener) {
        new addCartItemAsyncTask(appDatabase, listener).execute(cart);
    }

    public void insertFavItem(CartFav cartFav) {
        new addCartFavItemAsyncTask(appDatabase).execute(cartFav);
    }

    public void insertItemTopping(List<CartItemTopping> topping) {
        new addCartItemToppingAsyncTask(appDatabase).execute(topping);
    }

    //Async task Controlling queries

    private static class deleteAsyncTask extends AsyncTask<Cart, Void, Void> {
        private final AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Cart... params) {
            db.cartDao().delete(params[0]);
            return null;
        }
    }

    private static class deleteCartAsyncTask extends AsyncTask<Cart, Void, Void> {
        private final AppDatabase db;

        deleteCartAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Cart... params) {
            db.cartDao().emptyCart();
            return null;
        }
    }

    private static class deleteCartToppingAsyncTask extends AsyncTask<Cart, Void, Void> {
        private final AppDatabase db;

        deleteCartToppingAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Cart... params) {
            db.cartDao().emptyCartTopping();
            return null;
        }
    }

    private static class addCartItemAsyncTask extends AsyncTask<Cart, Void, Void> {
        private final AppDatabase db;
        private final OnTaskCompleted listener;


        addCartItemAsyncTask(AppDatabase db, OnTaskCompleted listener) {
            this.db = db;
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Cart... carts) {
            db.cartDao().insertItem(carts[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listener.onTaskCompleted();
        }
    }


    private static class addCartFavItemAsyncTask extends AsyncTask<CartFav, Void, Void> {
        private final AppDatabase db;

        addCartFavItemAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(CartFav... carts) {
            db.cartDao().insertFavItem(carts);
            return null;
        }
    }

    private static class addCartItemToppingAsyncTask extends AsyncTask<List<CartItemTopping>, Void, Void> {
        private final AppDatabase db;

        addCartItemToppingAsyncTask(AppDatabase db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(List<CartItemTopping>... lists) {
            db.cartDao().insertTopping(lists[0]);
            return null;
        }
    }

    public interface OnTaskCompleted {
        void onTaskCompleted();
    }


}
