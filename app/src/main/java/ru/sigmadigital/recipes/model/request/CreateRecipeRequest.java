/*
 * Херня ебаная
 * Эта ебаная херня скорее всего может наебнуться, но мне глубоко похую на это
 * Если это кто-то читает, то в данный момент вероятно я уже пью пиво и разобраться с багами смогу только завтра
 * Если "завтра" уже наступило, читаем выше еще раз
 */
package ru.sigmadigital.recipes.model.request;

import java.io.Serializable;
import java.util.List;

/**
 * @author dima
 */

public class CreateRecipeRequest extends Request implements Serializable {

    private String name;
    private long categoryId;
    private List<StepRequest> steps;
    private List<ProductRequest> products;

    public CreateRecipeRequest(String name, long categoryId, List<StepRequest> steps, List<ProductRequest> products) {
        this.name = name;
        this.categoryId = categoryId;
        this.steps = steps;
        this.products = products;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ProductRequest> getProducts() {
        return products;
    }

    public void setProducts(List<ProductRequest> products) {
        this.products = products;
    }

}
