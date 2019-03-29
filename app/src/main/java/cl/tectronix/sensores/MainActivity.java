package cl.tectronix.sensores;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private int cont = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.tab_bottom);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.actionBluetooth:
                        changeFragment(new BluetoothFragment(),menuItem.getTitle().toString());
                        break;
                    case R.id.actionMonitor:
                        changeFragment(new MonitorFragment(),menuItem.getTitle().toString());
                        break;
                }

                return true;
            }
        });
    }

    private void changeFragment(Fragment f, String title) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,f).commit();
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (cont == 0){
            Toast.makeText(getApplicationContext(),"Presione nuevamente para salir",Toast.LENGTH_LONG).show();
            cont++;
        }else {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        new CountDownTimer(3000,1000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                cont = 0;
            }
        }.start();

        super.onBackPressed();
    }
}
