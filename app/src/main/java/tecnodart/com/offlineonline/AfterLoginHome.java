package tecnodart.com.offlineonline;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.messaging.FirebaseMessaging;


public class AfterLoginHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, NotificationsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FirebaseMessaging.getInstance().subscribeToTopic("pushNotifications");


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        displaySelectedItem(R.id.home_fragment);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.after_login_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displaySelectedItem(id);

        return true;
    }


    public void displaySelectedItem(int id){
        Fragment f = null;

        if (id == R.id.home_fragment) {

            f = new Home();

        } else if (id == R.id.minimum_prize) {
            f = new MinimumSupportPrice();

        } else if (id == R.id.e_mandi) {
            startActivity(new Intent(AfterLoginHome.this , MarketPriceDetails.class));

        } else if (id == R.id.share) {

            try {
                Intent sh = new Intent(Intent.ACTION_SEND);
                sh.setType("text/plain");
                sh.putExtra(Intent.EXTRA_SUBJECT, "Ubi Quotes");
                String sAux = "\nWe invite you to join Ubi Quotes\nDownload and Install Ubi Quotes\n";
                sAux = sAux + "  \nClick below link to download Ubi Quotes App \n " +
                        "https://drive.google.com/open?id=1aTHwV78iFO_xRn_GTAccpdAkucS5FzCO";
                sh.putExtra(Intent.EXTRA_TEXT, sAux);
                startActivity(Intent.createChooser(sh, "choose one"));
            } catch(Exception e) {
                //e.toString();
            }
        } else if (id == R.id.about_fragment) {

        }
        else if(id==R.id.notifications)
        {
            f=new NotificationsFragment();
        }


        if (f != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_after_login_home, f);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    public void onFragmentInteraction(Uri uri)
    {

    }
}
