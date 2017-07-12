package com.indeema.carouselview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.indeema.carouselview.widget.ItemWidget;
import com.indeema.carouselview.widget.ItemScrollerWidget;
import com.indeema.carouselview.widget.ItemWidgetInterface;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<View>listItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ItemScrollerWidget itemScrollerWidget = (ItemScrollerWidget)findViewById(R.id.carousel_widget);
        itemScrollerWidget.setVisibleItemsOnScreen(3);
        Log.d("MainActivity","Scroll width = " + itemScrollerWidget.getWidth());

        ItemWidget itemWidget1 = new ItemWidget(this);
        ItemWidget itemWidget2 = new ItemWidget(this);
        ItemWidget itemWidget3 = new ItemWidget(this);
        ItemWidget itemWidget4 = new ItemWidget(this);
        ItemWidget itemWidget5 = new ItemWidget(this);

        itemWidget1.setIcons(R.drawable.economy_plus_selected, R.drawable.economy_plus);
        itemWidget2.setIcons(R.drawable.economy_selected, R.drawable.economy);
        itemWidget3.setIcons(R.drawable.premium_selected, R.drawable.premium);
        itemWidget4.setIcons(R.drawable.regular_plus_selected, R.drawable.regular_plus);
        itemWidget5.setIcons(R.drawable.regular_selected, R.drawable.regular);

        listItems.add(itemWidget1);
        listItems.add(itemWidget2);
        listItems.add(itemWidget3);
        listItems.add(itemWidget4);
        listItems.add(itemWidget5);

        itemScrollerWidget.addItems(listItems);
        itemScrollerWidget.setOnCarouselCallbackListener(new ItemScrollerWidget.OnCarouselCallbackListener() {
            @Override
            public void onItemsScroll(int selectedItemIndex, int unselectedItemIndex) {
                if (listItems.get(selectedItemIndex) instanceof ItemWidgetInterface) {
                    ((ItemWidgetInterface)listItems.get(unselectedItemIndex)).setItemSelected(false);
                    ((ItemWidgetInterface)listItems.get(selectedItemIndex)).setItemSelected(true);
                }
            }

            @Override
            public void onItemClick(int index) {
            }
        });

        Log.d("MainActivity","Scroll width = " + itemScrollerWidget.getWidth());
    }
}
