package ru.sigmadigital.recipes.model.realmObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.model.response.StepResponse;


public class RealmRecipe extends RealmObject implements Serializable {

    @PrimaryKey
    private long id;
    private String name;
    private RealmCategory category;
    private RealmList<RealmProduct> products = new RealmList<>();
    private RealmList<RealmStep> steps = new RealmList<>();
    private int selected = 0;


    public RealmRecipe() {
    }

    public RealmRecipe(RecipeResponse recipeResponse) {
        this.id = recipeResponse.getId();
        this.name = recipeResponse.getName();
        this.category = new RealmCategory(recipeResponse.getCategory());
        this.selected = recipeResponse.getSelected();

        for (ProductResponse product : recipeResponse.getProducts()) {
            products.add(new RealmProduct(product));
        }

        for (StepResponse step : recipeResponse.getSteps()) {
            steps.add(new RealmStep(step));
        }
    }


    public RealmCategory getCategory() {
        return category;
    }

    public void setCategory(RealmCategory category) {
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private RealmList<RealmStep> getSteps() {
        return steps;
    }

    public RealmList<RealmProduct> getProducts() {
        return products;
    }

    public void setProducts(RealmList<RealmProduct> products) {
        this.products = products;
    }

    private int getSelected() {
        return selected;
    }

    public RecipeResponse toRecipe() {

        List<ProductResponse> productResponseList = new ArrayList<>();
        List<StepResponse> stepResponseList = new ArrayList<>();
        RecipeResponse recipe = new RecipeResponse();
        recipe.setId(this.getId());
        recipe.setCategory(this.getCategory().toCategory());
        recipe.setName(this.getName());
        recipe.setSelected(this.getSelected());

        for (RealmProduct x : this.getProducts()) {
            productResponseList.add(x.toProduct());
        }
        recipe.setProducts(productResponseList);

        for (RealmStep x : this.getSteps()) {
            stepResponseList.add(x.toStep());
        }
        recipe.setSteps(stepResponseList);

        return recipe;
    }

}