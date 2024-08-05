package rodriguez.miguel.ordinarioo_miguel_rc;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class SalesActivity extends AppCompatActivity {

    private Spinner categorySpinner;
    private ListView productsListView;
    private TextView totalTextView;
    private Button finishButton;
    private DatabaseHelper db;
    private List<Product> productList;
    private double total = 0.0;
    private Map<Product, Integer> selectedProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales);

        db = new DatabaseHelper(this);
        selectedProducts = new HashMap<>();

        categorySpinner = findViewById(R.id.categorySpinner);
        productsListView = findViewById(R.id.productsListView);
        totalTextView = findViewById(R.id.totalTextView);
        finishButton = findViewById(R.id.finishButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = parent.getItemAtPosition(position).toString();
                loadProducts(category);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showReceipt();
            }
        });
    }

    private void loadProducts(String category) {
        if (category.equals("Todas las categorías")) {

            productList = db.getAllProducts();
        } else {

            productList = db.getProductsByCategory(category);
        }

        ProductAdapter adapter = new ProductAdapter(this, productList);
        productsListView.setAdapter(adapter);

        productsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product product = productList.get(position);
                total += product.getPrice();
                totalTextView.setText("Total: $" + total);
                if (selectedProducts.containsKey(product)) {
                    selectedProducts.put(product, selectedProducts.get(product) + 1);
                } else {
                    selectedProducts.put(product, 1);
                }
            }
        });
    }

    private void showReceipt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        StringBuilder receipt = new StringBuilder();
        for (Map.Entry<Product, Integer> entry : selectedProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            receipt.append(product.getName())
                    .append(" - $")
                    .append(product.getPrice())
                    .append(" x ")
                    .append(quantity)
                    .append("\n");
        }
        receipt.append("\nTotal: $").append(total);

        builder.setTitle("Recibo")
                .setMessage(receipt.toString())
                .setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                        total = 0.0;
                        totalTextView.setText("Total: $0.00");
                        selectedProducts.clear();

                        loadProducts(categorySpinner.getSelectedItem().toString());
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}