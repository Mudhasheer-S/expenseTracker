package com.example.expensetracker.ui.main;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import  android.Manifest;
import android.widget.Toast;

import com.example.expensetracker.R;
import com.example.expensetracker.worker.SmsScanWorker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private ExpenseViewModel expenseViewModel;
    private ExpenseAdapter adapter;
    private TextView tvTotal;
    private DrawerLayout drawerLayout;
    private ProgressBar progressBar;
    private Button btnStartScan;
    private UUID scanWorkId;
    private MaterialToolbar topAppBar;
    private AppBarConfiguration appBarConfiguration;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS}, 1);
        }
        topAppBar = findViewById(R.id.topAppBar);
        setSupportActionBar(topAppBar);
        drawerLayout = findViewById(R.id.drawerLayout);

        appBarConfiguration = new AppBarConfiguration.Builder(R.id.expenseListFragment)
                .setOpenableLayout(drawerLayout)
                .build();

        NavigationView navView = findViewById(R.id.navView);
        View headerView = navView.getHeaderView(0);
        btnStartScan = headerView.findViewById(R.id.btnStartScan);
        progressBar = headerView.findViewById(R.id.progressBar);

        btnStartScan.setOnClickListener(v -> {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Confirm Scan")
                    .setMessage("Auto-scan old and missing messages")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        startSmsScan(); // your existing method
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
//        (this,R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController,appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);


//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

    }
    private void startSmsScan() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please grant SMS permission to scan", Toast.LENGTH_SHORT).show();
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_SMS}, 123);
            return;
        }
        btnStartScan.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        OneTimeWorkRequest scanRequest = new OneTimeWorkRequest.Builder(SmsScanWorker.class)
                .build();

        scanWorkId = scanRequest.getId();
        WorkManager.getInstance(this).enqueue(scanRequest);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(scanWorkId)
                .observe(this, workInfo -> {
                    if (workInfo != null) {
                        if (workInfo.getState() == WorkInfo.State.SUCCEEDED) {
                            progressBar.setVisibility(View.GONE);
                            btnStartScan.setEnabled(true);
                            Toast.makeText(this, "Scan Complete", Toast.LENGTH_SHORT).show();
                        } else if (workInfo.getState() == WorkInfo.State.FAILED) {
                            progressBar.setVisibility(View.GONE);
                            btnStartScan.setEnabled(true);
                            Toast.makeText(this, "Scan Failed", Toast.LENGTH_SHORT).show();
                        } else if (workInfo.getState() == WorkInfo.State.RUNNING) {
                            // Optional: update progress here if set
                        }
                    }
                });
    }
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.nav_host_fragment);
//        NavController navController = navHostFragment.getNavController();
//        return navController.navigateUp(navController,appBarConfiguration) || super.onSupportNavigateUp();
    }

}