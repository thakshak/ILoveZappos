package com.example.android.ilovezappos;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.ilovezappos.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String SEARCH_TERM = "sTerm";
    Product p;
    ActivityMainBinding binding;
    StringBuffer link;
    String searchTerm;
    private Intent shareIntent;
    private ShareActionProvider mShareActionProvider;

    @BindingAdapter({"bind:imageUrl", "bind:error"})
    public static void loadImage(ImageView view, String url, Drawable error) {
        Picasso.with(view.getContext()).load(url).error(error).into(view);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            searchTerm = savedInstanceState.getString(SEARCH_TERM, "");
            if (!searchTerm.equals(""))
                searchProduct(searchTerm, "");
        }
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Intent intent = getIntent();
        if (intent != null && Intent.ACTION_VIEW.equals(intent.getAction())) {

            String a[] = intent.getData().getPath().split("(\\=)|(\\&)");
            searchProduct(a[1], a[3]);
        }
    }

    void displayProduct() {
        TextView tView = (TextView) findViewById(R.id.OriginalPrice);
        tView.setPaintFlags(tView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        ScrollView scrollView = (ScrollView) MainActivity.this.findViewById(R.id.my_product_page);
        scrollView.setVisibility(View.VISIBLE);
    }

    void noProducts() {
        Toast.makeText(MainActivity.this, "No Products Found",
                Toast.LENGTH_SHORT).show();
    }

    void searchProduct(String s, final String styleID) {
        searchTerm = s;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.zappos.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // prepare call in Retrofit 2.0
        ZapposAPI zapposAPI = retrofit.create(ZapposAPI.class);
        Call<SearchResult> call = zapposAPI.getSearchResults(s, "b743e26728e16b81da139182bb2094357c31d331");

        //asynchronous call
        call.enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                if (response.body().getResults().size() == 0) {
                    noProducts();
                    return;
                }
                p = response.body().getResults().get(0);
                if (!styleID.equals("")) {
                    for (Product product : response.body().getResults()) {
                        if (styleID.equals(product.getStyleId())) {
                            p = product;
                            break;
                        }
                    }
                }
                if (p == null) {
                    noProducts();
                    return;
                }
                try {
                    binding.setP(p);
                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                }
                link = new StringBuffer("productID=" + p.getProductId() + "&styleID=" + p.getStyleId());
                mShareActionProvider.setShareIntent(createShareIntent());
                displayProduct();
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {
                Log.d("onFailure", t.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
        mShareActionProvider.setShareIntent(createShareIntent());
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                searchProduct(s, "");
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SEARCH_TERM, searchTerm);
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "http://www.mydemosharinglink.com/" + link);
        return shareIntent;
    }

    public void animateFab(View view) {
        if (p == null) return;
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.cartButton);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 0, -45)
                        .setDuration(30),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, -45, 45)
                        .setDuration(60),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 45, 40)
                        .setDuration(30),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 0, -45)
                        .setDuration(30),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, -45, 45)
                        .setDuration(60),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 45, 0)
                        .setDuration(30),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 0, -45)
                        .setDuration(60),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, -45, 45)
                        .setDuration(120),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 45, 0)
                        .setDuration(60),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 0, -45)
                        .setDuration(90),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, -45, 45)
                        .setDuration(180),
                ObjectAnimator.ofFloat(fab, View.ROTATION_Y, 45, 0)
                        .setDuration(90)
        );

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_shopping_cart_black_24dp));
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                fab.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_add_shopping_cart_black_24dp));
            }
        });
        set.start();

    }
}
