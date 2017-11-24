package com.example.ledsoon.ghostssip;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private EditText newName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_layout);
        title = getSupportActionBar().getCustomView().findViewById(R.id.title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_edit:
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View dialogHeaderView = layoutInflater.inflate(R.layout.dialog_header_layout, null, false);
                newName = dialogHeaderView.findViewById(R.id.newName);
                newName.setText(title.getText());
                newName.setSelection(newName.getText().length());

                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                        .setHeaderView(dialogHeaderView)
                        .addButton(getString(R.string.save), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
                            if(newName.getText().length() <= 0){
                                Toast.makeText(MainActivity.this, R.string.name_cant_be_empty, Toast.LENGTH_SHORT).show();
                            }else {
                                title.setText(newName.getText().toString());
                                Toast.makeText(MainActivity.this, R.string.name_saved, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                builder.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
