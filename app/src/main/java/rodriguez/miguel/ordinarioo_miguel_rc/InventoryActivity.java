package rodriguez.miguel.ordinarioo_miguel_rc;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.ListView;
import java.io.ByteArrayOutputStream;
import java.util.List;


public class InventoryActivity extends AppCompatActivity {


    private DatabaseHelper dbHelper;
    private Spinner categorySpinner;
    private Button addButton, deleteButton, updateButton, viewButton;
    private ListView productListView;
    private List<Product> productList;
    private ProductAdapter productAdapter;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView productImageView;
    private byte[] selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        dbHelper = new DatabaseHelper(this);

        categorySpinner = findViewById(R.id.categorySpinner);
        addButton = findViewById(R.id.addButton);
        deleteButton = findViewById(R.id.deleteButton);
        updateButton = findViewById(R.id.updateButton);
        viewButton = findViewById(R.id.viewButton);
        productListView = findViewById(R.id.productListView);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteProductDialog();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUpdateProductDialog();
            }
        });

        viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProducts();
            }
        });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.productNameEditText);
        EditText priceEditText = dialogView.findViewById(R.id.productPriceEditText);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        productImageView = dialogView.findViewById(R.id.productImageView);
        Button selectImageButton = dialogView.findViewById(R.id.selectImageButton);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                String name = nameEditText.getText().toString();
                String priceStr = priceEditText.getText().toString();
                String category = categorySpinner.getSelectedItem().toString();

                if (name.isEmpty() || priceStr.isEmpty() || selectedImage == null) {
                    Toast.makeText(InventoryActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price = Double.parseDouble(priceStr);
                dbHelper.insertProduct(name, price, category, selectedImage);
                Toast.makeText(InventoryActivity.this, "Producto agregado", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_delete_product, null);
        builder.setView(dialogView);

        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        Spinner productSpinner = dialogView.findViewById(R.id.productSpinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = categorySpinner.getSelectedItem().toString();
                productList = dbHelper.getProductsByCategory(category);
                ArrayAdapter<Product> productAdapter = new ArrayAdapter<>(InventoryActivity.this, android.R.layout.simple_spinner_item, productList);
                productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productSpinner.setAdapter(productAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Product selectedProduct = (Product) productSpinner.getSelectedItem();
                if (selectedProduct != null) {
                    dbHelper.deleteProduct(selectedProduct.getId());
                    Toast.makeText(InventoryActivity.this, "Producto eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InventoryActivity.this, "Seleccione un producto para eliminar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUpdateProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_update_product, null);
        builder.setView(dialogView);

        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        Spinner productSpinner = dialogView.findViewById(R.id.productSpinner);
        EditText priceEditText = dialogView.findViewById(R.id.productPriceEditText);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = categorySpinner.getSelectedItem().toString();
                productList = dbHelper.getProductsByCategory(category);
                ArrayAdapter<Product> productAdapter = new ArrayAdapter<>(InventoryActivity.this, android.R.layout.simple_spinner_item, productList);
                productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                productSpinner.setAdapter(productAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Product selectedProduct = (Product) productSpinner.getSelectedItem();
                if (selectedProduct != null) {
                    String priceStr = priceEditText.getText().toString();
                    if (!priceStr.isEmpty()) {
                        double price = Double.parseDouble(priceStr);
                        dbHelper.updateProductPrice(selectedProduct.getId(), price);
                        Toast.makeText(InventoryActivity.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(InventoryActivity.this, "Ingrese un nuevo precio", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(InventoryActivity.this, "Seleccione un producto para actualizar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showProducts() {
        String category = categorySpinner.getSelectedItem().toString();
        productList = dbHelper.getProductsByCategory(category);
        productAdapter = new ProductAdapter(this, productList);
        productListView.setAdapter(productAdapter);
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                productImageView.setImageBitmap(bitmap);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                selectedImage = baos.toByteArray();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}