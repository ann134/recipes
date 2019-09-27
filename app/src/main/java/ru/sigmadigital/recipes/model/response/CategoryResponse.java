/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.recipes.model.response;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dima
 */

public class CategoryResponse extends Response implements Serializable {

    private long id;
    private CategoryResponse parent;
    private String name;
    private String action;


    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setParent(CategoryResponse parent) {
        this.parent = parent;
    }

    public CategoryResponse getParent() {
        return parent;
    }

    public String getName() {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public void setName(String name) {
        this.name = name;
    }


    public static List<CategoryResponse> fromJsonList(String json) {
        try {
            Type list = new TypeToken<ArrayList<CategoryResponse>>() {
            }.getType();
            List<CategoryResponse> items = new Gson().fromJson(json, list);
            if (items != null) {
                return namesUpperCase(items);
            } else {
                return new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static List<CategoryResponse> namesUpperCase(List<CategoryResponse> list) {
        for (CategoryResponse category : list) {
            category.setName(firstUpperCase(category.getName()));
        }
        return list;
    }

    private static String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }
}
