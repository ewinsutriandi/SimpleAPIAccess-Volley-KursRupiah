package simple.example.kursrupiah;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btnUSD,btnEUR,btnJPY,btnSGD,btnAUD;
    TextView txMataUang,txNilaiTukar,txTanggalKurs;
    FloatingActionButton btnRefresh;
    View lyCurrency;
    ProgressBar loadingIndicator;
    private String mataUang = "USD";
    JSONObject kursTerbaru;
    DecimalFormat formatUang;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inisialisasiView();
        siapkanFormatUang();
        ambilDataKurs();
    }

    private void siapkanFormatUang() {
        formatUang = new DecimalFormat("#,###.00");
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setGroupingSeparator('.');
        s.setDecimalSeparator(',');
        formatUang.setDecimalFormatSymbols(s);
    }

    private void inisialisasiView() {
        txMataUang = findViewById(R.id.txMataUang);
        txNilaiTukar = findViewById(R.id.txNilaiTukarIDR);
        txTanggalKurs = findViewById(R.id.tglKurs);
        lyCurrency = findViewById(R.id.lyCurrency);
        loadingIndicator = findViewById(R.id.loadingIndicator);

        btnAUD = findViewById(R.id.btnAUD);
        btnAUD.setOnClickListener(view -> tampilkanDataKurs("AUD"));

        btnEUR = findViewById(R.id.btnEUR);
        btnEUR.setOnClickListener(view -> tampilkanDataKurs("EUR"));

        btnJPY = findViewById(R.id.btnJPY);
        btnJPY.setOnClickListener(view -> tampilkanDataKurs("JPY"));

        btnSGD = findViewById(R.id.btnSGD);
        btnSGD.setOnClickListener(view -> tampilkanDataKurs("SGD"));

        btnUSD = findViewById(R.id.btnUSD);
        btnUSD.setOnClickListener(view -> tampilkanDataKurs("USD"));

        btnRefresh = findViewById(R.id.btnRefresh);
        btnRefresh.setOnClickListener(view -> ambilDataKurs());
    }

    private void ambilDataKurs() {
        loadingIndicator.setVisibility(View.VISIBLE);
        String baseURL = "https://api.exchangeratesapi.io/latest?base=IDR";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, baseURL, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("MAIN",response.toString());
                        kursTerbaru = response;
                        tampilkanDataKurs(mataUang);
                        loadingIndicator.setVisibility(View.INVISIBLE);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadingIndicator.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(),"Gagal mengambil data",Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    private void tampilkanDataKurs(String mataUang) {
        this.mataUang = mataUang;
        // tampilkan mata uang terpilih
        txMataUang.setText("1 "+mataUang);
        try { // try catch untuk antisipasi error saat parsing JSON
            // tampilkan tanggal
            txTanggalKurs.setText(kursTerbaru.getString("date"));
            // siapkan kemudian tampilkan nilai tukar
            JSONObject rates = kursTerbaru.getJSONObject("rates");
            double nilaiTukar = 1 / rates.getDouble(mataUang);
            txNilaiTukar.setText(formatUang.format(nilaiTukar)+" IDR");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}