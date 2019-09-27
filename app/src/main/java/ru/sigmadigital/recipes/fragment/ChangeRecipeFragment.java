package ru.sigmadigital.recipes.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickCancel;
import com.vansuita.pickimage.listeners.IPickResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.ChangeRecipeActivity;
import ru.sigmadigital.recipes.adapter.ProductsAdapter;
import ru.sigmadigital.recipes.adapter.SimpleItemTouchHelperCallback;
import ru.sigmadigital.recipes.adapter.StepsAdapter;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.asynctasks.CreateRecipeTask;
import ru.sigmadigital.recipes.api.asynctasks.DeleteRecipeTask;
import ru.sigmadigital.recipes.api.asynctasks.SendImageTask;
import ru.sigmadigital.recipes.api.asynctasks.UpdateRecipeTask;
import ru.sigmadigital.recipes.model.request.CreateRecipeRequest;
import ru.sigmadigital.recipes.model.request.ProductRequest;
import ru.sigmadigital.recipes.model.request.StepRequest;
import ru.sigmadigital.recipes.model.response.CategoryResponse;
import ru.sigmadigital.recipes.model.response.DoneResponse;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.RecipeResponse;
import ru.sigmadigital.recipes.model.response.StepResponse;
import ru.sigmadigital.recipes.util.PicassoUtil;


public class ChangeRecipeFragment extends Fragment implements AddStepDialogFragment.DialogListener, AddProductDialogFragment.DialogListener, View.OnClickListener {

    private CategoryResponse category;
    private RecipeResponse recipe;


    private InputStream imageInputStream;

    private ImageView image;
    private TextView changeImage;
    private ImageView logo;
    private TextView logoText;

    private EditText name;
    private ProductsAdapter productsAdapter;
    private StepsAdapter stepAdapter;
    private Button save;
    private ProgressBar loading;

    private CreateRecipeTask createRecipeTask;
    private UpdateRecipeTask updateRecipeTask;
    private DeleteRecipeTask deleteRecipeTask;
    private SendImageTask sendImageTask;


    public static ChangeRecipeFragment newInstance() {
        return new ChangeRecipeFragment();
    }

