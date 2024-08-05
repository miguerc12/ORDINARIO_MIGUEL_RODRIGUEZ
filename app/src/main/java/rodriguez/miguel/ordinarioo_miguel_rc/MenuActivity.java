package rodriguez.miguel.ordinarioo_miguel_rc;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity {


    private Button inventoryButton;
    private Button salesButton;
    private Button exitButton;
    private TextView welcomeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        inventoryButton = findViewById(R.id.inventoryButton);
        salesButton = findViewById(R.id.salesButton);
        exitButton = findViewById(R.id.exitButton);
        welcomeTextView = findViewById(R.id.welcomeTextView);


        String username = getIntent().getStringExtra("USERNAME");


        welcomeTextView.setText("BIENVENIDO CAJERO " + username);

        inventoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, InventoryActivity.class);
                startActivity(intent);
            }
        });

        salesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, SalesActivity.class);
                startActivity(intent);
            }
        });

        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}