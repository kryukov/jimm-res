package ru.net.jimm.res;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;

public class JimmResourcesActivity extends Activity implements View.OnClickListener {
    private LinearLayout items;
    private Button uninstall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        items = (LinearLayout)findViewById(R.id.resources);
        uninstall = (Button)findViewById(R.id.uninstall);
        uninstall.setOnClickListener(this);
        addSmiles();
    }
    private void addSmiles() {
        try {
            AssetManager dirs = getAssets();
            String[] smiles = dirs.list("");
            for (String smile : smiles) {
                addSmile(smile);
            }
        } catch (Exception ignored) {
        }
    }

    private void addSmile(String assertsFolder) {
        try {
            ImageSetView resItem = new ImageSetView(this);
            resItem.set(assertsFolder);
            items.addView(resItem);
        } catch (IOException ignored) {
        }
    }

    @Override
    public void onClick(View view) {
        ImageSetView.rmdir(new File("/sdcard/jimm-multi/res/smiles"));
        alert(this, "Uninstalling", "Done");
    }

    public static void alert(Context c, String title, String text) {
        AlertDialog alertDialog = new AlertDialog.Builder(c).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(text);
        alertDialog.show();

    }
}