    public static ChangeRecipeFragment newInstance(RecipeResponse recipe) {
        ChangeRecipeFragment fragment = new ChangeRecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("recipe", recipe);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static ChangeRecipeFragment newInstance(CategoryResponse category) {
        ChangeRecipeFragment fragment = new ChangeRecipeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("category", category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("recipe")) {
            recipe = (RecipeResponse) getArguments().getSerializable("recipe");
            return;
        }
        if (getArguments() != null && getArguments().containsKey("category")) {
            category = (CategoryResponse) getArguments().getSerializable("category");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.fragment_change_recipe, null);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        image = view.findViewById(R.id.image);
        changeImage = view.findViewById(R.id.change_image);
        logo = view.findViewById(R.id.logo);
        logoText = view.findViewById(R.id.logo_text);
        name = view.findViewById(R.id.name);
        save = view.findViewById(R.id.save);
        loading = view.findViewById(R.id.loading);


        setRecipeData();
        initAdapters(view);


        view.findViewById(R.id.image_container).setOnClickListener(this);
        changeImage.setOnClickListener(this);
        view.findViewById(R.id.add_product).setOnClickListener(this);
        view.findViewById(R.id.add_step).setOnClickListener(this);
        save.setOnClickListener(this);

        return view;
    }

    private void setRecipeData() {
        if (recipe != null) {
            name.setText(recipe.getName());
            save.setText(R.string.save_changes);

            PicassoUtil.getPicasso()
                    .load(Network.BASE_URL + "api/recipe/" + recipe.getId() + "/photo").into(image, new Callback() {
                @Override
                public void onSuccess() {
                    setImageLogoVisibility(false);
                }

                @Override
                public void onError(Exception e) {
                    setImageLogoVisibility(true);
                }
            });

            stepAdapter = new StepsAdapter(recipe.getSteps());
            productsAdapter = new ProductsAdapter(recipe.getProducts());
        }

        if (category != null) {
            setImageLogoVisibility(true);
            save.setText(R.string.add_recipe);

            stepAdapter = new StepsAdapter();
            productsAdapter = new ProductsAdapter();
        }
    }

    private void initAdapters(View view) {
        final RecyclerView productsRecycler = view.findViewById(R.id.products);
        productsRecycler.setAdapter(productsAdapter);
        productsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        final SimpleItemTouchHelperCallback productsCallback = new SimpleItemTouchHelperCallback(App.getAppContext(), productsAdapter);
        ItemTouchHelper productsTouchHelper = new ItemTouchHelper(productsCallback);
        productsTouchHelper.attachToRecyclerView(productsRecycler);
        productsRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                productsCallback.onDraw(c);
            }
        });


        final RecyclerView stepsRecycler = view.findViewById(R.id.steps);
        stepsRecycler.setAdapter(stepAdapter);
        stepsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        final SimpleItemTouchHelperCallback stepsCallback = new SimpleItemTouchHelperCallback(App.getAppContext(), stepAdapter);
        ItemTouchHelper stepsTouchHelper = new ItemTouchHelper(stepsCallback);
        stepsTouchHelper.attachToRecyclerView(stepsRecycler);
        stepsRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                stepsCallback.onDraw(c);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof ChangeRecipeActivity && recipe != null) {
            ((ChangeRecipeActivity) getActivity()).setActionButtonImage(getResources().getDrawable(R.drawable.ic_trash));
            ((ChangeRecipeActivity) getActivity()).setActionButtonClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getFragmentManager() != null)
                        DeleteDialogFragment.newInstance("Удалить " + recipe.getName() + "?", new DeleteDialogFragment.OnActionClickListener() {
                            @Override
                            public void onActionClick(int action) {
                                if (action == DeleteDialogFragment.PRESS_OK) {
                                    deleteRecipeTask = new DeleteRecipeTask(recipe, new DeleteRecipeTask.OnDeleteRecipeListener() {
                                        @Override
                                        public void onDeleteRecipe(DoneResponse response) {
                                            if (response.isResult()) {
                                                if (getActivity() != null) {
                                                    Intent intent = new Intent();
                                                    intent.putExtra("delete", true);
                                                    getActivity().setResult(Activity.RESULT_OK, intent);
                                                    getActivity().finish();
                                                }
                                            }
                                        }
                                    });
                                    deleteRecipeTask.execute();
                                }
                            }
                        }).show(getFragmentManager(), "dialog");
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (createRecipeTask != null) {
            createRecipeTask.cancel(true);
        }
        if (updateRecipeTask != null) {
            updateRecipeTask.cancel(true);
        }
        if (deleteRecipeTask != null) {
            deleteRecipeTask.cancel(true);
        }
        if (sendImageTask != null) {
            sendImageTask.cancel(true);
        }
    }

    @Override
    public void addStep(String s) {
        hideKeyboard(this.getView());
        stepAdapter.addStep(s);
    }

    @Override
    public void addProduct(ProductResponse product) {
        hideKeyboard(this.getView());
        productsAdapter.addProduct(product);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_container: {
                setImage();
                break;
            }

            case R.id.change_image: {
                setImage();
                break;
            }

            case R.id.add_product: {
                AddProductDialogFragment dialogFragment = new AddProductDialogFragment();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
                dialogFragment.setListener(this.getContext(), ChangeRecipeFragment.this);
                if (getFragmentManager() != null)
                    dialogFragment.show(getFragmentManager(), "AddProductDialogFragment");
                break;
            }

            case R.id.add_step: {
                AddStepDialogFragment dialogFragment = new AddStepDialogFragment();
                dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
                dialogFragment.setListener(ChangeRecipeFragment.this);
                if (getFragmentManager() != null)
                    dialogFragment.show(getFragmentManager(), "AddStepDialogFragment");
                break;
            }

            case R.id.save: {
                save.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
                sendRecipe();
                break;
            }
        }
    }


    private void sendRecipe() {
        if (productsAdapter.getProducts().size() > 0 && !name.getText().toString().equals("")) {
            List<ProductRequest> productRequests = new ArrayList<>();
            for (ProductResponse product : productsAdapter.getProducts()) {
                productRequests.add(new ProductRequest(product.getName(), product.getUnits().getId(), product.getCount()));
            }

            List<StepRequest> recipeStepRequests = new ArrayList<>();
            for (StepResponse step : stepAdapter.getSteps()) {
                recipeStepRequests.add(new StepRequest(step.getAction(), step.getPosition()));
            }

            if (category != null) {
                CreateRecipeRequest createRecipeRequest = new CreateRecipeRequest(name.getText().toString(), category.getId(), recipeStepRequests, productRequests);

                createRecipeTask = new CreateRecipeTask(createRecipeRequest, new CreateRecipeTask.OnAddRecipeListener() {
                    @Override

                    public void onAddRecipe(RecipeResponse recipeResponse) {

                        if (recipeResponse == null) {
                            save.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.GONE);
                            if (getFragmentManager() != null)
                                InfoDialogFragment.newInstance(getString(R.string.sending_error)).show(getFragmentManager(), "dialog");
                            return;
                        }

                        if (imageInputStream != null) {
                            uploadImage(recipeResponse);
                        } else {
                            if (getActivity() != null)
                                getActivity().finish();
                        }
                    }
                });
                createRecipeTask.execute();
            }

            if (recipe != null) {
                CreateRecipeRequest createRecipeRequest = new CreateRecipeRequest(name.getText().toString(), recipe.getCategory().getId(), recipeStepRequests, productRequests);

                updateRecipeTask = new UpdateRecipeTask(createRecipeRequest, recipe.getId(), new UpdateRecipeTask.OnUpdateRecipeListener() {
                    @Override
                    public void onUpdateRecipe(RecipeResponse recipeResponse) {

                        if (recipeResponse == null) {
                            save.setVisibility(View.VISIBLE);
                            loading.setVisibility(View.GONE);
                            if (getFragmentManager() != null)
                                InfoDialogFragment.newInstance(getString(R.string.sending_error)).show(getFragmentManager(), "dialog");
                            return;
                        }

                        if (imageInputStream != null) {
                            uploadImage(recipe);
                        } else {
                            if (getActivity() != null)
                                getActivity().finish();
                        }
                    }
                });
                updateRecipeTask.execute();
            }
        } else {
            InfoDialogFragment.newInstance("Введите название рецепта и добавте продукт").show(getFragmentManager(),"dialog");
            loading.setVisibility(View.GONE);
            save.setVisibility(View.VISIBLE);
        }
    }


    private void setImageLogoVisibility(boolean isLogoVisible) {
        if (isLogoVisible) {
            logo.setVisibility(View.VISIBLE);
            logoText.setVisibility(View.VISIBLE);
            changeImage.setVisibility(View.GONE);
        } else {
            logo.setVisibility(View.GONE);
            logoText.setVisibility(View.GONE);
            changeImage.setVisibility(View.VISIBLE);
        }
    }

    private void setImage() {

        PickImageDialog.build(new PickSetup()).setOnPickResult(new IPickResult() {
            @Override
            public void onPickResult(PickResult pickResult) {
                try {
                    image.setImageBitmap(pickResult.getBitmap());
                    setImageLogoVisibility(false);

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(pickResult.getBitmap(), pickResult.getBitmap().getWidth() * 400 / pickResult.getBitmap().getHeight(), 400, true);
                    File file2 = new File(getActivity().getFilesDir(), "BitmapScale.jpg");
                    if (file2.exists())
                        file2.delete();
                    FileOutputStream out2 = new FileOutputStream(file2);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out2);
                    out2.flush();
                    out2.close();
                    imageInputStream = new FileInputStream(file2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).setOnPickCancel(new IPickCancel() {
            @Override
            public void onCancelClick() {

            }
        }).show(getFragmentManager());

    }

    private void uploadImage(RecipeResponse recipe) {
        sendImageTask = new SendImageTask(recipe, imageInputStream, new SendImageTask.OnSendImageListener() {
            @Override
            public void onSendImage(DoneResponse response) {
                if (getActivity() != null)
                    getActivity().finish();
            }
        });
        sendImageTask.execute();

        PicassoUtil.deletePicasso();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int PICK_PHOTO_FOR_RECIPE = 12;
        if (requestCode == PICK_PHOTO_FOR_RECIPE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return;
            }
            try {
                if (data.getData() != null && getActivity() != null) {
                    InputStream inputStream = App.getAppContext().getContentResolver().openInputStream(data.getData());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    image.setImageBitmap(bitmap);
                    setImageLogoVisibility(false);

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 400 / bitmap.getHeight(), 400, true);
                    File file2 = new File(getActivity().getFilesDir(), "BitmapScale.jpg");
                    if (file2.exists())
                        file2.delete();
                    FileOutputStream out2 = new FileOutputStream(file2);
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out2);
                    out2.flush();
                    out2.close();

                    imageInputStream = new FileInputStream(file2);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) App.getAppContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
