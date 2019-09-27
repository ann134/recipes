package ru.sigmadigital.recipes.model.realmObjects;


import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import ru.sigmadigital.recipes.model.response.RecipeResponse;

public class UsedRecipes extends RealmObject {

    private long categoryId;
    private RealmList<Long> usedRecipes;

    public UsedRecipes() {
    }

    UsedRecipes(long categoryId) {
        this.categoryId = categoryId;
        usedRecipes = new RealmList<>();
    }

    long getCategoryId() {
        return categoryId;
    }

    public List<Long> getUsedRecipes() {
        return usedRecipes;
    }

    public void addRecipe(RecipeResponse recipe, int recipesInCategorySize) {
        usedRecipes.add(recipe.getId());
        if (recipesInCategorySize == usedRecipes.size()) {
            usedRecipes.clear();
        }
    }
}
