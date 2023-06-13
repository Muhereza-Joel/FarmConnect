package com.moels.farmconnect.activities;

import android.app.UiModeManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.moels.farmconnect.R;
import com.moels.farmconnect.adapters.ViewPagerAdapter;
import com.moels.farmconnect.fragments.ChatListFragment;
import com.moels.farmconnect.fragments.ZonesListFragment;
import com.moels.farmconnect.receivers.NetworkChangeReceiver;
import com.moels.farmconnect.services.ProductsDataSyncService;
import com.moels.farmconnect.utility_classes.UI;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements ProductsDataSyncService.ProductsSyncListener{

    public TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton startNewChatFloatingActionButton, addNewZoneFloatingActionButton, startNewCallFloatingActionButton;
    private Toolbar toolbar;
    private ViewPagerAdapter viewPagerAdapter;
    private SharedPreferences myAppPreferences;
    private boolean buyerAccountChosen;
    private boolean farmerAccountChosen;
    private Bundle bundle  = new Bundle();
    private ProgressBar progressBar;
    private ProductsDataSyncService productsDataSyncService;
    private boolean bound = false;

    private ServiceConnection productsSyncServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            ProductsDataSyncService.ProductsSyncServiceBinder productsSyncServiceBinder = (ProductsDataSyncService.ProductsSyncServiceBinder) binder;
            productsDataSyncService = productsSyncServiceBinder.getProductsSyncServiceBinder();
            productsDataSyncService.setProductsSyncListener(MainActivity.this);
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        setUpStatusBar();
        UI.setUpToolbarInDarkMode(getApplicationContext(), toolbar);
        UI.setUpTabLayoutInDarkMode(getApplicationContext(), tabLayout);

        if (savedInstanceState != null) {
            int currentlySelectedTab = savedInstanceState.getInt("currentlySelectedTab");
            tabLayout.selectTab(tabLayout.getTabAt(currentlySelectedTab));
        }


        Drawable icon = toolbar.getOverflowIcon();
        if (icon != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                icon.setTint(ContextCompat.getColor(this, R.color.colorWhite));
                toolbar.setOverflowIcon(icon);
            }
        }

        setSupportActionBar(toolbar);
        UI.setUpActionBar(getSupportActionBar(), "FarmConnect");

        //Create adapter for the view_pager
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        initTabLayout();

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager); //Link Tab_layout and View_pager
        viewPagerAdapter.notifyDataSetChanged();

        Log.d("On Create Called", "Selected Tab " +tabLayout.getSelectedTabPosition());


        startNewChatFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectContactActivity.class);
                startActivity(intent);
            }
        });

        addNewZoneFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundle.putInt("currentlySelectedTab",tabLayout.getSelectedTabPosition());
                Intent intent = new Intent(MainActivity.this, AddNewZoneActivity.class);
                startActivity(intent);
            }
        });

        startNewCallFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectContactActivity.class);
                startActivity(intent);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Update the FAB position based on the ViewPager position and offset
                int viewPagerWidth = viewPager.getWidth();
                int fabWidth = startNewChatFloatingActionButton.getWidth();
                int fabMargin = (int) getResources().getDimension(R.dimen.fab_margin);
                int fabPosition = (int) ((viewPagerWidth - fabMargin - fabWidth) * positionOffset) + fabMargin;

                // Set the new position of the FAB
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) startNewChatFloatingActionButton.getLayoutParams();
                params.setMargins(params.leftMargin, params.topMargin, fabPosition, params.bottomMargin);
                startNewChatFloatingActionButton.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == Objects.requireNonNull(viewPager.getAdapter()).getCount() - 1) {
                    startNewChatFloatingActionButton.hide();
                } else {
                    startNewChatFloatingActionButton.show();
                }

                // Hide all FABs except for the one corresponding to the current page
                startNewChatFloatingActionButton.hide();
                addNewZoneFloatingActionButton.hide();
                startNewCallFloatingActionButton.hide();

                switch (position) {
                    case 0:
                        startNewChatFloatingActionButton.show();
                        break;
                    case 1:
                        addNewZoneFloatingActionButton.show();
                        break;
                    case 2:
                        startNewCallFloatingActionButton.show();
                        break;
                }

                if (farmerAccountChosen == true) {
                    addNewZoneFloatingActionButton.hide();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentlySelectedTab", tabLayout.getSelectedTabPosition());
    }



    @Override
    protected void onStart() {
        super.onStart();
        tabLayout.selectTab(tabLayout.getTabAt(tabLayout.getSelectedTabPosition()));
        // Check if there is an active network connection during app launch
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    Intent serviceIntent = new Intent(getApplicationContext(), ProductsDataSyncService.class);
                    startService(serviceIntent);
                    bindService(serviceIntent, productsSyncServiceConnection, Context.BIND_AUTO_CREATE);

            } else {
                    Intent serviceIntent = new Intent(getApplicationContext(), ProductsDataSyncService.class);
                    startService(serviceIntent);
                    bindService(serviceIntent, productsSyncServiceConnection, Context.BIND_AUTO_CREATE);

            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        bundle.putInt("currentlySelectedTab", tabLayout.getSelectedTabPosition());
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        // Get the 3 dotted menu icon view
        View menuView = findViewById(R.id.action_help);
        if (menuView != null) {
            // Set the pop-up menu to be displayed below the 3 dotted menu icon
            PopupMenu popupMenu = new PopupMenu(this, menuView);
            popupMenu.inflate(R.menu.main_menu);
            popupMenu.setOnMenuItemClickListener((PopupMenu.OnMenuItemClickListener) this);

            // Set the gravity to display the menu below the icon
            popupMenu.setGravity(Gravity.END | Gravity.BOTTOM);
            popupMenu.show();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem syncItem = menu.findItem(R.id.action_sync);
        progressBar = syncItem.getActionView().findViewById(R.id.action_sync_progress);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            //TODO Launch Settings activity
            return true;
        } else if (id == R.id.action_help) {
            //TODO Launch help activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI(){
        toolbar = findViewById(R.id.my_toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.viewPager);
        startNewChatFloatingActionButton = findViewById(R.id.start_new_chart_fab);
        addNewZoneFloatingActionButton = findViewById(R.id.add_new_zone_fab);
        startNewCallFloatingActionButton = findViewById(R.id.start_new_call_fab);

    }

    private void setUpStatusBar() {
        Window window = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            UiModeManager uiModeManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
            int currentMode = uiModeManager.getNightMode();
            if (currentMode == UiModeManager.MODE_NIGHT_YES) {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));
            }else {
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            }
        }

    }
    private void initTabLayout(){
        //Add fragments to the adapter
        myAppPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE);
        buyerAccountChosen = myAppPreferences.getBoolean("buyerAccountTypeChosen", false);
        farmerAccountChosen = myAppPreferences.getBoolean("farmerAccountTypeChosen", false);
        SharedPreferences.Editor editor = myAppPreferences.edit();

        if (buyerAccountChosen == true) {
            viewPagerAdapter.addFragment(new ChatListFragment(), "Chats");
            viewPagerAdapter.addFragment(new ZonesListFragment(), "Zones");
            viewPagerAdapter.addFragment(new ChatListFragment(), "Calls");

            editor.putBoolean("buyerAccountTypeChosen", true);
            editor.putBoolean("farmerAccountTypeChosen", false);
            editor.apply();

        } else if(farmerAccountChosen == true) {
            viewPagerAdapter.addFragment(new ChatListFragment(), "Chats");
            viewPagerAdapter.addFragment(new ZonesListFragment(), "Zones");
            viewPagerAdapter.addFragment(new ChatListFragment(), "Calls");

            editor.putBoolean("farmerAccountTypeChosen", true);
            editor.putBoolean("buyerAccountTypeChosen", false);
            editor.apply();
        }

    }

    @Override
    public void onProductsSyncComplete() {
        if (bound){
            unbindService(productsSyncServiceConnection);
            progressBar.setVisibility(View.GONE);
            bound = false;
        }

    }

}