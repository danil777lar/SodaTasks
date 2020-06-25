package com.larje.taskmanager.ui.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.larje.taskmanager.MainActivity;
import com.larje.taskmanager.R;
import com.larje.taskmanager.data.DBManager;
import com.larje.taskmanager.sphere.AddElementDialog;
import com.larje.taskmanager.sphere.GoalScreen;
import com.larje.taskmanager.sphere.KategoryScreen;
import com.larje.taskmanager.sphere.SphereScreen;
import com.larje.taskmanager.sphere.field.Node;
import com.larje.taskmanager.sphere.field.NodeField;
import com.larje.taskmanager.sphere.field.NodeOpen;
import com.larje.taskmanager.task.TaskScreen;

/**
 * A placeholder fragment containing a simple view.
 */

public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";

//    SPHERE VARIABLES
    public static int spherelvl = 0;
    public static int opened_sphereid = 0;
    public static int closed_sphereid = 0;
    public static int scrolly;
    public static int kategoryId;
    public static boolean sphereAnim = true;

//    TASK VARIABLES
    public static int taskFilterOption = 0;
    public static int taskScrollY = 0;

    public static FragmentManager fm;

    private int index;
    private SphereScreen sphere;
    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        pageViewModel.setIndex(index);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        fm = this.getParentFragmentManager();
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        RelativeLayout rel_layout = root.findViewById(R.id.rel_layout);

        //Кнопка добавления сферы
        FloatingActionButton flt = root.findViewById(R.id.fab);
        switch (MainActivity.theme){
            case 0:
                flt.setImageResource(R.drawable.ic_add_light);
                break;
            case 1:
                flt.setImageResource(R.drawable.ic_add);
                break;
        }
        flt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dialog = new AddElementDialog(-1, 0);
                dialog.show(getFragmentManager(), "none");
            }
        });

        //Определение наполнения фрагмента
        if (getArguments() != null) {
            this.index = getArguments().getInt(ARG_SECTION_NUMBER);
        }
        switch (index) {
            case(1):
                flt.setVisibility(View.VISIBLE);
                switch (spherelvl) {
                    case (0):
                        makeSphere(rel_layout, sphereAnim);
                        if (sphereAnim == false){sphereAnim = true;}
                        break;
                    case (1):
                        NodeField nodeField = new NodeField(getContext(), kategoryId, getFragmentManager());
                        if (nodeField.hasChildren()) {
                            rel_layout.addView(nodeField.getNodeField());
                            flt.setVisibility(View.GONE);
                        }else{
                            makeSphere(rel_layout, false);
                            NodeOpen nodeOpen = new NodeOpen(kategoryId);
                            nodeOpen.show(getFragmentManager(), "none");
                            spherelvl = 0;
                        }
                        break;
                }
                break;
            case(2):
                flt.setVisibility(View.GONE);
                TaskScreen taskScreen = new TaskScreen(getContext(), taskFilterOption, taskScrollY);
                rel_layout.addView(taskScreen.getTaskScreen());
                break;
            case(3):
                flt.setVisibility(View.GONE);
                GoalScreen goalScreen = new GoalScreen(getContext());
                rel_layout.addView(goalScreen.getGoalScreen());
                break;
        }
        return root;
    }

    @Override
    public void onStart(){
        super.onStart();
        try {this.sphere.StartAnim();}
        catch (java.lang.NullPointerException e){}
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void makeSphere(RelativeLayout rel_layout, Boolean anim){
        SphereScreen sphere = new SphereScreen(getContext(), opened_sphereid, closed_sphereid, getFragmentManager(), anim);
        this.sphere = sphere;
        final ScrollView newScroll = sphere.Create();
        newScroll.setScrollY(scrolly);
        rel_layout.addView(newScroll);
        rel_layout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                newScroll.getViewTreeObserver().removeOnPreDrawListener(this);
                newScroll.setScrollY(scrolly);
                return false;
            }
        });
    }
}