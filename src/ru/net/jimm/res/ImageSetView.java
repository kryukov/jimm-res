package ru.net.jimm.res;

import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.*;
import android.content.*;
import android.app.*;
import android.view.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

/**
 * Created with IntelliJ IDEA.
 * <p/>
 * Date: 18.02.13 22:45
 *
 * @author vladimir
 */
public class ImageSetView extends LinearLayout implements View.OnClickListener {
    private ImageView image;
    private TextView description;
    private Button installButton;
    private String assertsFolder;

    public ImageSetView(Context context) {
        this(context,null);
    }

    public ImageSetView(Context context, AttributeSet attrs) {
        super(context, attrs);

        ((Activity)getContext())
                .getLayoutInflater()
                .inflate(R.layout.image_set_view, this, true);

        setUpViews();
        setAttrs(attrs);
    }

    private void setUpViews() {
        image = (ImageView)findViewById(R.id.image);
        description = (TextView)findViewById(R.id.description);
        installButton = (Button)findViewById(R.id.install);
        installButton.setOnClickListener(this);
    }

    private void setAttrs(AttributeSet attrs) {
    }

    public void onClick(View v) {
        if (v == installButton) {
            install();
        }
    }

    public void set(String assertsFolder) throws IOException {
        this.assertsFolder = assertsFolder;
        this.image.setImageDrawable(getPreview());
        this.description.setText(getDescription());
    }
    private Drawable getPreview() throws IOException {
        InputStream ims = getContext().getAssets().open(assertsFolder + "/screenshot.png");
        return Drawable.createFromStream(ims, null);
    }
    private String getDescription() throws IOException {
        String str = new String(getContent(assertsFolder + "/config.ini"));
        return  String.format("%s\ncount: %s (%s)",
                getValue("caption", str).replaceAll("\"", "").trim(),
                getValue("count", str).trim(),
                getValue("size", str).replaceAll("\"", "").trim());
    }
    private String getValue(String key, String data) {
        key += "=";
        data = data.substring(data.indexOf(key) + key.length());
        return data.substring(0, data.indexOf("\n"));
    }
    private void install() {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction<Boolean>() {
                public Boolean run() throws java.lang.Exception {
                    installIt();
                    return true;
                }
            }, AccessController.getContext());
        } catch (Exception ignored) {
        }
        JimmResourcesActivity.alert(getContext(), "Installing", "Done");
    }

    private void installIt() throws IOException {
        rmdir(new File("/sdcard/jimm-multi/res/smiles"));
        mkdirs("/sdcard/jimm-multi/res/smiles");
        String[] files = getContext().getAssets().list(assertsFolder + "/smiles");
        for (String file : files) {
            copy(file, assertsFolder + "/smiles", "/sdcard/jimm-multi/res/smiles");
        }
        copy("smiles.txt", assertsFolder, "/sdcard/jimm-multi/res/smiles");
    }

    private void copy(String what, String from, String to) throws IOException {
        byte[] content = getContent(from + "/" + what);
        putContent(to + "/" + what, content);
    }

    public static void rmdir(File path) {
        try {
            if (path.exists()) {
                File[] files = path.listFiles();
                for (File file : files) {
                    if (file.isDirectory()) {
                        rmdir(file);
                    } else {
                        file.delete();
                    }
                }
                path.delete();
            }
        } catch (Exception ignored) {
        }
    }

    private void mkdirs(String file) {
        try {
            new File(file).mkdirs();
        } catch (Exception ignored) {
        }
    }

    private void putContent(String file, byte[] content) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(file), false);
        out.write(content);
        out.close();
    }

    private byte[] getContent(String assertsFile) throws IOException {
        InputStream is = getContext().getAssets().open(assertsFile);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        return buffer;
    }
}
