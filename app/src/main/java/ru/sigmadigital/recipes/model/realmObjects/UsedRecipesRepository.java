package ru.sigmadigital.recipes.model.realmObjects;

import io.realm.RealmList;
import io.realm.RealmObject;

public class UsedRecipesRepository extends RealmObject {

    private RealmList<UsedRecipes> categoriesList = new RealmList<>();

    public UsedRecipes getCurrentUsedRecipes(long category) {

        for (UsedRecipes usedRecipes : categoriesList) {
            if (usedRecipes.getCategoryId() == category)
                return usedRecipes;
        }

        UsedRecipes newUR = new UsedRecipes(category);
        categoriesList.add(newUR);
        return newUR;
    }
}
