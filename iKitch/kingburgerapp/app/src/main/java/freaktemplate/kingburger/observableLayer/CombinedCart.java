package freaktemplate.kingburger.observableLayer;

import java.util.ArrayList;

public class CombinedCart {
    private Cart cart;
    private ArrayList<CartItemTopping> ItemToppings;

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public ArrayList<CartItemTopping> getItemToppings() {
        return ItemToppings;
    }

    public void setItemToppings(ArrayList<CartItemTopping> itemToppings) {
        ItemToppings = itemToppings;
    }
}
