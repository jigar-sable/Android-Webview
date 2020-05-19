package com.sam.webapp;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    WebView webView;
    TextView textView;
    DrawerLayout drawer;
    Button retrybtn;
    AdView adView;
    private InterstitialAd interstitialAd;
    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = findViewById(R.id.webview);
        textView = findViewById(R.id.textview);
        retrybtn = findViewById(R.id.retrybtn);

        checkConnection(); //method to check connection

        //Runtime External storage permission for saving download files
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                Log.d("permission", "permission denied to WRITE_EXTERNAL_STORAGE - requesting it");
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 1);
            }
        }

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront();

        retrybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkConnection();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_Email) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {"Paste Your Mail ID Here"});
            intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            intent.putExtra(Intent.EXTRA_TEXT, "Mail Body");
            startActivity(Intent.createChooser(intent, "Email Via"));
        }
        if (id == R.id.action_Contact) {
            String phone = "Paste Your Contact Number Here";
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            startActivity(intent);
        }
        if (id == R.id.refresh) {
            if (isOnline()){
                webView.loadUrl(webView.getUrl());
        } else {
                Toast.makeText(this, "Can't Connect to Internet.", Toast.LENGTH_SHORT).show();
            }
        }
        if (id == R.id.share){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "Download my new webview app https://play.google.com/store/apps/details?id=com.sam.webapp");
            startActivity(Intent.createChooser(i,"Share Via"));
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //paste your Social Media ID's here.
        String fb = "Paste Your Facebook ID Here";
        String twt = "Paste Your Twitter ID Here";
        String ig = "Paste Your Instagram ID Here";
        String lin = "Paste Your LinkedIn ID Here";
        String amzn = "Paste Your Amazon Affiliate Link Here";
        String flpkrt = "Paste Your Flipkart Affiliate Link Here";
        int id = item.getItemId();
                if (id == R.id.nav_facebook) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/"+fb)));
                } else if (id == R.id.nav_twitter) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/"+twt)));
                } else if (id == R.id.nav_linkedin) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://linkedin.com/"+lin)));
                } else if (id == R.id.nav_insta) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/"+ig)));
                } else if (id == R.id.nav_amazon) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(amzn)));
                } else if (id == R.id.nav_flipkart) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(flpkrt)));
                }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(webView.canGoBack()) {
                webView.goBack();
            }else {
                if(exit) {
                    next();
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "Press again to exit.", Toast.LENGTH_SHORT).show();
                }
                Timer timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        exit=false;
                    }
                },2000);
                exit=true;
            }
        }
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    private void checkConnection() {
        if (isOnline()){
            String url = "https://android.com/"; //Enter your url here
            webView.loadUrl(url);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.setWebViewClient(new WebViewClient());
            webView.setWebViewClient(new Browser_Home());
            webView.setWebChromeClient(new ChromeClient());
            webSettings.setAllowFileAccess(true);
            webSettings.setAppCacheEnabled(true);

            //handle downloading
            webView.setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent,
                                            String contentDisposition, String mimeType,
                                            long contentLength) {
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(url));
                    request.setMimeType(mimeType);
                    String cookies = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("cookie", cookies);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setDescription("Downloading File...");
                    request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(
                                    url, contentDisposition, mimeType));
                    DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
                }});

            adView=findViewById(R.id.adView);
            AdRequest adRequest= new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            interstitialAd=new InterstitialAd(this);
            interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712"); //Enter your adunit ID here
            interstitialAd.loadAd(new AdRequest.Builder().build());
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    finish();
                    interstitialAd.loadAd(new AdRequest.Builder().build());
                }
            });
            textView.setVisibility(View.INVISIBLE);
            retrybtn.setEnabled(false);
            retrybtn.setVisibility(View.INVISIBLE);
        }
        else {
            Toast.makeText(this, "Can't Connect to Internet.", Toast.LENGTH_SHORT).show();
            textView.setText("                      No Connection! \n Please check your internet connection.");
            retrybtn.setEnabled(true);
        }
    }

    public void next(){
        if(interstitialAd.isLoaded()) {
            interstitialAd.show();
        }else {
            finish();
        }
    }

    //fullscreen videos
    private static class Browser_Home extends WebViewClient {
        Browser_Home(){}
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    private class ChromeClient extends WebChromeClient {
        private View customview;
        private WebChromeClient.CustomViewCallback customviewcallback;
        private int originalorientation;
        private int originalsystemvisibility;

        ChromeClient() {}

        public Bitmap getDefaultVideoPoster() {
            if (customview == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout)getWindow().getDecorView()).removeView(this.customview);
            this.customview = null;
            getWindow().getDecorView().setSystemUiVisibility(this.originalsystemvisibility);
            setRequestedOrientation(this.originalorientation);
            this.customviewcallback.onCustomViewHidden();
            this.customviewcallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.customview != null){
                onHideCustomView();
                return;
            }
            this.customview = paramView;
            this.originalsystemvisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.originalorientation = getRequestedOrientation();
            this.customviewcallback = paramCustomViewCallback;
            ((FrameLayout)getWindow().getDecorView()).addView(this.customview, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }
}
