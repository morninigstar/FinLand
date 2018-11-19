/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.finland.utility;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.morningstar.finland.R;
import com.morningstar.finland.ui.AboutUs;
import com.morningstar.finland.ui.MainActivity;
import com.morningstar.finland.ui.TutorialActivity;

import androidx.appcompat.widget.Toolbar;

public class DrawerUtils {

    public static void getDrawer(final Activity activity, Toolbar toolbar) {

        PrimaryDrawerItem dashboardItem = new PrimaryDrawerItem().withIdentifier(1).withName("What?").withIcon(R.mipmap.ic_home);
        PrimaryDrawerItem tutorialItem = new PrimaryDrawerItem().withIdentifier(1).withName("How?").withIcon(R.mipmap.ic_slide);
        PrimaryDrawerItem aboutUsItem = new PrimaryDrawerItem().withIdentifier(1).withName("Who?").withIcon(R.mipmap.ic_about);

        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.background_gradient)
                .build();
        Drawer drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(false)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(dashboardItem, tutorialItem, aboutUsItem)
                .withAccountHeader(headerResult)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (position == 1) {
                            Intent intent = new Intent(activity, MainActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (position == 2) {
                            Intent intent = new Intent(activity, TutorialActivity.class);
                            view.getContext().startActivity(intent);
                        }
                        if (position == 3) {
                            Intent intent = new Intent(activity, AboutUs.class);
                            view.getContext().startActivity(intent);
                        }
                        return true;
                    }
                })
                .build();

    }

}
