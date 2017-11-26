package com.example.ledsoon.ghostssip;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class MainActivity extends AppCompatActivity {

    private TextView title;
    private EditText newName;
    private SpaceNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.title_layout);
        title = getSupportActionBar().getCustomView().findViewById(R.id.title);
        bottomMenu = findViewById(R.id.bottomMenu);
        bottomMenu.initWithSaveInstanceState(savedInstanceState);
        setUpBottomMenu();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        bottomMenu.onSaveInstanceState(outState);
    }

    private void setUpBottomMenu() {
        bottomMenu.addSpaceItem(new SpaceItem("MESSAGES", R.drawable.ic_question_answer_black_24dp));
        bottomMenu.addSpaceItem(new SpaceItem("MAP", R.drawable.ic_map_black_24dp));
        bottomMenu.setCentreButtonIconColorFilterEnabled(false);
        bottomMenu.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
            }
        });
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
                                Toast.makeText(MainActivity.this, R.string.name_changed_to + newName.getText().toString(), Toast.LENGTH_SHORT).show();
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
