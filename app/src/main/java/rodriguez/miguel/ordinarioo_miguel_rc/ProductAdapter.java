package rodriguez.miguel.ordinarioo_miguel_rc;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ProductAdapter  extends ArrayAdapter<Product>{

    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Product product = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.productName);
        TextView priceTextView = convertView.findViewById(R.id.productPrice);
        ImageView productImageView = convertView.findViewById(R.id.productImage);

        nameTextView.setText(product.getName());
        priceTextView.setText("$" + product.getPrice());
        Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
        productImageView.setImageBitmap(bitmap);

        return convertView;
    }
}