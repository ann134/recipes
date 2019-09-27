/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.recipes.model.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * @author dima
 */


public class RecipeResponse extends Response implements Serializable {

    public static final int GOOD_SELECT = 1;
    public static final int BAD_SELECT = 2;

    private long id;
    private String name;
    private CategoryResponse category;
    private List<StepResponse> steps = new ArrayList<>();
    private List<ProductResponse> products = new ArrayList<>();
    private int selected;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CategoryResponse getCategory() {
        return category;
    }

    public CategoryResponse getGlobalCategory() {
        CategoryResponse global = category;
        while (global.getParent() != null) {
           global = global.getParent();
        }
        return global;
    }

    public void setCategory(CategoryResponse category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StepResponse> getSteps() {
        return steps;
    }

    public void setSteps(List<StepResponse> steps) {
        this.steps = steps;
    }

    public List<ProductResponse> getProducts() {
        return products;
    }

    public void setProducts(List<ProductResponse> products) {
        this.products = products;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public void sortSteps(){
        if(steps != null){
            Collections.sort(steps, new Comparator<StepResponse>() {
                @Override
                public int compare(StepResponse o1, StepResponse o2) {
                    if(o1.getPosition() == o2.getPosition()){
                        return  0;
                    }
                    return o1.getPosition() > o2.getPosition() ? 1 : -1;
                }
            });
        }
    }
}
