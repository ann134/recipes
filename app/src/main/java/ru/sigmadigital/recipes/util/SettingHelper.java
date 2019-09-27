package ru.sigmadigital.recipes.util;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.model.User;
import ru.sigmadigital.recipes.model.realmObjects.ArchiveMenuRealm;
import ru.sigmadigital.recipes.model.realmObjects.PreparedMenuRealm;
import ru.sigmadigital.recipes.model.realmObjects.RealmRecipe;
import ru.sigmadigital.recipes.model.realmObjects.RealmUnit;
import ru.sigmadigital.recipes.model.realmObjects.UsedRecipes;
import ru.sigmadigital.recipes.model.realmObjects.UsedRecipesRepository;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.model.response.TokenResponse;
import ru.sigmadigital.recipes.model.response.UnitsResponse;

public class SettingHelper {

    private static String NAME = "settings";
    private static String FIELD_USER = "user";
    private static String FIELD_TOKEN = "token";


    public static User getUser() {
        if (App.getAppContext() != null) {
            String json = App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .getString(FIELD_USER, null);
            return User.fromJson(json);
        } else {
            return null;
        }
    }

    public static void setUser(User user) {
        if (App.getAppContext() != null) {
            App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(FIELD_USER, user != null ? user.toString() : null)
                    .apply();
        }
    }

    public static TokenResponse getToken() {
        if (App.getAppContext() != null) {
            String json = App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .getString(FIELD_TOKEN, null);
            return TokenResponse.fromJson(json, TokenResponse.class);
        } else {
            return null;
        }
    }

    public static void setToken(TokenResponse token) {
        if (App.getAppContext() != null) {
            App.getAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(FIELD_TOKEN, token != null ? token.toString() : null)
                    .apply();
        }
    }

    public static List<UnitsResponse> getUnits() {
        final List<UnitsResponse> unitsl = new ArrayList<>();
        if (App.getRealm() != null) {
            List<RealmUnit> runits = App.getRealm().where(RealmUnit.class).findAll();
            for (RealmUnit ru : runits) {
                unitsl.add(ru.toUnits());
            }
        }

        return unitsl;
    }

    public static void setUnits(List<UnitsResponse> units) {
        if (App.getRealm() != null) {
            App.getRealm().beginTransaction();

            //как нормально очистить старые юниты
            //App.getRealm().delete(RealmUnit.class);

            for (UnitsResponse unit : units) {
                App.getRealm().insertOrUpdate(new RealmUnit(unit));
            }
            App.getRealm().commitTransaction();
        }

    }


    public static List<RecipeResponse> getPreparedRecipesList() {
        if (App.getRealm() != null) {
            PreparedMenuRealm preparedMenuRealm = App.getRealm().where(PreparedMenuRealm.class).equalTo("id", 10).findFirst();
            List<RecipeResponse> recipeList = new ArrayList<>();
            if (preparedMenuRealm != null) {
                RealmList realmList = preparedMenuRealm.getRecipes();
                for (Object x : realmList) {
                    RealmRecipe realmRecipe = (RealmRecipe) x;
                    recipeList.add(realmRecipe.toRecipe());
                }
            }

            return recipeList;
        }
        return new ArrayList<>();
    }

    public static void savePreparedRecipes(List<RecipeResponse> list) {
        clearPreparedRecipes();

        PreparedMenuRealm preparedMenuRealm = new PreparedMenuRealm();
        RealmList<RealmRecipe> recipeRealmList = new RealmList<>();
        for (RecipeResponse x : list) {
            RealmRecipe realmRecipe = new RealmRecipe(x);
            recipeRealmList.add(realmRecipe);
        }
        if (App.getRealm() != null)
            App.getRealm().beginTransaction();
        preparedMenuRealm.setRecipes(recipeRealmList);
        preparedMenuRealm.setId(10);
        preparedMenuRealm.setDate(System.currentTimeMillis());
        App.getRealm().insertOrUpdate(preparedMenuRealm);
        App.getRealm().commitTransaction();
    }

    public static void savePreparedRecipes(PreparedMenuRealm prepMenu) {
        clearPreparedRecipes();
        PreparedMenuRealm newPrepMenu = new PreparedMenuRealm();
        if (App.getRealm() != null)
            App.getRealm().beginTransaction();
        newPrepMenu.setRecipes(prepMenu.getRecipes());
        newPrepMenu.setId(10);
        newPrepMenu.setDate(prepMenu.getDate());
        App.getRealm().insertOrUpdate(newPrepMenu);
        App.getRealm().commitTransaction();
    }

    public static void changeRecipe(RecipeResponse oldRecipe, RecipeResponse newRecipe) {
        List<RecipeResponse> preparedRecipesList = getPreparedRecipesList();
        int position = 0;
        for (RecipeResponse x : preparedRecipesList) {
            if (x.getId() == oldRecipe.getId()) {
                position = preparedRecipesList.indexOf(x);
                break;
            }
        }

        preparedRecipesList.remove(position);
        preparedRecipesList.add(position, newRecipe);
        savePreparedRecipes(preparedRecipesList);
    }

    public static void clearPreparedRecipes() {
        if (App.getRealm() != null) {
            App.getRealm().beginTransaction();
            App.getRealm().where(PreparedMenuRealm.class).equalTo("id", 10).findAll().deleteAllFromRealm();
            App.getRealm().commitTransaction();
        }
    }


    private static ArchiveMenuRealm getArchiveMenu() {
        if (App.getRealm() != null)
            if (App.getRealm().where(ArchiveMenuRealm.class).equalTo("id", 1).findFirst() != null) {
                return App.getRealm().where(ArchiveMenuRealm.class).equalTo("id", 1).findFirst();
            } else {
                ArchiveMenuRealm archiveMenuRealm = new ArchiveMenuRealm();
                archiveMenuRealm.setId(1);
                return archiveMenuRealm;
            }
        return null;
    }

    public static void addToArchiveMenu(PreparedMenuRealm menu) {
        if (App.getRealm() != null)
            App.getRealm().beginTransaction();
        ArchiveMenuRealm archiveMenuRealm = getArchiveMenu();
        if (archiveMenuRealm != null) {
            archiveMenuRealm.addPreparedMenu(menu);
            App.getRealm().insertOrUpdate(archiveMenuRealm);
        }
        App.getRealm().commitTransaction();
    }


    public static UsedRecipes getUsedRecipes(long category) {
        if (App.getRealm() != null) {
            App.getRealm().beginTransaction();
            UsedRecipesRepository result = App.getRealm().where(UsedRecipesRepository.class).findFirst();

            if (result != null) {
                UsedRecipes usedRecipes = result.getCurrentUsedRecipes(category);
                App.getRealm().commitTransaction();
                return usedRecipes;
            }

            UsedRecipesRepository newRep = new UsedRecipesRepository();
            UsedRecipes usedRecipesNew = newRep.getCurrentUsedRecipes(category);
            App.getRealm().insertOrUpdate(newRep);
            App.getRealm().commitTransaction();
            return usedRecipesNew;
        }

        return null;
    }

    public static void addToUsedRecipes(RecipeResponse recipe, int countRecipes) {
        if (App.getRealm() != null) {
            App.getRealm().beginTransaction();
            UsedRecipesRepository result = App.getRealm().where(UsedRecipesRepository.class).findFirst();
            if (result != null) {
                result.getCurrentUsedRecipes(recipe.getCategory().getId()).addRecipe(recipe, countRecipes);
            }
            App.getRealm().commitTransaction();
        }
    }

}
