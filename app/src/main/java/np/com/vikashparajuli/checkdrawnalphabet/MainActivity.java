package np.com.vikashparajuli.checkdrawnalphabet;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements DrawingImageView.DrawFinishListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LinearLayout llMain = (LinearLayout) findViewById(R.id.llMain);

        int drawable = R.drawable.a_t_alpha;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);
        DrawingImageView image = new DrawingImageView(this, bitmap);
        image.setImageBitmap(bitmap);

        llMain.addView(image);
    }

    @Override
    public void onDrawFinish() {
        Toast.makeText(getApplicationContext(), "Draw finished", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDrawStop() {
        Log.i("Action: ", "Draw stop");
    }

    @Override
    public void onDrawStart() {
        Log.i("Action: ", "Draw start");
    }
}
