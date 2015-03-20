package com.example.kurmanosiuntinys;

import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class ActivityWithNavigationDrawer extends Activity
{
    public DrawerLayout drawerLayout;
    public ListView drawerList;
    public String[] layers = {"One","Two"};
    private ActionBarDrawerToggle drawerToggle;
    private Map map;

    protected void onCreate(Bundle savedInstanceState)
    {
//        // R.id.drawer_layout should be in every activity with exactly the same id.
//        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//        
//        drawerToggle = new ActionBarDrawerToggle((Activity) this, drawerLayout, R.drawable.ic_drawer, 0, 0) 
//        {
//            public void onDrawerClosed(View view) 
//            {
//                getActionBar().setTitle(R.string.app_name);
//            }
//
//            public void onDrawerOpened(View drawerView) 
//            {
//                getActionBar().setTitle(R.string.menu);
//            }
//        };
//        drawerLayout.setDrawerListener(drawerToggle);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

//        layers = new String  {"xcvxcv","xcvcxvcx"};
        drawerList = (ListView) findViewById(R.id.left_drawer);
//        View header = getLayoutInflater().inflate(R.layout.drawer_list_header, null);
//        drawerList.addHeaderView(header, null, false);
        drawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, android.R.id.text1,
                layers));
//        View footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
//                R.layout.drawer_list_footer, null, false);
//        drawerList.addFooterView(footerView);

        drawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				 
			}
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
}