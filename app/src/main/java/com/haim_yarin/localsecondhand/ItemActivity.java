package com.haim_yarin.localsecondhand;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ItemActivity extends AppCompatActivity {

    private TextView title;
    private TextView discription;
    private TextView price;
    private TextView name;
    private TextView email;
    private TextView phone;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);

        price = (TextView)findViewById(R.id.activity_item_txtPrice);
        title = (TextView)findViewById(R.id.activity_item_txtTitle);
        discription = (TextView)findViewById(R.id.activity_item_txtDiscription);
        name = (TextView)findViewById(R.id.activity_item_txtName);
        email = (TextView)findViewById(R.id.activity_item_txtEmail);
        phone = (TextView)findViewById(R.id.activity_item_txtPhone);
        image = (ImageView) findViewById(R.id.activity_item_img);

        price.setText(getIntent().getExtras().getString("price"));
        title.setText(getIntent().getExtras().getString("title"));
        discription.setText(getIntent().getExtras().getString("discription"));
        name.setText(getIntent().getExtras().getString("name"));
        email.setText(getIntent().getExtras().getString("email"));
        phone.setText(getIntent().getExtras().getString("phone"));
        Picasso.with(this).load(getIntent().getExtras().getString("image"))
                .fit()
                .into(image);
    }
}
