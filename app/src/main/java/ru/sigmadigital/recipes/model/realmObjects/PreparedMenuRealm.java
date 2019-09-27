package ru.sigmadigital.recipes.model.realmObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;

public class PreparedMenuRealm extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;
    private RealmList<RealmRecipe> recipes;

    private long date;


    public PreparedMenuRealm() {
        recipes = new RealmList<>();
    }

    public PreparedMenuRealm(RealmList<RealmRecipe> recipes) {
        this.recipes = recipes;
    }


    public void setRecipes(RealmList<RealmRecipe> recipes) {
        this.recipes = recipes;
    }

    public void setRecipe(RealmRecipe recipe) {
        this.recipes.add(recipe);
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }


    public RealmList<RealmRecipe> getRecipes() {
        return recipes;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }


    public List<RecipeResponse> getRecipeResponses(){
        List<RecipeResponse> recipeResponses = new ArrayList<>();
        for (RealmRecipe x : recipes) {
            recipeResponses.add(x.toRecipe());
        }
        return recipeResponses;
    }


    public List<RealmProduct> getProductResponses(){
        List<RealmProduct> products = new ArrayList<>();
        for (RealmRecipe recipe : recipes) {
            for (RealmProduct product : recipe.getProducts())
            products.add(product);
        }
        return products;
    }

}
