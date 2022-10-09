package com.example.myapplication.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.Recipe;
import com.example.myapplication.model.Upload;
import com.google.android.gms.safetynet.VerifyAppsConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddRecipeFragment extends Fragment
{
    private static final int RESULT_OK = -1;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private EditText recipeName, ingredients, steps, description;
    private RadioGroup uploadRadioGroup;
    private RadioButton manually_radio_btn, upload_radio_btn;
    private Button importButton;
    private RadioGroup categoryRadioGroup;
    private RadioButton beef_btn, dairy_btn, fish_btn, vegan_btn, cocktails_btn, desserts_btn;
    private String chosenCategory;
    private String chosenUploadMethod;
    private Upload upload;
    private String imageUrl;
    private String imageName;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddRecipeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddRecipeFragment newInstance(String param1, String param2) {
        AddRecipeFragment fragment = new AddRecipeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode==VerifyAppsConstants.HARMFUL_CATEGORY_BACKDOOR){
                imageUrl=data.getStringExtra("url");
                imageName=data.getStringExtra("name");
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_add_recipe, container, false);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        recipeName = view.findViewById(R.id.recipeNameText);
        description = view.findViewById(R.id.descriptionText);
        ingredients = view.findViewById(R.id.ingredientsText);
        ingredients.setEnabled(false);
        steps = view.findViewById(R.id.stepsText);
        steps.setEnabled(false);
        uploadRadioGroup = view.findViewById(R.id.uploadRadioGroup);
        manually_radio_btn = view.findViewById(R.id.manually_radio_button);
        upload_radio_btn = view.findViewById(R.id.upload_radio_button);
        importButton = view.findViewById(R.id.importButton);
        importButton.setEnabled(false);
        importButton.setBackground(getResources().getDrawable(R.drawable.button_disable_style));

        categoryRadioGroup = view.findViewById(R.id.categoryRadioGroup);
        beef_btn = view.findViewById(R.id.beef_button);
        dairy_btn = view.findViewById(R.id.dairy_button);
        fish_btn = view.findViewById(R.id.fish_button);
        vegan_btn = view.findViewById(R.id.vegan_button);
        cocktails_btn = view.findViewById(R.id.cocktails_button);
        desserts_btn = view.findViewById(R.id.desserts_button);

        // Set click listener to the radio buttons, then store the chosen category inside String
        uploadRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.manually_radio_button:
                        importButton.setEnabled(false);
                        importButton.setBackground(getResources().getDrawable(R.drawable.button_disable_style));
                        ingredients.setEnabled(true);
                        steps.setEnabled(true);
                        chosenUploadMethod = "manually";
                       break;
                    case R.id.upload_radio_button:
                        ingredients.setEnabled(false);
                        ingredients.setText("");
                        steps.setEnabled(false);
                        steps.setText("");
                        importButton.setEnabled(true);
                        importButton.setBackground(getResources().getDrawable(R.drawable.button_style));
                        chosenUploadMethod = "upload";
                        break;
                    default:
                        ingredients.setEnabled(false);
                        steps.setEnabled(false);
                        importButton.setEnabled(false);
                        importButton.setBackground(getResources().getDrawable(R.drawable.button_disable_style));
                        chosenUploadMethod = "null";
                        break;
                }
            }
        });

        categoryRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.beef_button:
                        chosenCategory = beef_btn.getText().toString();
                        break;
                    case R.id.dairy_button:
                        chosenCategory = dairy_btn.getText().toString();
                        break;
                    case R.id.fish_button:
                        chosenCategory = fish_btn.getText().toString();
                        break;
                    case R.id.vegan_button:
                        chosenCategory = vegan_btn.getText().toString();
                        break;
                    case R.id.cocktails_button:
                        chosenCategory = cocktails_btn.getText().toString();
                        break;
                    case R.id.desserts_button:
                        chosenCategory = desserts_btn.getText().toString();
                        break;
                    default:
                        chosenCategory=null;
                        break;
                }
            }
        });

        importButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment fragment = new UploadImageFragment();
            fragment.setTargetFragment(AddRecipeFragment.this, VerifyAppsConstants.HARMFUL_CATEGORY_BACKDOOR);
            transaction.addToBackStack(fragment.getClass().getName());
            transaction.replace(R.id.fragment_container, fragment);
            transaction.commit();
            }
        });



        //click listener for the add recipe button
        Button addButton = view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String ingredientsStr = ingredients.getText().toString();
                ArrayList<String> ingredients = splitIngredients(ingredientsStr);

                String recipeNameStr = recipeName.getText().toString();
                String descriptionStr = description.getText().toString();
                String stepsStr = steps.getText().toString();
                Upload upload = new Upload();
                upload.setName(imageName);
                upload.setImageUrl(imageUrl);

                //check if the fields are properly filled
                if (!recipeNameStr.equals("") && !descriptionStr.equals("") && !stepsStr.equals("") && !ingredientsStr.equals("") && chosenCategory != null && chosenUploadMethod=="manually")
                {
                    //create new recipe's object and store it in the user's data-base
                    Recipe recipe = new Recipe(recipeNameStr, descriptionStr, ingredients, stepsStr, chosenCategory);
                    db.collection("Users").document(uid).collection(chosenCategory).document(recipe.getRecipeName()).set(recipe);
                    Toast.makeText(getActivity(), "Recipe added successfully!", Toast.LENGTH_SHORT).show();
                }
                else if (!recipeNameStr.equals("") && !descriptionStr.equals("") && chosenCategory != null && chosenUploadMethod=="upload") {
                    //create new recipe's object with image and store it in the user's data-base
                    Recipe recipe = new Recipe(recipeNameStr, descriptionStr, chosenCategory, upload);
                    db.collection("Users").document(uid).collection(chosenCategory).document(recipe.getRecipeName()).set(recipe);
                    Toast.makeText(getActivity(), "Recipe added successfully!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getActivity(), "Fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }


    //this function gets a string of ingredients, separated by "," and insert each element to array list of ingredients
    public ArrayList<String> splitIngredients(String ingredients)
    {
        String[] arrayOfIngredients = ingredients.split(",");
        ArrayList<String> splitIngredients = new ArrayList<>();

        for (int i = 0; i < arrayOfIngredients.length; i++)
        {
            splitIngredients.add(arrayOfIngredients[i]);
        }
        return splitIngredients;
    }

}